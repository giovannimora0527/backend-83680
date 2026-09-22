package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.FormulaMedica;
import java.util.List;

/**
 * Interfaz de servicio para la gestión de las fórmulas médicas del inventario[cite: 20].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
public interface FormulaMedicaService {

    /**
     * Obtiene todas las fórmulas médicas ordenadas de la más reciente a la más antigua[cite: 20].
     *
     * @return Lista de fórmulas médicas ordenadas descendentemente por fecha[cite: 20].
     */
    List<FormulaMedica> obtenerTodasOrdenadasPorFecha();
}