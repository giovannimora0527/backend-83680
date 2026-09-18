package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.AnotacionHistoriaRs;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios relacionados
 * con las historias médicas.
 */
@Service
public class HistoriaMedicaServiceImpl
        implements HistoriaMedicaService {

    /**
     * Repository utilizado para acceder a las historias médicas.
     */
    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    /**
     * Repository utilizado para acceder a las anotaciones
     * de las historias médicas.
     */
    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    /**
     * Crea una nueva historia médica.
     *
     * @param historiaRq datos necesarios para crear la historia.
     * @return respuesta del servicio.
     * @throws BadRequestException si los datos no son válidos.
     */
    @Override
    public RespuestaRs guardarHistoria(
            HistoriaMedicaRq historiaRq)
            throws BadRequestException {

        HistoriaMedica historia = new HistoriaMedica();

        historia.setPacienteId(
                historiaRq.getPacienteId());

        historia.setFechaCreacion(
                LocalDateTime.now());

        this.historiaMedicaRepository.save(historia);

        RespuestaRs respuesta = new RespuestaRs();

        respuesta.setStatus(200);
        respuesta.setMensaje(
                "Historia médica creada exitosamente.");

        return respuesta;
    }

    /**
     * Lista las historias médicas dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaIni fecha y hora inicial del rango.
     * @param fechaFin fecha y hora final del rango.
     * @return lista de historias médicas.
     */
    @Override
    public List<HistoriaMedicaRs> listarHistorias(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin) {

        List<HistoriaMedica> historias =
                this.historiaMedicaRepository
                        .findByFechaCreacionBetweenOrderByFechaCreacionDesc(
                                fechaIni,
                                fechaFin);

        List<HistoriaMedicaRs> respuestas =
                new ArrayList<>();

        for (HistoriaMedica historia : historias) {

            HistoriaMedicaRs respuesta =
                    new HistoriaMedicaRs();

            respuesta.setId(historia.getId());
            respuesta.setPacienteId(
                    historia.getPacienteId());
            respuesta.setFechaCreacion(
                    historia.getFechaCreacion());

            List<AnotacionHistoriaRs> anotaciones =
                    new ArrayList<>();

            if (historia.getAnotaciones() != null) {

                for (AnotacionHistoria anotacion
                        : historia.getAnotaciones()) {

                    AnotacionHistoriaRs anotacionRs =
                            new AnotacionHistoriaRs();

                    anotacionRs.setId(
                            anotacion.getId());
                    anotacionRs.setHistoriaId(
                            anotacion.getHistoriaId());
                    anotacionRs.setMedicoId(
                            anotacion.getMedicoId());
                    anotacionRs.setFecha(
                            anotacion.getFecha());
                    anotacionRs.setDescripcion(
                            anotacion.getDescripcion());

                    anotaciones.add(anotacionRs);
                }
            }

            respuesta.setAnotaciones(anotaciones);

            respuestas.add(respuesta);
        }

        return respuestas;
    }

    /**
     * Actualiza una anotación de una historia médica.
     *
     * @param id identificador de la anotación que se desea actualizar.
     * @param anotacionRq nuevos datos de la anotación.
     * @return respuesta del servicio.
     * @throws BadRequestException si la anotación no existe.
     */
    @Override
    public RespuestaRs actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        Optional<AnotacionHistoria> optAnotacion =
                this.anotacionHistoriaRepository
                        .findById(id);

        if (optAnotacion.isEmpty()) {

            throw new BadRequestException(
                    "La anotación con ID "
                            + id
                            + " no existe.");
        }

        AnotacionHistoria anotacion =
                optAnotacion.get();

        anotacion.setDescripcion(
                anotacionRq.getDescripcion());

        this.anotacionHistoriaRepository.save(
                anotacion);

        RespuestaRs respuesta =
                new RespuestaRs();

        respuesta.setStatus(200);
        respuesta.setMensaje(
                "Anotación actualizada exitosamente.");

        return respuesta;
    }
}