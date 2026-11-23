package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.entity.Medico;
import org.springframework.web.bind.annotation.RequestParam;
import com.uniminuto.clinica.models.MedicoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Rutas de los servicios de los medicos.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medico")
public interface MedicoApi {

    /**
     * Lista todos los medicos.
     *
     * @return lista de medicos.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"})
    ResponseEntity<List<Medico>> listarMedicos();

    /**
     * Crea un medico nuevo.
     *
     * @param medicoRq datos del medico.
     * @return respuesta de exito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarMedico(@RequestBody MedicoRq medicoRq);

    /**
     * Actualiza un medico existente.
     *
     * @param medicoRq datos del medico con su id.
     * @return respuesta de exito.
     */
    @PutMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMedico(@RequestBody MedicoRq medicoRq);

    /**
     * Horarios de disponibilidad de un medico.
     *
     * @param medicoId id del medico.
     * @return horarios.
     */
    @GetMapping(value = "/horarios", produces = {"application/json"})
    ResponseEntity<List<HorarioMedico>> listarHorarios(@RequestParam Long medicoId);

    /**
     * Reemplaza los horarios de un medico (solo administrador).
     *
     * @param medicoId id del medico.
     * @param horarios horarios nuevos.
     * @return respuesta de exito.
     */
    @PutMapping(value = "/horarios", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarHorarios(@RequestParam Long medicoId, @RequestBody List<HorarioMedico> horarios);
}
