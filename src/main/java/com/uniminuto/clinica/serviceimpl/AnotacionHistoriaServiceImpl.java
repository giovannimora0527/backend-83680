package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new BadRequestException("La anotación ya está creada en la historia médica");
        }
        AnotacionHistoria anotacion = new AnotacionHistoria();
        asignarDatos(anotacion, anotacionRq);
        anotacion.setFecha(LocalDateTime.now());
        anotacionRepository.save(anotacion);
        return respuesta("Anotación de historia creada correctamente");
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
    // Valida los datos, actualiza la anotación y guarda los cambios.
    public MiRespuestaRS actualizarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException {
        validarAnotacion(anotacionRq);
        if (anotacionRq.getAnotacionHistoriaId() == null) {
            throw new BadRequestException("El ID de la anotación es obligatorio para actualizar");
        }
        AnotacionHistoria anotacion = anotacionRepository.findById(anotacionRq.getAnotacionHistoriaId())
                .orElseThrow(() -> new BadRequestException("Anotación de historia no encontrada"));
        asignarDatos(anotacion, anotacionRq);
        anotacion.setFecha(LocalDateTime.now());
        anotacionRepository.save(anotacion);
        return respuesta("Anotación de historia actualizada correctamente");
    }

    private void asignarDatos(AnotacionHistoria anotacion, AnotacionHistoriaRq anotacionRq)
            throws BadRequestException {
        // Busca las relaciones necesarias y copia los datos de la solicitud.
        HistoriaMedica historia = historiaMedicaRepository.findById(anotacionRq.getHistoriaMedicaId())
                .orElseThrow(() -> new BadRequestException("Historia médica no encontrada"));
        Medico medico = medicoRepository.findById(anotacionRq.getMedicoId())
                .orElseThrow(() -> new BadRequestException("Médico no encontrado"));
        anotacion.setHistoriaMedica(historia);
        anotacion.setMedico(medico);
        anotacion.setDescripcion(anotacionRq.getDescripcion().trim());
    }

    private void validarAnotacion(AnotacionHistoriaRq anotacionRq) throws BadRequestException {
        // Verifica que la historia, el médico y la descripción sean válidos.
        if (anotacionRq == null || anotacionRq.getHistoriaMedicaId() == null || anotacionRq.getMedicoId() == null
                || anotacionRq.getDescripcion() == null || anotacionRq.getDescripcion().isBlank()) {
            throw new BadRequestException("La historia médica, el médico y la descripción son obligatorios");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        // Crea una respuesta estándar para las operaciones exitosas.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
