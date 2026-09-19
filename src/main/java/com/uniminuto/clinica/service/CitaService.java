package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.model.CitaRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {

    /**
     * lista las citas del sistema.
     * @return lista de citas.
     */
    List<Cita> listarCitas();
    /**
 * Lista las citas dentro de un rango de fechas.
 *
 * @param fechaIni fecha inicial de la consulta.
 * @param fechaFin fecha final de la consulta.
 * @return lista de citas ordenadas de la más reciente a la más antigua.
 */
List<Cita> listarCitasPorFecha(LocalDateTime fechaIni, LocalDateTime fechaFin);

    /**
     * Guarda una nueva cita en el sistema.
     * @param citaRq Cita a guardar.
     * @return Respuesta del servicio.
     * @throws BadRequestException excepcion.
     */
    RespuestaRs guardarCita(CitaRq citaRq) throws BadRequestException;

        /**
     * Actualiza una cita existente en el sistema.
     *
     * @param citaRq información actualizada de la cita.
     * @return respuesta del servicio.
     * @throws BadRequestException si la cita no existe o los datos relacionados
     * no son válidos.
     */
    RespuestaRs actualizarCita(CitaRq citaRq) throws BadRequestException;
}
