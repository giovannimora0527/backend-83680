package com.uniminuto.clinica.api;

import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.RazaRq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.uniminuto.clinica.entity.Raza;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Contrato de los servicios web de las razas.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/raza")
public interface RazaApi {

    /**
     * Crea una nueva raza.
     *
     * @param razaRq datos de la raza.
     * @return respuesta de éxito.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarRaza(
            @RequestBody RazaRq razaRq);

    /**
     * Lista todas las razas (para el combo de mascotas).
     *
     * @return lista de razas.
     */
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Raza>> listarRazas();
}
