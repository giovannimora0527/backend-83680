package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/** Implementa las operaciones de anotaciones médicas. */
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {
    @Autowired
    /** Repositorio principal de anotaciones. */
    private AnotacionHistoriaRepository anotacionRepository;
    @Autowired
    /** Repositorio de historias médicas relacionadas. */
    private final HistoriaMedicaRepository historiaMedicaRepository;
    @Autowired
    /** Repositorio de médicos relacionados. */
    private final MedicoRepository medicoRepository;

    /** Recibe los repositorios necesarios para operar. */
    public AnotacionHistoriaServiceImpl(AnotacionHistoriaRepository anotacionRepository,
                                        HistoriaMedicaRepository historiaMedicaRepository,
                                        MedicoRepository medicoRepository) {
        this.anotacionRepository = anotacionRepository;
        this.historiaMedicaRepository = historiaMedicaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    // Valida y guarda una nueva anotación relacionada con una historia clínica.
    public MiRespuestaRS crearAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException {
        validarAnotacion(anotacionRq);
        if (anotacionRepository.existsByHistoriaMedica_IdAndMedico_IdAndDescripcion(
                anotacionRq.getHistoriaMedicaId(),
                anotacionRq.getMedicoId(),
                anotacionRq.getDescripcion().trim())) {
            throw new ConflictException("La anotación ya está creada en la historia médica");
        }
        AnotacionHistoria anotacion = new AnotacionHistoria();
        asignarDatos(anotacion, anotacionRq);
        anotacion.setFecha(LocalDateTime.now());
        anotacionRepository.save(anotacion);
        return respuesta(HttpStatus.CREATED.value(), "Anotación de historia creada correctamente");
    }

    @Override
    // Busca anotaciones creadas dentro del rango de fechas indicado.
    public List<AnotacionHistoria> listarAnotacionesPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        if (fechaInicial == null || fechaFinal == null || fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("El rango de fechas no es válido");
        }
        return anotacionRepository.findByFechaBetweenOrderByFechaDesc(fechaInicial, fechaFinal);
    }

    @Override
    // Valida la historia y devuelve sus anotaciones de la más reciente a la más antigua.
    public List<AnotacionHistoria> listarAnotacionesPorHistoria(Long historiaMedicaId)
            throws BadRequestException {
        if (historiaMedicaId == null) {
            throw new BadRequestException("El ID de la historia médica no puede ser nulo");
        }
        if (!historiaMedicaRepository.existsById(historiaMedicaId)) {
            throw new NotFoundException("Historia médica no encontrada");
        }
        return anotacionRepository.findByHistoriaMedica_IdOrderByFechaDesc(historiaMedicaId);
    }

    @Override
    // Valida los datos, actualiza la anotación y guarda los cambios.
    public MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException {
        validarAnotacion(anotacionRq);
        if (anotacionRq.getAnotacionHistoriaId() == null) {
            throw new BadRequestException("El ID de la anotación es obligatorio para actualizar");
        }
        AnotacionHistoria anotacion = anotacionRepository.findById(anotacionRq.getAnotacionHistoriaId())
                .orElseThrow(() -> new NotFoundException("Anotación de historia no encontrada"));
        asignarDatos(anotacion, anotacionRq);
        anotacion.setFecha(LocalDateTime.now());
        anotacionRepository.save(anotacion);
        return respuesta(HttpStatus.OK.value(), "Anotación de historia actualizada correctamente");
    }

    private void asignarDatos(AnotacionHistoria anotacion, AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {
        // Busca las relaciones necesarias y copia los datos de la solicitud.
        HistoriaMedica historia = historiaMedicaRepository.findById(anotacionRq.getHistoriaMedicaId())
                .orElseThrow(() -> new NotFoundException("Historia médica no encontrada"));
        if (Constantes.HISTORIA_CERRADA.equalsIgnoreCase(historia.getEstado())) {
            throw new ConflictException("La historia médica está cerrada, no se pueden registrar anotaciones");
        }
        Medico medico = medicoRepository.findById(anotacionRq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));
        anotacion.setHistoriaMedica(historia);
        anotacion.setMedico(medico);
        anotacion.setDescripcion(anotacionRq.getDescripcion().trim());
        anotacion.setObservacion(anotacionRq.getObservacion() == null ? null : anotacionRq.getObservacion().trim());
        anotacion.setDiagnostico(anotacionRq.getDiagnostico().trim());
    }

    private void validarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException {
        // Verifica que la historia, el médico, la descripción y el diagnóstico sean válidos.
        if (anotacionRq == null || anotacionRq.getHistoriaMedicaId() == null || anotacionRq.getMedicoId() == null
                || anotacionRq.getDescripcion() == null || anotacionRq.getDescripcion().isBlank()
                || anotacionRq.getDiagnostico() == null || anotacionRq.getDiagnostico().isBlank()) {
            throw new BadRequestException(
                    "La historia médica, el veterinario, la descripción y el diagnóstico o evolución son obligatorios");
        }
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        // Crea una respuesta estándar para las operaciones exitosas.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
