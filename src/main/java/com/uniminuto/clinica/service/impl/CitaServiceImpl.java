package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Paciente;
import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

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

    @Override
    public List<Cita> listarCitas() {
        return citaRepository.findAllByOrderByFechaHoraDesc();
    }

    /**
    * Lista las citas dentro de un rango de fechas.
    *
    * @param fechaIni fecha inicial de la consulta.
    * @param fechaFin fecha final de la consulta.
    * @return lista de citas ordenadas de la más reciente a la más antigua.
    */
    @Override
    public List<Cita> listarCitasPorFecha(LocalDateTime fechaIni, LocalDateTime fechaFin) {
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaIni, fechaFin);
}
  /**
 * Guarda una nueva cita en el sistema.
 *
 * @param citaRq información de la cita a guardar.
 * @return respuesta del proceso.
 * @throws BadRequestException si el médico no existe o hay conflicto de horario.
 */
@Override
public RespuestaRs guardarCita(CitaRq citaRq) throws BadRequestException {

    Optional<Medico> optMedico =
            this.medicoRepository.findById(citaRq.getMedicoId());

    if (optMedico.isEmpty()) {
        throw new BadRequestException(
                "El médico con ID " + citaRq.getMedicoId() + " no existe.");
    }

    LocalDateTime fechaInicio = LocalDateTime.parse(
            citaRq.getFechaHora(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    LocalDateTime fechaFin = fechaInicio.plusMinutes(20);

    List<Cita> citasDelMedico = this.citaRepository
            .findByMedicoAndFechaHoraBetween(
                    optMedico.get(), fechaInicio, fechaFin);

    if (!citasDelMedico.isEmpty()) {
        throw new BadRequestException(
                "El médico ya tiene una cita programada en ese horario.");
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
     * Actualiza una cita existente en el sistema.
     *
     * @param citaRq información actualizada de la cita.
     * @return respuesta del proceso.
     * @throws BadRequestException si la cita no existe o los datos relacionados
     * no son válidos.
     */
    @Override
    public RespuestaRs actualizarCita(CitaRq citaRq) throws BadRequestException {

        if (citaRq.getId() == null) {
            throw new BadRequestException(
                    "El identificador de la cita es obligatorio.");
        }

        Optional<Cita> optCita =
                this.citaRepository.findById(citaRq.getId());

        if (optCita.isEmpty()) {
            throw new BadRequestException(
                    "La cita con ID " + citaRq.getId() + " no existe.");
        }

        
        Optional<Medico> optMedico =
                this.medicoRepository.findById(citaRq.getMedicoId());

        if (optMedico.isEmpty()) {
            throw new BadRequestException(
                    "El médico con ID " + citaRq.getMedicoId()
                            + " no existe.");
        }

        Cita cita = optCita.get();

        cita.setClienteId(citaRq.getClienteId());
        cita.setMedico(optMedico.get());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());   

        cita.setFechaHora(
                LocalDateTime.parse(
                        citaRq.getFechaHora(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

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
private Cita converterToCita(CitaRq citaRq, Medico medico) {

    Cita cita = new Cita();

    cita.setClienteId(citaRq.getClienteId());
    cita.setMascotaId(citaRq.getMascotaId());
    cita.setEstado(citaRq.getEstado());
    cita.setMotivo(citaRq.getMotivo());

    cita.setFechaHora(
            LocalDateTime.parse(
                    citaRq.getFechaHora(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    cita.setMedico(medico);

    return cita;
}
}
