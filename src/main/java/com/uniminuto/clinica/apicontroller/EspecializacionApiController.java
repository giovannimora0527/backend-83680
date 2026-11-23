package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.EspecializacionApi;
import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.EspecializacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador de las especializaciones, solo delega al servicio.
 */
@RestController
public class EspecializacionApiController implements EspecializacionApi {

    @Autowired
    private EspecializacionService especializacionService;

    @Override
    public ResponseEntity<List<Especializacion>> listarEspecializaciones() {
        return ResponseEntity.ok(especializacionService.listarEspecializaciones());
    }

    @Override
    public ResponseEntity<MiRespuestaRS> guardarEspecializacion(Especializacion especializacion) {
        return ResponseEntity.ok(especializacionService.guardarEspecializacion(especializacion));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> actualizarEspecializacion(Especializacion especializacion) {
        return ResponseEntity.ok(especializacionService.actualizarEspecializacion(especializacion));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> cambiarEstado(Long id, Boolean activo) {
        return ResponseEntity.ok(especializacionService.cambiarEstado(id, activo));
    }
}
