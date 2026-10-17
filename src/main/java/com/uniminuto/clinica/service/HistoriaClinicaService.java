package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.HistoriaClinicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

/**
 * Servicio de negocio para la gestión de historias clínicas.
 */
public interface HistoriaClinicaService {

    /**
     * Lista historias clínicas de forma paginada con filtros opcionales.
     *
     * @param texto     texto libre (mascota, cliente, veterinario o motivo) o null.
     * @param estado    estado de la historia o null.
     * @param clienteId id del cliente o null.
     * @param mascotaId id de la mascota o null.
     * @param medicoId  id del veterinario o null.
     * @param desde     fecha mínima o null.
     * @param hasta     fecha máxima o null.
     * @param pageable  paginación y orden.
     * @return página de historias clínicas.
     * @throws BadRequestException si el estado o el rango de fechas no son válidos.
     */
    Page<HistoriaMedica> listarHistorias(String texto, String estado, Long clienteId, Integer mascotaId,
                                         Long medicoId, LocalDateTime desde, LocalDateTime hasta,
                                         Pageable pageable) throws BadRequestException;

    /**
     * Consulta una historia clínica por su id.
     *
     * @param id id de la historia clínica.
     * @return la historia encontrada.
     */
    HistoriaMedica consultarHistoria(Long id);

    /**
     * Crea una historia clínica validando las reglas de negocio.
     *
     * @param rq datos de la historia.
     * @return mensaje de confirmación.
     * @throws BadRequestException si faltan datos obligatorios.
     */
    MiRespuestaRS crearHistoria(HistoriaClinicaRq rq) throws BadRequestException;
}