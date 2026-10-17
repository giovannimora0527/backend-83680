package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.BusinessRuleException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.AnotacionHistoriaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Implementación del servicio de anotaciones. Reglas de negocio: la historia y el
 * veterinario deben existir, la descripción es obligatoria y no se agregan
 * anotaciones a una historia cerrada.
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    private static final String ESTADO_CERRADA = "CERRADA";

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /** {@inheritDoc} */
    @Override
    public Page<AnotacionHistoria> listarAnotaciones(Long historiaId, String texto, Pageable pageable) {
        if (!historiaMedicaRepository.existsById(historiaId)) {
            throw new NotFoundException("La historia clinica no existe");
        }
        String textoFiltro = texto == null ? "" : texto.trim();
        return anotacionHistoriaRepository.buscarPorHistoria(historiaId, textoFiltro, pageable);
    }

    /** {@inheritDoc} */
    @Override
    public AnotacionHistoria consultarAnotacion(Long id) {
        return anotacionHistoriaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La anotacion no existe"));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public MiRespuestaRS crearAnotacionHistoria(AnotacionHistoriaRq rq) throws BadRequestException {
        this.validarCamposObligatorios(rq);

        HistoriaMedica historia = historiaMedicaRepository.findById(rq.getHistoriaId())
                .orElseThrow(() -> new NotFoundException("La historia clinica no existe"));
        Medico medico = medicoRepository.findById(rq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("El veterinario no existe"));

        if (ESTADO_CERRADA.equals(historia.getEstado())) {
            throw new BusinessRuleException(
                    "No se pueden agregar anotaciones a una historia clinica cerrada");
        }

        AnotacionHistoria anotacion = new AnotacionHistoria();
        anotacion.setHistoria(historia);
        anotacion.setMedico(medico);
        anotacion.setFecha(LocalDateTime.now());
        anotacion.setDescripcion(rq.getDescripcion().trim());
        anotacion.setObservacion(rq.getObservacion());
        anotacion.setDiagnostico(rq.getDiagnostico());
        anotacionHistoriaRepository.save(anotacion);

        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(201);
        rta.setMessage("Anotacion agregada a la historia clinica");
        return rta;
    }

    /**
     * Verifica que los campos obligatorios vengan diligenciados.
     *
     * @param rq datos recibidos.
     * @throws BadRequestException si falta algún campo.
     */
    private void validarCamposObligatorios(AnotacionHistoriaRq rq) throws BadRequestException {
        if (rq == null) {
            throw new BadRequestException("La anotacion no puede ser nula");
        }
        if (rq.getHistoriaId() == null) {
            throw new BadRequestException("La historia clinica es obligatoria");
        }
        if (rq.getMedicoId() == null) {
            throw new BadRequestException("El veterinario es obligatorio");
        }
        if (rq.getDescripcion() == null || rq.getDescripcion().isBlank()) {
            throw new BadRequestException("La descripcion de la anotacion es obligatoria");
        }
    }
}