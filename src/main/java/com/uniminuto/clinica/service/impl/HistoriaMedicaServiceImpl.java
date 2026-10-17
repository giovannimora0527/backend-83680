
package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.model.AnotacionHistoriaRq;
import com.uniminuto.clinica.model.AnotacionHistoriaRs;
import com.uniminuto.clinica.model.CrearAnotacionHistoriaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRq;
import com.uniminuto.clinica.model.HistoriaMedicaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
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
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Crea una nueva historia médica.
     */
    @Override
    public RespuestaRs guardarHistoria(
            HistoriaMedicaRq historiaRq)
            throws BadRequestException {

        HistoriaMedica historia = new HistoriaMedica();
        historia.setPacienteId(historiaRq.getPacienteId());
        historia.setFechaCreacion(LocalDateTime.now());

        historiaMedicaRepository.save(historia);

        RespuestaRs respuesta = new RespuestaRs();
        respuesta.setStatus(200);
        respuesta.setMensaje("Historia médica creada exitosamente.");

        return respuesta;
    }

    /**
     * Crea una anotación asociada a una historia médica existente.
     */
    @Override
    public RespuestaRs guardarAnotacion(
            CrearAnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        if (anotacionRq.getHistoriaId() == null) {
            throw new BadRequestException(
                    "El identificador de la historia médica es obligatorio.");
        }

        if (anotacionRq.getMedicoId() == null) {
            throw new BadRequestException(
                    "El identificador del médico es obligatorio.");
        }

        if (anotacionRq.getDescripcion() == null
                || anotacionRq.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException(
                    "La descripción de la anotación es obligatoria.");
        }

        Optional<HistoriaMedica> optHistoria =
                historiaMedicaRepository.findById(
                        anotacionRq.getHistoriaId());

        if (optHistoria.isEmpty()) {
            throw new BadRequestException(
                    "La historia médica indicada no existe.");
        }

        Optional<Medico> optMedico =
                medicoRepository.findById(anotacionRq.getMedicoId());

        if (optMedico.isEmpty()) {
            throw new BadRequestException(
                    "El médico indicado no existe.");
        }

        AnotacionHistoria anotacion = new AnotacionHistoria();

        anotacion.setHistoriaId(
                optHistoria.get().getId().intValue());
        anotacion.setMedicoId(optMedico.get().getId());
        anotacion.setFecha(LocalDateTime.now());
        anotacion.setDescripcion(
                anotacionRq.getDescripcion().trim());

        anotacionHistoriaRepository.save(anotacion);

        RespuestaRs respuesta = new RespuestaRs();
        respuesta.setStatus(200);
        respuesta.setMensaje("Anotación creada exitosamente.");

        return respuesta;
    }

    /**
     * Lista las historias médicas dentro de un rango de fechas.
     */
    @Override
    public List<HistoriaMedicaRs> listarHistorias(
            LocalDateTime fechaIni,
            LocalDateTime fechaFin) {

        List<HistoriaMedica> historias =
                historiaMedicaRepository
                        .findByFechaCreacionBetweenOrderByFechaCreacionDesc(
                                fechaIni, fechaFin);

        List<HistoriaMedicaRs> respuestas = new ArrayList<>();

        for (HistoriaMedica historia : historias) {
            HistoriaMedicaRs respuesta = new HistoriaMedicaRs();

            respuesta.setId(historia.getId());
            respuesta.setPacienteId(historia.getPacienteId());
            respuesta.setFechaCreacion(historia.getFechaCreacion());

            List<AnotacionHistoriaRs> anotaciones = new ArrayList<>();

            if (historia.getAnotaciones() != null) {
                for (AnotacionHistoria anotacion : historia.getAnotaciones()) {
                    AnotacionHistoriaRs anotacionRs =
                            new AnotacionHistoriaRs();

                    anotacionRs.setId(anotacion.getId());
                    anotacionRs.setHistoriaId(anotacion.getHistoriaId());
                    anotacionRs.setMedicoId(anotacion.getMedicoId());
                    anotacionRs.setFecha(anotacion.getFecha());
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
     * Actualiza la descripción de una anotación existente.
     */
    @Override
    public RespuestaRs actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {

        Optional<AnotacionHistoria> optAnotacion =
                anotacionHistoriaRepository.findById(id);

        if (optAnotacion.isEmpty()) {
            throw new BadRequestException(
                    "La anotación con ID " + id + " no existe.");
        }

        if (anotacionRq.getDescripcion() == null
                || anotacionRq.getDescripcion().trim().isEmpty()) {
            throw new BadRequestException(
                    "La descripción de la anotación es obligatoria.");
        }

        AnotacionHistoria anotacion = optAnotacion.get();
        anotacion.setDescripcion(
                anotacionRq.getDescripcion().trim());

        anotacionHistoriaRepository.save(anotacion);

        RespuestaRs respuesta = new RespuestaRs();
        respuesta.setStatus(200);
        respuesta.setMensaje("Anotación actualizada exitosamente.");

        return respuesta;
    }
}
