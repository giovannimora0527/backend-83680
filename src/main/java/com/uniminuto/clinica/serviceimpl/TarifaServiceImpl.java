package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Tarifa;
import com.uniminuto.clinica.entity.TarifaLog;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.TarifaLogRs;
import com.uniminuto.clinica.models.TarifaRq;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.repository.TarifaLogRepository;
import com.uniminuto.clinica.repository.TarifaRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementacion de tarifas. Cada cambio queda en tarifa_log.
 */
@Service
public class TarifaServiceImpl implements TarifaService {

    /** Tipos de servicio permitidos. */
    private static final List<String> TIPOS = List.of("CONSULTA", "PROCEDIMIENTO", "SERVICIO");

    @Autowired
    private TarifaRepository tarifaRepository;
    @Autowired
    private TarifaLogRepository tarifaLogRepository;
    @Autowired
    private EspecializacionRepository especializacionRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Tarifa> listarTarifas() {
        return tarifaRepository.findAllByOrderByTipoAscNombreAsc();
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarTarifa(TarifaRq rq) {
        this.validar(rq);
        if (tarifaRepository.findByCodigoIgnoreCase(rq.getCodigo().trim()).isPresent()) {
            throw new BadRequestException("Ya existe una tarifa con ese código");
        }
        Tarifa tarifa = new Tarifa();
        this.copiar(rq, tarifa);
        tarifa.setActivo(true);
        tarifa.setFechaCreacion(LocalDateTime.now());
        tarifaRepository.save(tarifa);
        this.log(tarifa, "CREACION", null, null);
        return this.exito("Tarifa registrada");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarTarifa(TarifaRq rq) {
        this.validar(rq);
        Tarifa tarifa = this.buscar(rq.getId());
        Optional<Tarifa> mismoCodigo = tarifaRepository.findByCodigoIgnoreCase(rq.getCodigo().trim());
        if (mismoCodigo.isPresent() && !mismoCodigo.get().getId().equals(tarifa.getId())) {
            throw new BadRequestException("Ya existe otra tarifa con ese código");
        }
        BigDecimal valorAnterior = tarifa.getValor();
        BigDecimal ivaAnterior = tarifa.getIvaPorcentaje();
        this.copiar(rq, tarifa);
        tarifa.setFechaModificacion(LocalDateTime.now());
        tarifaRepository.save(tarifa);
        this.log(tarifa, "ACTUALIZACION", valorAnterior, ivaAnterior);
        return this.exito("Tarifa actualizada. Las facturas anteriores conservan el valor con el que se generaron.");
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstado(Long id, Boolean activo) {
        Tarifa tarifa = this.buscar(id);
        tarifa.setActivo(Boolean.TRUE.equals(activo));
        tarifa.setFechaModificacion(LocalDateTime.now());
        tarifaRepository.save(tarifa);
        this.log(tarifa, Boolean.TRUE.equals(activo) ? "ACTIVACION" : "INACTIVACION", tarifa.getValor(), tarifa.getIvaPorcentaje());
        return this.exito(Boolean.TRUE.equals(activo) ? "Tarifa activada" : "Tarifa inactivada");
    }

    @Override
    public List<TarifaLogRs> listarLog(Long tarifaId) {
        this.buscar(tarifaId);
        Map<Long, String> usuarios = usuarioRepository.findAll().stream()
                .collect(Collectors.toMap(Usuario::getId, Usuario::getUsername));
        return tarifaRepository.findLogs(tarifaId).stream().map(l -> {
            TarifaLogRs rs = new TarifaLogRs();
            rs.setId(l.getId());
            rs.setAccion(l.getAccion());
            rs.setValorAnterior(l.getValorAnterior());
            rs.setValorNuevo(l.getValorNuevo());
            rs.setIvaAnterior(l.getIvaAnterior());
            rs.setIvaNuevo(l.getIvaNuevo());
            rs.setUsuario(usuarios.get(l.getUsuarioId()));
            rs.setFecha(l.getFecha());
            return rs;
        }).toList();
    }

    @Override
    public Tarifa buscarActiva(Long id) {
        Tarifa tarifa = this.buscar(id);
        if (!Boolean.TRUE.equals(tarifa.getActivo())) {
            throw new BadRequestException("La tarifa " + tarifa.getNombre() + " está inactiva");
        }
        return tarifa;
    }

    /**
     * Guarda una linea en el log de tarifas.
     */
    private void log(Tarifa tarifa, String accion, BigDecimal valorAnterior, BigDecimal ivaAnterior) {
        TarifaLog log = new TarifaLog();
        log.setTarifaId(tarifa.getId());
        log.setAccion(accion);
        log.setValorAnterior(valorAnterior);
        log.setValorNuevo(tarifa.getValor());
        log.setIvaAnterior(ivaAnterior);
        log.setIvaNuevo(tarifa.getIvaPorcentaje());
        log.setUsuarioId(UsuarioActual.get().getUsuarioId());
        log.setFecha(LocalDateTime.now());
        tarifaLogRepository.save(log);
    }

    private void copiar(TarifaRq rq, Tarifa tarifa) {
        tarifa.setCodigo(rq.getCodigo().trim().toUpperCase());
        tarifa.setNombre(rq.getNombre().trim());
        tarifa.setTipo(rq.getTipo().toUpperCase());
        tarifa.setValor(rq.getValor());
        tarifa.setIvaPorcentaje(rq.getIvaPorcentaje() == null ? BigDecimal.ZERO : rq.getIvaPorcentaje());
        Especializacion especializacion = null;
        if (rq.getEspecializacionId() != null) {
            especializacion = especializacionRepository.findById(rq.getEspecializacionId())
                    .orElseThrow(() -> new BadRequestException("Especialidad no encontrada"));
        }
        tarifa.setEspecializacion(especializacion);
    }

    private void validar(TarifaRq rq) {
        if (rq == null || rq.getCodigo() == null || !rq.getCodigo().trim().matches("[A-Za-z0-9_-]{2,20}")) {
            throw new BadRequestException("El código debe tener entre 2 y 20 caracteres (letras, números, _ o -)");
        }
        if (rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del servicio es obligatorio");
        }
        if (rq.getTipo() == null || !TIPOS.contains(rq.getTipo().toUpperCase())) {
            throw new BadRequestException("El tipo debe ser CONSULTA, PROCEDIMIENTO o SERVICIO");
        }
        if (rq.getValor() == null || rq.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El valor debe ser mayor a cero");
        }
        if (rq.getIvaPorcentaje() != null && (rq.getIvaPorcentaje().signum() < 0
                || rq.getIvaPorcentaje().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new BadRequestException("El IVA debe estar entre 0 y 100");
        }
    }

    private Tarifa buscar(Long id) {
        if (id == null) {
            throw new BadRequestException("El id de la tarifa es obligatorio");
        }
        return tarifaRepository.findById(id).orElseThrow(() -> new BadRequestException("Tarifa no encontrada"));
    }

    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
