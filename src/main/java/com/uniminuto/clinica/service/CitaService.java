package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de negocio para la gestion de citas medicas.
 */
public interface CitaService {

    /**
     * Lista citas de forma paginada con filtros opcionales.
     *
     * @param estado   estado de la cita o null.
     * @param medicoId id del veterinario o null.
     * @param desde    fecha minima o null.
     * @param hasta    fecha maxima o null.
     * @param pageable paginacion y orden.
     * @return pagina de citas.
     * @throws BadRequestException si el estado o el rango de fechas no son validos.
     */
    Page<Cita> listarCitas(String estado, Long medicoId, LocalDateTime desde,
                           LocalDateTime hasta, Pageable pageable) throws BadRequestException;

    /**
     * Lista las citas dentro de un rango de fechas ordenadas ascendentemente.
     *
     * @param fechaInicio inicio del rango.
     * @param fechaFin    fin del rango.
     * @return citas del rango.
     * @throws BadRequestException si las fechas son nulas o invertidas.
     */
    List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException;

    /**
     * Consulta una cita por su id.
     *
     * @param id id de la cita.
     * @return la cita encontrada.
     */
    Cita consultarCita(Long id);

    /**
     * Crea una cita validando las reglas de negocio.
     *
     * @param citaRq datos de la cita.
     * @return mensaje de confirmacion.
     * @throws BadRequestException si faltan datos obligatorios.
     */
    MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException;

    /**
     * Actualiza una cita existente respetando las restricciones por estado.
     *
     * @param citaRq datos de la cita (el id es obligatorio).
     * @return mensaje de confirmacion.
     * @throws BadRequestException si faltan datos obligatorios.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException;
}