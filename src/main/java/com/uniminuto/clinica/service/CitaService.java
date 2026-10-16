package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio con la lógica de negocio de las citas.
 */
public interface CitaService {

    /**
     * Lista las citas de la más reciente a la más antigua.
     * Si las dos fechas vienen nulas se listan todas.
     *
     * @param fechaInicial primer día del rango.
     * @param fechaFinal   último día del rango.
     * @return las citas.
     */
    List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal);

    /**
     * Lista las citas de una mascota, de la más reciente a la más antigua.
     *
     * @param mascotaId id de la mascota.
     * @return las citas de la mascota.
     */
    List<CitaRs> listarCitasPorMascota(Integer mascotaId);

    /**
     * Consulta una cita por su id.
     *
     * @param citaId id de la cita.
     * @return la cita convertida a respuesta.
     */
    CitaRs consultarCita(Long citaId);

    /**
     * Busca la entidad cita y lanza error si no existe. La usan historia y fórmula.
     *
     * @param citaId id de la cita.
     * @return la entidad cita.
     */
    Cita buscarCita(Long citaId);

    /**
     * Convierte una entidad cita al objeto de respuesta.
     *
     * @param cita entidad.
     * @return objeto de respuesta.
     */
    CitaRs convertirCita(Cita cita);

    /**
     * Crea una nueva cita (pendiente, confirmada o programada).
     *
     * @param citaRq datos de la cita.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarCita(CitaRq citaRq);

    /**
     * Actualiza una cita existente respetando su estado actual.
     *
     * @param citaRq datos de la cita, incluido su identificador.
     * @return respuesta de éxito.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq);

    /**
     * Reactiva una cita cancelada, queda en estado pendiente.
     *
     * @param citaId id de la cita.
     * @return respuesta de éxito.
     */
    MiRespuestaRS reactivarCita(Long citaId);
}
