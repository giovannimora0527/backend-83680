package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.InventarioApi;
import com.uniminuto.clinica.entity.CargaInventarioLog;
import com.uniminuto.clinica.models.CargaResultadoRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controlador de lotes y carga masiva, solo delega al servicio.
 */
@RestController
public class InventarioApiController implements InventarioApi {

    @Autowired
    private InventarioService inventarioService;

    @Override
    public ResponseEntity<MiRespuestaRS> agregarLote(LoteRq rq) {
        return ResponseEntity.ok(inventarioService.agregarLote(rq));
    }

    @Override
    public ResponseEntity<CargaResultadoRs> previsualizarCarga(MultipartFile archivo) {
        return ResponseEntity.ok(inventarioService.previsualizarCarga(archivo));
    }

    @Override
    public ResponseEntity<CargaResultadoRs> confirmarCarga(MultipartFile archivo) {
        return ResponseEntity.ok(inventarioService.confirmarCarga(archivo));
    }

    @Override
    public ResponseEntity<List<CargaInventarioLog>> listarCargas() {
        return ResponseEntity.ok(inventarioService.listarCargas());
    }

    @Override
    public ResponseEntity<byte[]> plantilla() {
        return Descarga.archivo(inventarioService.plantillaCarga(), "plantilla_inventario.csv", Descarga.CSV);
    }
}
