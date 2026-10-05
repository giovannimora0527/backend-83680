package com.uniminuto.clinica.serviceimpl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;

/**
 * Implementa las operaciones relacionadas con las citas de la clínica.
 */
@Service
public class CitaServiceImpl implements CitaService {

    private static final int DURACION_CITA_MINUTOS = 20;

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Obtiene las citas que se encuentran dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     */
    @Override
    public List<Cita> obtenerCitasPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal) throws BadRequestException {

        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                fechaInicial,
                fechaFinal
        );
    }

    /**
     * Crea una nueva cita validando el horario y la disponibilidad del médico.
     */
    @Override
    public Cita crearCita(CitaRq citaRq) throws BadRequestException {

        validarHorario(citaRq.getFechaHora());

        validarDisponibilidadMedico(
                citaRq.getMedicoId(),
                citaRq.getFechaHora(),
                null
        );

        Cita cita = new Cita();

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());

        return citaRepository.save(cita);
    }

    /**
     * Actualiza una cita existente validando el nuevo horario y
     * la disponibilidad del médico.
     */
    @Override
    public Cita actualizarCita(
            Long id,
            CitaRq citaRq) throws BadRequestException {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException("La cita no existe"));

        validarHorario(citaRq.getFechaHora());

        validarDisponibilidadMedico(
                citaRq.getMedicoId(),
                citaRq.getFechaHora(),
                id
        );

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());

        return citaRepository.save(cita);
    }

    /**
     * Valida que la hora de la cita corresponda a un bloque de 20 minutos.
     *
     * Los únicos minutos permitidos son:
     * 00, 20 y 40.
     */
    private void validarHorario(LocalDateTime fechaHora)
            throws BadRequestException {

        if (fechaHora == null) {
            throw new BadRequestException(
                    "La fecha y hora de la cita son obligatorias"
            );
        }

        LocalTime hora = fechaHora.toLocalTime();

        int minuto = hora.getMinute();

        if (minuto != 0 && minuto != 20 && minuto != 40) {
            throw new BadRequestException(
                    "La cita debe comenzar en bloques de 20 minutos. "
                    + "Horarios permitidos: minuto 00, 20 o 40."
            );
        }

        if (hora.getSecond() != 0 || hora.getNano() != 0) {
            throw new BadRequestException(
                    "La hora de la cita debe tener segundos y nanosegundos en cero."
            );
        }
    }

    /**
     * Verifica que el médico no tenga otra cita en el mismo bloque
     * de 20 minutos.
     *
     * @param medicoId médico que tendrá la cita.
     * @param fechaHora fecha y hora de inicio de la nueva cita.
     * @param citaIdExcluir identificador de la cita que se está actualizando.
     *                    Es null cuando se está creando una nueva cita.
     */
    private void validarDisponibilidadMedico(
            Integer medicoId,
            LocalDateTime fechaHora,
            Long citaIdExcluir) throws BadRequestException {

        if (medicoId == null) {
            throw new BadRequestException(
                    "El médico es obligatorio"
            );
        }

        LocalDateTime inicioBloque = fechaHora;
        LocalDateTime finBloque = fechaHora.plusMinutes(DURACION_CITA_MINUTOS);

        /*
         * Buscamos las citas del médico desde el inicio del bloque
         * hasta el comienzo del siguiente bloque.
         */
        List<Cita> citasExistentes =
                citaRepository.findByMedicoIdAndFechaHoraBetween(
                        medicoId,
                        inicioBloque,
                        finBloque.minusNanos(1)
                );

        for (Cita citaExistente : citasExistentes) {

            /*
             * Al actualizar una cita, ignoramos la misma cita.
             */
            if (citaIdExcluir != null
                    && citaExistente.getId().equals(citaIdExcluir)) {
                continue;
            }

            /*
             * Una cita cancelada no ocupa el horario.
             */
            if (citaExistente.getEstado() != null
                    && citaExistente.getEstado().equalsIgnoreCase("cancelada")) {
                continue;
            }

            throw new BadRequestException(
                    "El médico ya tiene una cita programada para las "
                    + inicioBloque.toLocalTime()
                    + ". El siguiente horario disponible comienza a las "
                    + finBloque.toLocalTime()
                    + "."
            );
        }
    }
}