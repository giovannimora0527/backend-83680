package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.FormulaDetalle;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaDetalleRs;
import com.uniminuto.clinica.models.FormulaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.FormulaDetalleRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.util.PdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de la consulta de fórmulas médicas.
 */
@Service
public class FormulaMedicaServiceImpl implements FormulaMedicaService {

    @Autowired
    private FormulaMedicaRepository formulaMedicaRepository;
    @Autowired
    private FormulaDetalleRepository detalleRepository;
    @Autowired
    private InventarioService inventarioService;

    @Override
    public List<FormulaMedicaRs> listarFormulasMedicas() {
        Long medicoId = UsuarioActual.get().getMedicoId();
        return formulaMedicaRepository.findAllByOrderByFechaCreacionRegistroDesc().stream()
                // El medico solo ve las formulas que el genero
                .filter(f -> medicoId == null || (f.getMedico() != null && medicoId.equals(f.getMedico().getId())))
                .map(f -> this.convertir(f, false)).toList();
    }

    @Override
    public FormulaMedicaRs consultarFormula(Long formulaId) {
        return this.convertir(this.buscar(formulaId), true);
    }

    @Override
    public FormulaMedicaRs convertir(FormulaMedica formula, boolean conDetalle) {
        Cita cita = formula.getCita();
        FormulaMedicaRs rs = new FormulaMedicaRs();
        rs.setId(formula.getId());
        rs.setCitaId(cita.getId());
        rs.setCitaFechaHora(cita.getFechaHora());
        rs.setHistoriaId(formula.getHistoriaId());
        rs.setMascotaId(cita.getMascota().getMascotaId());
        rs.setMascotaNombre(cita.getMascota().getNombreMascota());
        if (cita.getMascota().getRaza() != null) {
            rs.setMascotaEspecie(cita.getMascota().getRaza().getEspecie());
        }
        rs.setClienteId(cita.getMascota().getCliente().getClienteId());
        rs.setClienteNombre(cita.getMascota().getCliente().getNombres() + " " + cita.getMascota().getCliente().getApellidos());
        rs.setClienteDocumento(cita.getMascota().getCliente().getTipoDocumento() + " "
                + cita.getMascota().getCliente().getNumeroDocumento());
        if (formula.getMedico() != null) {
            rs.setMedicoId(formula.getMedico().getId());
            rs.setMedicoNombre(formula.getMedico().getNombres() + " " + formula.getMedico().getApellidos());
        }
        rs.setFechaEmision(formula.getFechaEmision());
        rs.setFechaCreacionRegistro(formula.getFechaCreacionRegistro());
        rs.setIndicaciones(formula.getIndicaciones());
        rs.setActivo(formula.getActivo());
        rs.setEstadoDespacho(formula.getEstadoDespacho());

        List<FormulaDetalle> detalles = detalleRepository.findByFormulaIdOrderByIdAsc(formula.getId());
        rs.setMedicamentosResumen(detalles.stream()
                .map(d -> d.getMedicamento().getNombre() + " x" + d.getCantidad()).collect(Collectors.joining(", ")));
        if (conDetalle) {
            List<FormulaDetalleRs> lista = new ArrayList<>();
            for (FormulaDetalle d : detalles) {
                FormulaDetalleRs dr = new FormulaDetalleRs();
                dr.setId(d.getId());
                dr.setMedicamentoId(d.getMedicamento().getId());
                dr.setMedicamentoCodigo(d.getMedicamento().getCodigo());
                dr.setMedicamentoNombre(d.getMedicamento().getNombre());
                dr.setPresentacion(d.getMedicamento().getPresentacion());
                dr.setDosis(d.getDosis());
                dr.setFrecuencia(d.getFrecuencia());
                dr.setDuracionDias(d.getDuracionDias());
                dr.setCantidad(d.getCantidad());
                dr.setViaAdministracion(d.getViaAdministracion());
                dr.setIndicaciones(d.getIndicaciones());
                dr.setCantidadEntregada(d.getCantidadEntregada());
                dr.setStockDisponible(inventarioService.stockDisponible(d.getMedicamento().getId()));
                lista.add(dr);
            }
            rs.setDetalles(lista);
        }
        return rs;
    }

    @Override
    @Transactional
    public MiRespuestaRS inactivarFormula(Long formulaId) {
        FormulaMedica formula = this.buscar(formulaId);
        if (!Boolean.TRUE.equals(formula.getActivo())) {
            throw new BadRequestException("La fórmula ya estaba inactiva");
        }
        boolean yaEntregada = detalleRepository.findByFormulaIdOrderByIdAsc(formulaId).stream()
                .anyMatch(d -> d.getCantidadEntregada() > 0);
        if (yaEntregada) {
            throw new BadRequestException("La fórmula ya tiene medicamentos despachados; no se puede inactivar");
        }
        // No se borra: queda inactiva y el despacho cancelado (trazabilidad)
        formula.setActivo(false);
        formula.setEstadoDespacho("CANCELADO");
        formula.setFechaActualizacionRegistro(LocalDateTime.now());
        formulaMedicaRepository.save(formula);
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Fórmula médica inactivada correctamente");
        return rta;
    }

    @Override
    public byte[] formulaPdf(Long formulaId) {
        FormulaMedicaRs f = this.consultarFormula(formulaId);
        DateTimeFormatter fh = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<List<String>> filas = new ArrayList<>();
        for (FormulaDetalleRs d : f.getDetalles()) {
            filas.add(List.of(d.getMedicamentoNombre() + (d.getPresentacion() == null ? "" : " (" + d.getPresentacion() + ")"),
                    d.getDosis(), d.getFrecuencia(), d.getDuracionDias() + " días", String.valueOf(d.getCantidad()),
                    d.getViaAdministracion(), d.getIndicaciones() == null ? "" : d.getIndicaciones()));
        }
        return new PdfUtil.Documento("Fórmula médica N.° " + f.getId())
                .seccion("Datos de la atención", List.of(
                        new String[]{"Fecha de emisión", String.valueOf(f.getFechaEmision())},
                        new String[]{"Consulta", "#" + f.getCitaId() + " - " + f.getCitaFechaHora().format(fh)},
                        new String[]{"Propietario", f.getClienteNombre()},
                        new String[]{"Documento", f.getClienteDocumento()},
                        new String[]{"Paciente", f.getMascotaNombre() + (f.getMascotaEspecie() == null ? "" : " (" + f.getMascotaEspecie() + ")")},
                        new String[]{"Médico veterinario", f.getMedicoNombre()}))
                .tabla("Medicamentos formulados",
                        List.of("Medicamento", "Dosis", "Frecuencia", "Duración", "Cantidad", "Vía", "Indicaciones"), filas)
                .texto("Indicaciones generales", f.getIndicaciones())
                .texto(null, "\n\n_______________________________\nFirma del médico veterinario: " + f.getMedicoNombre())
                .generar();
    }

    /**
     * Busca una formula o lanza 404.
     */
    private FormulaMedica buscar(Long formulaId) {
        if (formulaId == null || formulaId <= 0) {
            throw new BadRequestException("El ID de la fórmula no puede ser nulo o menor que 1");
        }
        return formulaMedicaRepository.findById(formulaId)
                .orElseThrow(() -> new BadRequestException("Fórmula médica no encontrada", HttpStatus.NOT_FOUND));
    }
}
