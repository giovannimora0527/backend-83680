package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.model.DespachoConfirmarRq;
import com.uniminuto.clinica.model.DespachoItemRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.service.DespachoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class DespachoServiceImpl implements DespachoService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> listarPendientes() {
        return jdbcTemplate.queryForList(
                "SELECT f.id, f.fecha_creacion_registro AS fechaGeneracion, " +
                "f.dosis, f.indicaciones, f.cantidad_prescrita AS cantidadPrescrita, " +
                "f.estado_despacho AS estadoDespacho, f.medicamento_id AS medicamentoId, " +
                "m.nombre AS medicamento, c.id AS citaId, c.mascota_id AS mascotaId, " +
                "ma.nombre AS mascota, cl.nombres AS propietarioNombres, " +
                "cl.apellidos AS propietarioApellidos, med.nombres AS medicoNombres, " +
                "med.apellidos AS medicoApellidos " +
                "FROM formula_medica_legacy f " +
                "JOIN cita c ON c.id=f.cita_id " +
                "JOIN medicamento m ON m.id=f.medicamento_id " +
                "LEFT JOIN mascota ma ON ma.id=c.mascota_id " +
                "LEFT JOIN cliente cl ON cl.id=ma.propietario_id " +
                "LEFT JOIN medico med ON med.id=c.medico_id " +
                "WHERE f.estado_despacho IN ('PENDIENTE_DESPACHO','EN_PREPARACION','PENDIENTE_POR_DISPONIBILIDAD','DESPACHO_PARCIAL') " +
                "ORDER BY f.fecha_creacion_registro DESC");
    }

    @Override
    @Transactional
    public RespuestaRs confirmar(String username, DespachoConfirmarRq request) throws BadRequestException {
        if (request.getFormulaId() == null || request.getItems() == null || request.getItems().isEmpty()) {
            throw new BadRequestException("Debe indicar fórmula y lotes a entregar.");
        }

        Map<String, Object> formula;
        try {
            formula = jdbcTemplate.queryForMap(
                    "SELECT medicamento_id,cantidad_prescrita,estado_despacho " +
                    "FROM formula_medica_legacy WHERE id=?", request.getFormulaId());
        } catch (org.springframework.dao.EmptyResultDataAccessException ex) {
            throw new BadRequestException("La fórmula no existe.");
        }

        if ("DESPACHADO".equals(formula.get("estado_despacho"))
                || "CANCELADO".equals(formula.get("estado_despacho"))) {
            throw new BadRequestException("La fórmula no admite una nueva entrega.");
        }

        Long formulaMedicamentoId = ((Number) formula.get("medicamento_id")).longValue();
        BigDecimal cantidadPrescrita = (BigDecimal) formula.get("cantidad_prescrita");
        BigDecimal entregada = BigDecimal.ZERO;

        Long usuarioId = jdbcTemplate.queryForObject(
                "SELECT id FROM usuario WHERE username=?",
                Long.class, username);

        Number dispatchCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM despacho_medicamento WHERE formula_legacy_id=?",
                Number.class, request.getFormulaId());

        Long despachoId;
        if (dispatchCount != null && dispatchCount.intValue() == 0) {
            jdbcTemplate.update(
                    "INSERT INTO despacho_medicamento " +
                    "(formula_legacy_id,usuario_despacho_id,estado,fecha_creacion) " +
                    "VALUES (?, ?, 'PREPARACION', NOW())",
                    request.getFormulaId(), usuarioId);
            despachoId = jdbcTemplate.queryForObject(
                    "SELECT id FROM despacho_medicamento WHERE formula_legacy_id=?",
                    Long.class, request.getFormulaId());
        } else {
            despachoId = jdbcTemplate.queryForObject(
                    "SELECT id FROM despacho_medicamento WHERE formula_legacy_id=?",
                    Long.class, request.getFormulaId());
        }

        for (DespachoItemRq item : request.getItems()) {
            if (item.getCantidad() == null || item.getCantidad().signum() <= 0) {
                throw new BadRequestException("La cantidad entregada debe ser mayor a cero.");
            }
            if (item.getMedicamentoId() == null
                    || item.getMedicamentoId().longValue() != formulaMedicamentoId) {
                throw new BadRequestException("El medicamento no corresponde a la fórmula.");
            }

            Map<String, Object> lote = jdbcTemplate.queryForMap(
                    "SELECT id,medicamento_id,cantidad_actual,fecha_vencimiento,activo " +
                    "FROM lote_medicamento WHERE id=?",
                    item.getLoteId());

            if (((Number) lote.get("activo")).intValue() != 1) {
                throw new BadRequestException("El lote no está activo.");
            }
            if (((Number) lote.get("medicamento_id")).intValue() != item.getMedicamentoId()) {
                throw new BadRequestException("El lote no corresponde al medicamento.");
            }
            if (((java.sql.Date) lote.get("fecha_vencimiento")).toLocalDate().isBefore(LocalDate.now())) {
                throw new BadRequestException("No se puede dispensar un medicamento vencido.");
            }

            BigDecimal disponible = (BigDecimal) lote.get("cantidad_actual");
            if (disponible.compareTo(item.getCantidad()) < 0) {
                throw new BadRequestException("El lote seleccionado no tiene existencia suficiente.");
            }
            if (entregada.add(item.getCantidad()).compareTo(cantidadPrescrita) > 0) {
                throw new BadRequestException("La entrega excede la cantidad prescrita.");
            }

            BigDecimal cantidadLote = disponible.subtract(item.getCantidad());
            jdbcTemplate.update(
                    "UPDATE lote_medicamento SET cantidad_actual=? WHERE id=?",
                    cantidadLote, item.getLoteId());

            Number stockActual = jdbcTemplate.queryForObject(
                    "SELECT cantidad_actual FROM medicamento WHERE id=?",
                    Number.class, item.getMedicamentoId());
            BigDecimal nuevoStock = new BigDecimal(stockActual.toString())
                    .subtract(item.getCantidad());
            if (nuevoStock.signum() < 0) {
                throw new BadRequestException("La operación dejaría inventario negativo.");
            }
            jdbcTemplate.update(
                    "UPDATE medicamento SET cantidad_actual=? WHERE id=?",
                    nuevoStock, item.getMedicamentoId());

            jdbcTemplate.update(
                    "INSERT INTO detalle_despacho " +
                    "(despacho_id,medicamento_id,lote_id,cantidad_entregada) " +
                    "VALUES (?,?,?,?)",
                    despachoId, item.getMedicamentoId(), item.getLoteId(), item.getCantidad());

            jdbcTemplate.update(
                    "INSERT INTO movimiento_inventario " +
                    "(medicamento_id,lote_id,tipo_movimiento,cantidad,stock_resultante,usuario_id," +
                    "formula_id,despacho_id,referencia,motivo,fecha_movimiento) " +
                    "VALUES (?, ?, 'SALIDA', ?, ?, ?, ?, ?, ?, 'Dispensación confirmada', NOW())",
                    item.getMedicamentoId(), item.getLoteId(), item.getCantidad(), nuevoStock,
                    usuarioId, request.getFormulaId(), despachoId,
                    "FORMULA-" + request.getFormulaId());

            entregada = entregada.add(item.getCantidad());
        }

        String estadoFormula = entregada.compareTo(cantidadPrescrita) >= 0
                ? "DESPACHADO"
                : entregada.signum() == 0 ? "PENDIENTE_DESPACHO" : "DESPACHO_PARCIAL";
        jdbcTemplate.update(
                "UPDATE formula_medica_legacy SET estado_despacho=? WHERE id=?",
                estadoFormula, request.getFormulaId());
        jdbcTemplate.update(
                "UPDATE despacho_medicamento SET estado=?,fecha_despacho=NOW() WHERE id=?",
                "DESPACHADO".equals(estadoFormula) ? "COMPLETADO" : "PARCIAL",
                despachoId);
        jdbcTemplate.update(
                "INSERT INTO estado_despacho_log " +
                "(formula_id,estado,usuario_id,observacion,fecha_cambio) " +
                "VALUES (?, ?, ?, 'Cambio confirmado mediante dispensación', NOW())",
                request.getFormulaId(), estadoFormula, usuarioId);

        RespuestaRs response = new RespuestaRs();
        response.setStatus(200);
        response.setMensaje("Despacho confirmado y inventario actualizado.");
        return response;
    }
}
