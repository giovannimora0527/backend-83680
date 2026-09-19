package com.uniminuto.clinica.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;

/**
 * Implementa las operaciones relacionadas con las anotaciones
 * de las historias médicas.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    /**
     * Obtiene las anotaciones dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de anotaciones encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public List<AnotacionHistoria> obtenerAnotacionesPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal) throws BadRequestException {

        return anotacionHistoriaRepository.findByFechaBetweenOrderByFechaDesc(
                fechaInicial,
                fechaFinal
        );
    }

    /**
     * Crea una nueva anotación de historia.
     *
     * @param anotacionHistoriaRq datos de la anotación que se desea crear.
     * @return anotación creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public AnotacionHistoria crearAnotacion(
            AnotacionHistoriaRq anotacionHistoriaRq)
            throws BadRequestException {

        AnotacionHistoria anotacion = new AnotacionHistoria();

        anotacion.setHistoriaId(anotacionHistoriaRq.getHistoriaId());
        anotacion.setMedicoId(anotacionHistoriaRq.getMedicoId());
        anotacion.setFecha(anotacionHistoriaRq.getFecha());
        anotacion.setDescripcion(anotacionHistoriaRq.getDescripcion());

        return anotacionHistoriaRepository.save(anotacion);
    }

    /**
     * Actualiza una anotación de historia existente.
     *
     * @param id identificador de la anotación.
     * @param anotacionHistoriaRq nuevos datos de la anotación.
     * @return anotación actualizada.
     * @throws BadRequestException si la anotación no existe.
     */
    @Override
    public AnotacionHistoria actualizarAnotacion(
            Long id,
            AnotacionHistoriaRq anotacionHistoriaRq)
            throws BadRequestException {

        AnotacionHistoria anotacion =
                anotacionHistoriaRepository.findById(id)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "La anotación no existe"));

        anotacion.setHistoriaId(anotacionHistoriaRq.getHistoriaId());
        anotacion.setMedicoId(anotacionHistoriaRq.getMedicoId());
        anotacion.setFecha(anotacionHistoriaRq.getFecha());
        anotacion.setDescripcion(anotacionHistoriaRq.getDescripcion());

        return anotacionHistoriaRepository.save(anotacion);
    }
}