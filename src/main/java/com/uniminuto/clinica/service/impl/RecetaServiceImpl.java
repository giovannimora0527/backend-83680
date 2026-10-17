package com.uniminuto.clinica.service.impl;

import com.uniminuto.clinica.model.RecetaRs;
import java.util.stream.Collectors;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Receta;
import com.uniminuto.clinica.model.RecetaRq;
import com.uniminuto.clinica.model.RecetaRs;
import com.uniminuto.clinica.model.RespuestaRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.RecetaRepository;
import com.uniminuto.clinica.service.RecetaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecetaServiceImpl implements RecetaService {

    /**
     * Repositorio de datos para recetas médicas.
     */
    @Autowired
    private RecetaRepository recetaRepository;

    /**
     * CitaRepository.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * MedicamentoRepository.
     */
    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Override
    public List<RecetaRs> listarRecetas() {

    List<Receta> recetas =
            this.recetaRepository.findAllByOrderByFechaCreacionRegistroDesc();

    return recetas.stream().map(receta -> {

        RecetaRs respuesta = new RecetaRs();

        respuesta.setId(receta.getId());
        respuesta.setCitaId(receta.getCita().getId());
        respuesta.setMedicamentoId(receta.getMedicamento().getId());
        respuesta.setDosis(receta.getDosis());
        respuesta.setIndicaciones(receta.getIndicaciones());
        respuesta.setFechaCreacionRegistro(
                receta.getFechaCreacionRegistro());

        return respuesta;

    }).collect(Collectors.toList());
}

    @Override
    public RespuestaRs guardarReceta(RecetaRq recetaRq) throws BadRequestException {
        Optional<Medicamento> optMedicamento = this.medicamentoRepository.findById(recetaRq.getMedicamentoId());
        if (optMedicamento.isEmpty()) {
            throw new BadRequestException("El medicamento con ID " + recetaRq.getMedicamentoId() + " no existe.");
        }

        Optional<Cita> optCita = this.citaRepository.findById(recetaRq.getCitaId());
        if(optCita.isEmpty()) {
            throw new BadRequestException("La cita con ID " + recetaRq.getCitaId() + " no existe.");
        }

        Cita cita = optCita.get();
        Medicamento medicamento = optMedicamento.get();
        List<Receta> recetasExistentes = this.recetaRepository.findByCitaAndMedicamento(cita, medicamento);
        if (!recetasExistentes.isEmpty()) {
            throw new BadRequestException("Ya existe una receta para la cita con ID " + recetaRq.getCitaId() +
                    " y el medicamento con ID " + recetaRq.getMedicamentoId() + ".");
        }

        Receta receta = this.convertirRqAEntidad(recetaRq, optCita.get(), optMedicamento.get());
        this.recetaRepository.save(receta);
        RespuestaRs rta = new RespuestaRs();
        rta.setMensaje("Receta guardada exitosamente.");
        rta.setStatus(200);
        return rta;
    }

    /**
     * Convierte un objeto RecetaRq a una entidad Receta.
     * @param recetaRq receta de entrada.
     * @return entidad receta.
     */
    private Receta convertirRqAEntidad(RecetaRq recetaRq, Cita cita, Medicamento medicamento) {
        Receta receta = new Receta();
        receta.setCita(cita);
        receta.setMedicamento(medicamento);
        receta.setDosis(recetaRq.getDosis());
        receta.setIndicaciones(recetaRq.getIndicaciones());
        receta.setFechaCreacionRegistro(LocalDateTime.now());
        return receta;
    }
}
