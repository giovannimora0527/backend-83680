package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de la lógica de negocio para Anotaciones de Historia Médica.
 *
 * @author Alma Hernandez
 */

@Service

public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    @Autowired
    private AnotacionHistoriaRepository repository;

    /**
     * Obtiene las anotaciones médicas dentro de un rango de fechas específico.
     */
    @Override
    public List<AnotacionHistoria> listarAnotacionesPorFecha(
            LocalDateTime fechaInicio, 
            LocalDateTime fechaFin) throws BadRequestException {
        try {
            if (fechaInicio == null || fechaFin == null) {
                throw new BadRequestException("Las fechas son obligatorias");
            }
            return repository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al listar anotaciones: " + e.getMessage());
        }
    }

    /**
     * Crea una nueva anotación en la historia médica.
     */
    @Override
    public MiRespuestaRS crearAnotacion(AnotacionHistoriaRq rq) throws BadRequestException {
        try {
            if (rq.getDescripcion() == null || rq.getHistoriaId() == null) {
                throw new BadRequestException("La descripción y el ID de historia son obligatorios");
            }

            AnotacionHistoria entity = new AnotacionHistoria();
            entity.setHistoriaId(rq.getHistoriaId());
            entity.setMedicoId(rq.getMedicoId());
            entity.setFecha(rq.getFecha());
            entity.setDescripcion(rq.getDescripcion());

            repository.save(entity);

            MiRespuestaRS resp = new MiRespuestaRS();
            resp.setStatus(200);
            resp.setMessage("Anotación creada exitosamente");
            return resp;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al crear anotación: " + e.getMessage());
        }
    }

    /**
     * Actualiza los datos de una anotación médica existente.
     */
    @Override
    public MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq rq) throws BadRequestException {
        try {
            if (rq.getId() == null) {
                throw new BadRequestException("El ID es obligatorio para actualizar");
            }

            Optional<AnotacionHistoria> opt = repository.findById(rq.getId());
            if (!opt.isPresent()) {
                throw new BadRequestException("Anotación no encontrada con ID: " + rq.getId());
            }

            AnotacionHistoria entity = opt.get();
            entity.setHistoriaId(rq.getHistoriaId());
            entity.setMedicoId(rq.getMedicoId());
            entity.setFecha(rq.getFecha());
            entity.setDescripcion(rq.getDescripcion());

            repository.save(entity);

            MiRespuestaRS resp = new MiRespuestaRS();
            resp.setStatus(200);
            resp.setMessage("Anotación actualizada exitosamente");
            return resp;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al actualizar anotación: " + e.getMessage());
        }
    }
}