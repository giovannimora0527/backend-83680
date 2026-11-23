package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.CargaInventarioLog;
import com.uniminuto.clinica.models.CargaResultadoRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Rutas de lotes y carga masiva de inventario.
 */
@Tag(name = "Inventario - Lotes y carga masiva", description = "Ingreso de lotes y carga masiva desde CSV/Excel")
@RequestMapping("/inventario")
public interface InventarioApi {

    /**
     * @param rq datos del lote.
     * @return respuesta.
     */
    @Operation(summary = "Ingresar un lote (entrada de inventario)")
    @PostMapping(value = "/lote", produces = "application/json", consumes = "application/json")
    ResponseEntity<MiRespuestaRS> agregarLote(@RequestBody LoteRq rq);

    /**
     * @param archivo csv o xlsx.
     * @return vista previa con errores por fila.
     */
    @Operation(summary = "Vista previa de la carga masiva (no guarda nada)")
    @PostMapping(value = "/carga/previsualizar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    ResponseEntity<CargaResultadoRs> previsualizarCarga(@RequestParam("archivo") MultipartFile archivo);

    /**
     * @param archivo csv o xlsx.
     * @return resultado de la importacion.
     */
    @Operation(summary = "Confirmar la carga masiva (importa solo las filas válidas)")
    @PostMapping(value = "/carga/confirmar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    ResponseEntity<CargaResultadoRs> confirmarCarga(@RequestParam("archivo") MultipartFile archivo);

    /** @return log de cargas. */
    @Operation(summary = "Log de cargas masivas")
    @GetMapping(value = "/carga/logs", produces = "application/json")
    ResponseEntity<List<CargaInventarioLog>> listarCargas();

    /** @return plantilla CSV. */
    @Operation(summary = "Descargar plantilla CSV de la carga masiva")
    @GetMapping(value = "/carga/plantilla")
    ResponseEntity<byte[]> plantilla();
}
