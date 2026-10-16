package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.service.ClienteService;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import com.uniminuto.clinica.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de las historias clinicas.
 */
@Service
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    /** Repositorio de historias. */
    @Autowired
    private HistoriaMedicaRepository historiaRepository;

    /** Repositorio de medicos. */
    @Autowired
    private MedicoRepository medicoRepository;

    /** Servicio de clientes (para validar que exista). */
    @Autowired
    private ClienteService clienteService;

    /** Servicio de mascotas (para validar que exista). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de citas (para validar que exista). */
    @Autowired
    private CitaService citaService;

    @Override
    public List<HistoriaMedicaRs> listarHistorias() {
        List<HistoriaMedicaRs> respuesta = new ArrayList<>();
        for (HistoriaMedica historia : historiaRepository.findAllByOrderByFechaHoraDesc()) {
            respuesta.add(this.convertirHistoria(historia));
        }
        return respuesta;
    }

    @Override
    public HistoriaMedicaRs consultarHistoria(Long historiaId) {
        if (historiaId == null || historiaId <= 0) {
            throw new BadRequestException("El ID de la historia no puede ser nulo o menor que 1");
        }
        Optional<HistoriaMedica> optHistoria = historiaRepository.findById(historiaId);
        if (optHistoria.isEmpty()) {
            throw new BadRequestException("Historia clínica no encontrada");
        }
        return this.convertirHistoria(optHistoria.get());
    }

    @Override
    public MiRespuestaRS guardarHistoria(HistoriaMedicaRq historiaRq) {
        // Paso 1. Validar campos obligatorios
        this.validarObjetoHistoria(historiaRq);

        // Paso 2. Buscar cliente, mascota, cita y medico (cada uno lanza error si no existe)
        clienteService.consultarCliente(historiaRq.getClienteId());
        Mascota mascota = mascotaService.consultarMascota(historiaRq.getMascotaId());
        Cita cita = citaService.buscarCita(historiaRq.getCitaId());
        Optional<Medico> optMedico = medicoRepository.findById(historiaRq.getMedicoId());
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }

        // Paso 3. Reglas de negocio
        if (!mascota.getCliente().getClienteId().equals(historiaRq.getClienteId())) {
            throw new BadRequestException("La mascota no pertenece al cliente seleccionado");
        }
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La cita seleccionada pertenece a otra mascota");
        }
        if (!cita.getEstado().equals("atendida")) {
            throw new BadRequestException("Solo se puede crear la historia de una cita atendida");
        }
        if (historiaRepository.existsByCitaId(cita.getId())) {
            throw new BadRequestException("Esa cita ya tiene una historia clínica registrada");
        }
        if (historiaRq.getFechaHora().isBefore(cita.getFechaHora())) {
            throw new BadRequestException("La fecha de la historia no puede ser anterior a la fecha de la cita");
        }

        // Paso 4. Crear y guardar la historia
        HistoriaMedica historia = new HistoriaMedica();
        historia.setMascota(mascota);
        historia.setCita(cita);
        historia.setMedico(optMedico.get());
        historia.setFechaHora(historiaRq.getFechaHora());
        historia.setMotivo(historiaRq.getMotivo());
        historia.setDiagnostico(historiaRq.getDiagnostico());
        historia.setObservaciones(historiaRq.getObservaciones());
        historia.setFechaCreacion(LocalDateTime.now());
        historiaRepository.save(historia);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Historia clínica guardada correctamente");
        return rta;
    }

    /**
     * Convierte la entidad al objeto de respuesta.
     * La cita y el medico pueden venir nulos en historias viejas.
     *
     * @param historia entidad.
     * @return objeto de respuesta.
     */
    private HistoriaMedicaRs convertirHistoria(HistoriaMedica historia) {
        HistoriaMedicaRs rs = new HistoriaMedicaRs();
        rs.setId(historia.getId());
        rs.setMascotaId(historia.getMascota().getMascotaId());
        rs.setMascotaNombre(historia.getMascota().getNombreMascota());
        rs.setClienteId(historia.getMascota().getCliente().getClienteId());
        rs.setClienteNombre(historia.getMascota().getCliente().getNombres() + " "
                + historia.getMascota().getCliente().getApellidos());
        if (historia.getCita() != null) {
            rs.setCitaId(historia.getCita().getId());
            rs.setEstado(historia.getCita().getEstado());
        }
        if (historia.getMedico() != null) {
            rs.setMedicoId(historia.getMedico().getId());
            rs.setMedicoNombre(historia.getMedico().getNombres() + " " + historia.getMedico().getApellidos());
        }
        rs.setFechaHora(historia.getFechaHora());
        rs.setMotivo(historia.getMotivo());
        rs.setDiagnostico(historia.getDiagnostico());
        rs.setObservaciones(historia.getObservaciones());
        return rs;
    }

    /**
     * Valida los campos obligatorios de la historia.
     *
     * @param historiaRq datos de la historia.
     */
    private void validarObjetoHistoria(HistoriaMedicaRq historiaRq) {
        if (historiaRq == null) {
            throw new BadRequestException("Los datos de la historia son obligatorios");
        }
        if (historiaRq.getClienteId() == null) {
            throw new BadRequestException("Debe seleccionar un cliente");
        }
        if (historiaRq.getMascotaId() == null) {
            throw new BadRequestException("Debe seleccionar una mascota");
        }
        if (historiaRq.getCitaId() == null) {
            throw new BadRequestException("Debe seleccionar una cita");
        }
        if (historiaRq.getMedicoId() == null) {
            throw new BadRequestException("Debe seleccionar un veterinario");
        }
        if (historiaRq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora son obligatorias");
        }
        if (historiaRq.getMotivo() == null || historiaRq.getMotivo().isBlank()) {
            throw new BadRequestException("El motivo de la consulta es obligatorio");
        }
        if (historiaRq.getDiagnostico() == null || historiaRq.getDiagnostico().isBlank()) {
            throw new BadRequestException("El diagnóstico es obligatorio");
        }
    }
}
