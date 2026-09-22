package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interfaz de servicio para la gestión lógica de las anotaciones de historia clínica[cite: 18].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
public interface AnotacionHistoriaService {

    /**
     * Guarda una nueva anotación de historia en el sistema[cite: 18].
     *
     * @param anotacion Objeto AnotacionHistoria a registrar[cite: 18].
     * @return La anotación guardada exitosamente[cite: 18].
     */
    AnotacionHistoria guardarAnotacion(AnotacionHistoria anotacion);

    /**
     * Actualiza una anotación de historia existente[cite: 18].
     *
     * @param id Identificador de la anotación a actualizar[cite: 18].
     * @param detallesAnotacion Nuevos detalles que se asignarán a la anotación[cite: 18].
     * @return La anotación actualizada[cite: 18].
     */
    AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria detallesAnotacion);

    /**
     * Filtra las anotaciones de historia dentro de un rango de fechas[cite: 18].
     *
     * @param fechaInicio Fecha inicial del filtro[cite: 18].
     * @param fechaFin Fecha final del filtro[cite: 18].
     * @return Lista de anotaciones filtradas y ordenadas[cite: 18].
     */
    List<AnotacionHistoria> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}