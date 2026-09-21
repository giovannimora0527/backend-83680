package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.AnotacionHistoriaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de las anotaciones de historia medica.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    /**
     * Repositorio de anotaciones de historia medica.
     */
    @Autowired
    private AnotacionHistoriaRepository anotacionRepository;

    /**
     * Repositorio de historias medicas.
     */
    @Autowired
    private HistoriaMedicaRepository historiaRepository;

    /**
     * Repositorio de medicos.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Lista las anotaciones entre dos fechas, de la mas reciente a la mas antigua.
     *
     * @param fechaInicial primer dia del rango.
     * @param fechaFinal   ultimo dia del rango.
     * @return las anotaciones del rango.
     */
    @Override
    public List<AnotacionHistoriaRs> listarAnotaciones(LocalDate fechaInicial, LocalDate fechaFinal) {
        // Paso 1. Validar las fechas
        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException("La fecha inicial y la fecha final son obligatorias");
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("La fecha inicial no puede ser mayor que la fecha final");
        }

        // Paso 2. El rango va desde el inicio del primer dia hasta el final del ultimo dia
        LocalDateTime inicio = fechaInicial.atStartOfDay();
        LocalDateTime fin = fechaFinal.atTime(23, 59, 59);

        // Paso 3. Consultar las anotaciones ya ordenadas de la mas reciente a la mas antigua
        List<AnotacionHistoria> anotaciones = anotacionRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin);

        // Paso 4. Convertir cada anotacion al objeto de respuesta
        List<AnotacionHistoriaRs> respuesta = new ArrayList<>();
        for (AnotacionHistoria anotacion : anotaciones) {
            AnotacionHistoriaRs rs = new AnotacionHistoriaRs();
            rs.setId(anotacion.getId());
            rs.setHistoriaId(anotacion.getHistoria().getId());
            rs.setMedicoId(anotacion.getMedico().getId());
            rs.setMedicoNombre(anotacion.getMedico().getNombres() + " " + anotacion.getMedico().getApellidos());
            rs.setFecha(anotacion.getFecha());
            rs.setDescripcion(anotacion.getDescripcion());
            respuesta.add(rs);
        }

        return respuesta;
    }

    /**
     * Guarda una anotacion nueva en una historia medica.
     *
     * @param anotacionRq datos de la anotacion.
     * @return respuesta de exito.
     */
    @Override
    public MiRespuestaRS guardarAnotacion(AnotacionHistoriaRq anotacionRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoAnotacion(anotacionRq);

        if (anotacionRq.getHistoriaId() == null || anotacionRq.getHistoriaId() <= 0) {
            throw new BadRequestException("El ID de la historia médica no puede ser nulo o menor que 1");
        }

        // Paso 2. Buscar la historia y el medico
        Optional<HistoriaMedica> optHistoria = historiaRepository.findById(anotacionRq.getHistoriaId());
        if (optHistoria.isEmpty()) {
            throw new BadRequestException("Historia médica no encontrada");
        }

        Optional<Medico> optMedico = medicoRepository.findById(anotacionRq.getMedicoId());
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }

        // Paso 3. Crear la anotacion para guardar
        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoria(optHistoria.get());
        anotacion.setMedico(optMedico.get());
        anotacion.setDescripcion(anotacionRq.getDescripcion());
        anotacion.setFecha(LocalDateTime.now());

        // Paso 4. Guardar la anotacion
        this.anotacionRepository.save(anotacion);

        // Paso 5. Devolver la respuesta de exito
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Anotación guardada correctamente");

        return rta;
    }

    /**
     * Actualiza el medico y la descripcion de una anotacion que ya existe.
     *
     * @param anotacionRq datos de la anotacion, incluido su id.
     * @return respuesta de exito.
     */
    @Override
    public MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoAnotacion(anotacionRq);

        if (anotacionRq.getAnotacionId() == null) {
            throw new BadRequestException("El ID de la anotación no puede ser nulo");
        }

        // Paso 2. Buscar la anotacion y el medico
        Optional<AnotacionHistoria> optAnotacion = anotacionRepository.findById(anotacionRq.getAnotacionId());
        if (optAnotacion.isEmpty()) {
            throw new BadRequestException("Anotación no encontrada");
        }

        Optional<Medico> optMedico = medicoRepository.findById(anotacionRq.getMedicoId());
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }

        // Paso 3. Actualizar los datos de la anotacion
        AnotacionHistoria anotacionActualizar = optAnotacion.get();
        anotacionActualizar.setMedico(optMedico.get());
        anotacionActualizar.setDescripcion(anotacionRq.getDescripcion());

        // Paso 4. Guardar la anotacion actualizada
        this.anotacionRepository.save(anotacionActualizar);

        // Paso 5. Devolver la respuesta de exito
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Anotación actualizada correctamente");

        return rta;
    }

    /**
     * Valida los datos obligatorios de la anotacion.
     *
     * @param anotacionRq objeto a validar.
     */
    private void validarObjetoAnotacion(AnotacionHistoriaRq anotacionRq) {
        if (anotacionRq == null) {
            throw new BadRequestException("El objeto AnotacionHistoriaRq no puede ser nulo");
        }

        if (anotacionRq.getMedicoId() == null || anotacionRq.getMedicoId() <= 0) {
            throw new BadRequestException("El ID del médico no puede ser nulo o menor que 1");
        }

        if (anotacionRq.getDescripcion() == null || anotacionRq.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripción de la anotación no puede ser nula o vacía");
        }
    }
}
