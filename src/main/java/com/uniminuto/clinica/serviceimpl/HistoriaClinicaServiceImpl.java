package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.BusinessRuleException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.HistoriaClinicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.HistoriaClinicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Implementación del servicio de historias clínicas. Reglas de negocio:
 * existencia de cliente, mascota, veterinario y cita; la mascota debe pertenecer
 * al cliente; la cita debe pertenecer a la mascota y estar atendida; y una cita
 * solo puede tener una historia clínica.
 */
@Service
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    private static final String ESTADO_ABIERTA = "ABIERTA";
    private static final Set<String> ESTADOS = Set.of(ESTADO_ABIERTA, "CERRADA");
    private static final String ESTADO_CITA_ATENDIDA = "ATENDIDA";

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private CitaRepository citaRepository;

    /** {@inheritDoc} */
    @Override
    public Page<HistoriaMedica> listarHistorias(String texto, String estado, Long clienteId,
                                                Integer mascotaId, Long medicoId,
                                                LocalDateTime desde, LocalDateTime hasta,
                                                Pageable pageable) throws BadRequestException {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha de fin");
        }
        String estadoFiltro = null;
        if (estado != null && !estado.isBlank()) {
            estadoFiltro = estado.trim().toUpperCase();
            if (!ESTADOS.contains(estadoFiltro)) {
                throw new BadRequestException("Estado de historia no valido. Use: ABIERTA, CERRADA");
            }
        }
        String textoFiltro = texto == null ? "" : texto.trim();
        return historiaMedicaRepository.buscar(estadoFiltro, clienteId, mascotaId, medicoId,
                desde, hasta, textoFiltro, pageable);
    }

    /** {@inheritDoc} */
    @Override
    public HistoriaMedica consultarHistoria(Long id) {
        return historiaMedicaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La historia clinica no existe"));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public MiRespuestaRS crearHistoria(HistoriaClinicaRq rq) throws BadRequestException {
        this.validarCamposObligatorios(rq);

        Cliente cliente = clienteRepository.findById(rq.getClienteId())
                .orElseThrow(() -> new NotFoundException("El cliente no existe"));
        Mascota mascota = mascotaRepository.findById(rq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("La mascota no existe"));
        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("El veterinario no existe"));
        Cita cita = citaRepository.findById(rq.getCitaId())
                .orElseThrow(() -> new NotFoundException("La cita no existe"));

        if (mascota.getCliente() == null || !mascota.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessRuleException("La mascota no pertenece al cliente seleccionado");
        }
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BusinessRuleException("La cita seleccionada pertenece a otra mascota");
        }
        if (!ESTADO_CITA_ATENDIDA.equals(cita.getEstado())) {
            throw new BusinessRuleException("Solo una cita atendida puede generar una historia clinica");
        }
        if (historiaMedicaRepository.existsByCita(cita)) {
            throw new ConflictException("La cita ya tiene una historia clinica asociada");
        }

        HistoriaMedica historia = new HistoriaMedica();
        historia.setCliente(cliente);
        historia.setMascota(mascota);
        historia.setMedico(medico);
        historia.setCita(cita);
        historia.setFechaHora(rq.getFechaHora() != null ? rq.getFechaHora() : cita.getFechaHora());
        historia.setMotivo(rq.getMotivo().trim());
        historia.setDiagnostico(rq.getDiagnostico().trim());
        historia.setObservaciones(rq.getObservaciones());
        historia.setEstado(ESTADO_ABIERTA);
        historia.setFechaCreacion(LocalDateTime.now());
        historiaMedicaRepository.save(historia);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(201);
        rta.setMessage("Se registro la historia clinica de la mascota " + mascota.getNombreMascota());
        return rta;
    }

    /**
     * Verifica que los campos obligatorios vengan diligenciados.
     *
     * @param rq datos recibidos.
     * @throws BadRequestException si falta algún campo.
     */
    private void validarCamposObligatorios(HistoriaClinicaRq rq) throws BadRequestException {
        if (rq == null) {
            throw new BadRequestException("La historia clinica no puede ser nula");
        }
        if (rq.getClienteId() == null) {
            throw new BadRequestException("El cliente es obligatorio");
        }
        if (rq.getMascotaId() == null) {
            throw new BadRequestException("La mascota es obligatoria");
        }
        if (rq.getCitaId() == null) {
            throw new BadRequestException("La cita es obligatoria");
        }
        if (rq.getMedicoId() == null) {
            throw new BadRequestException("El veterinario es obligatorio");
        }
        if (rq.getMotivo() == null || rq.getMotivo().isBlank()) {
            throw new BadRequestException("El motivo de la consulta es obligatorio");
        }
        if (rq.getDiagnostico() == null || rq.getDiagnostico().isBlank()) {
            throw new BadRequestException("El diagnostico es obligatorio");
        }
    }
}