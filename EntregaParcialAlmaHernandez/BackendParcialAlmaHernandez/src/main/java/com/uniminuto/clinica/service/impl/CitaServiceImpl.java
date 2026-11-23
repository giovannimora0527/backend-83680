package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.CitaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios relacionados con las citas.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /**
     * Repositorio de datos para citas.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Repositorio de datos para médicos.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Lista las citas dentro de un rango de fechas, ordenadas
     * desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de citas dentro del rango.
     */
    @Override
    public List<CitaRs> listarCitas(
            LocalDateTime fechaIni, LocalDateTime fechaFin) {

        List<Cita> citas = citaRepository
                .findByFechaHoraBetweenOrderByFechaHoraDesc(
                        fechaIni, fechaFin);

        List<CitaRs> respuestas = new ArrayList<>();

        for (Cita cita : citas) {
            CitaRs respuesta = new CitaRs();

            respuesta.setId(cita.getId());
            respuesta.setClienteId(cita.getClienteId());
            respuesta.setMascotaId(cita.getMascotaId());
            respuesta.setMedicoId(cita.getMedico().getId());
            respuesta.setFechaHora(cita.getFechaHora());
            respuesta.setEstado(cita.getEstado());
            respuesta.setMotivo(cita.getMotivo());

            respuestas.add(respuesta);
        }

        return respuestas;
    }

    /**
     * Guarda una nueva cita en el sistema.
     *
     * @param citaRq información de la cita que se desea guardar.
     * @return respuesta del servicio.
     * @throws BadRequestException si el médico no existe o
     *         si existe un conflicto de horario.
     */
    @Override
    public RespuestaRs guardarCita(CitaRq citaRq)
            throws BadRequestException {

        Optional<Medico> optMedico = this.medicoRepository
                .findById(citaRq.getMedicoId());

        if (optMedico.isEmpty()) {
            throw new BadRequestException(
                    "El médico con ID " + citaRq.getMedicoId()
                            + " no existe.");
        }
        if (!Boolean.TRUE.equals(optMedico.get().getActivo())) {
            throw new BadRequestException("No se pueden asignar citas a médicos inactivos.");
        }

        LocalDateTime fechaInicio = LocalDateTime.parse(
                citaRq.getFechaHora(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime fechaFin = fechaInicio.plusMinutes(20);

        if (existeCruce(optMedico.get(), fechaInicio, fechaFin, null)) {
            throw new BadRequestException(
                    "El médico ya tiene una cita programada en ese horario.");
        }
        if (existeCruceMascota(citaRq.getMascotaId(), fechaInicio, fechaFin, null)) {
            throw new BadRequestException(
                    "La mascota ya tiene una cita simultánea.");
        }

        Cita citaNueva = this.converterToCita(
                citaRq, optMedico.get());

        this.citaRepository.save(citaNueva);

        RespuestaRs rta = new RespuestaRs();
        rta.setStatus(200);
        rta.setMensaje("Cita creada exitosamente.");

        return rta;
    }

    /**
     * Actualiza una cita almacenada en el sistema.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return respuesta del servicio.
     * @throws BadRequestException si la cita no existe, el médico no existe
     *         o existe un conflicto de horario.
     */
    @Override
    public RespuestaRs actualizarCita(
            Long id, CitaRq citaRq)
            throws BadRequestException {

        Optional<Cita> optCita = this.citaRepository.findById(id);

        if (optCita.isEmpty()) {
            throw new BadRequestException(
                    "La cita con ID " + id + " no existe.");
        }

        Optional<Medico> optMedico = this.medicoRepository
                .findById(citaRq.getMedicoId());

        if (optMedico.isEmpty()) {
            throw new BadRequestException(
                    "El médico con ID " + citaRq.getMedicoId()
                            + " no existe.");
        }
        if (!Boolean.TRUE.equals(optMedico.get().getActivo())) {
            throw new BadRequestException("No se pueden asignar citas a médicos inactivos.");
        }

        LocalDateTime fechaInicio = LocalDateTime.parse(
                citaRq.getFechaHora(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        LocalDateTime fechaFin = fechaInicio.plusMinutes(20);

        if (existeCruce(optMedico.get(), fechaInicio, fechaFin, id)) {
            throw new BadRequestException(
                    "El médico ya tiene una cita programada en ese horario.");
        }
        if (existeCruceMascota(citaRq.getMascotaId(), fechaInicio, fechaFin, id)) {
            throw new BadRequestException(
                    "La mascota ya tiene una cita simultánea.");
        }

        Cita cita = optCita.get();

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedico(optMedico.get());
        cita.setFechaHora(fechaInicio);
        cita.setFechaFin(fechaFin);
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());

        this.citaRepository.save(cita);

        RespuestaRs rta = new RespuestaRs();
        rta.setStatus(200);
        rta.setMensaje("Cita actualizada exitosamente.");

        return rta;
    }

    /**
     * Convierte un objeto CitaRq a una entidad Cita.
     *
     * @param citaRq objeto de entrada.
     * @param medico médico de la cita.
     * @return entidad Cita.
     */
    private Cita converterToCita(
            CitaRq citaRq, Medico medico) {

        Cita cita = new Cita();

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());
        cita.setFechaHora(LocalDateTime.parse(
                citaRq.getFechaHora(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        cita.setFechaFin(cita.getFechaHora().plusMinutes(20));
        cita.setMedico(medico);

        return cita;
    }

    private boolean existeCruce(
            Medico medico,
            LocalDateTime inicio,
            LocalDateTime fin,
            Long citaActualId) {
        for (Cita existente : this.citaRepository.findByMedico(medico)) {
            if (citaActualId != null && citaActualId.equals(existente.getId())) {
                continue;
            }
            if ("Cancelada".equalsIgnoreCase(existente.getEstado())) {
                continue;
            }
            LocalDateTime inicioExistente = existente.getFechaHora();
            LocalDateTime finExistente = existente.getFechaFin() != null
                    ? existente.getFechaFin()
                    : inicioExistente.plusMinutes(20);
            if (inicio.isBefore(finExistente) && fin.isAfter(inicioExistente)) {
                return true;
            }
        }
        return false;
    }

    private boolean existeCruceMascota(
            Integer mascotaId,
            LocalDateTime inicio,
            LocalDateTime fin,
            Long citaActualId) {
        for (Cita existente : this.citaRepository.findAll()) {
            if (citaActualId != null && citaActualId.equals(existente.getId())) {
                continue;
            }
            if (!mascotaId.equals(existente.getMascotaId())
                    || "Cancelada".equalsIgnoreCase(existente.getEstado())) {
                continue;
            }
            LocalDateTime inicioExistente = existente.getFechaHora();
            LocalDateTime finExistente = existente.getFechaFin() != null
                    ? existente.getFechaFin()
                    : inicioExistente.plusMinutes(20);
            if (inicio.isBefore(finExistente) && fin.isAfter(inicioExistente)) {
                return true;
            }
        }
        return false;
    }
}