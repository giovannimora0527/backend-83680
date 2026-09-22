package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio que define la lógica de negocio para las citas médicas de la clínica[cite: 19].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
public interface CitaService {

    /**
     * Filtra las citas del sistema dado un rango de fechas[cite: 19].
     *
     * @param fechaInicio Fecha inicial de la consulta[cite: 19].
     * @param fechaFin Fecha final de la consulta[cite: 19].
     * @return Lista de citas encontradas en el rango especificado[cite: 19].
     */
    List<Cita> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Registra y almacena una nueva cita médica[cite: 19].
     *
     * @param cita Objeto Cita con la información a guardar[cite: 19].
     * @return La cita guardada en el sistema[cite: 19].
     */
    Cita guardarCita(Cita cita);

    /**
     * Actualiza los datos de una cita existente[cite: 19].
     *
     * @param id Identificador de la cita a modificar[cite: 19].
     * @param detallesCita Datos nuevos para la cita[cite: 19].
     * @return La cita actualizada[cite: 19].
     */
    Cita actualizarCita(Long id, Cita detallesCita);
}