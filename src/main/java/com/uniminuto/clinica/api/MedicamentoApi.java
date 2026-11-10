package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaMedicamentoRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/medicamento")
public interface MedicamentoApi {

    // Lista todos los medicamentos registrados en el inventario.
    @GetMapping(value = "/listar", produces = {"application/json"})
    ResponseEntity<List<Medicamento>> listarMedicamentos() throws BadRequestException;

    // Consulta información del medicamento y su stock disponible.
    @GetMapping(value = "/{medicamentoId}", produces = {"application/json"})
    ResponseEntity<Medicamento> consultarMedicamento(@PathVariable Long medicamentoId) throws BadRequestException;

    // Registra un nuevo medicamento con validaciones de inventario.
    @PostMapping(value = "/guardar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> crearMedicamento(@RequestBody Medicamento medicamento) throws BadRequestException;

    // Actualiza la información de un medicamento existente.
    @PutMapping(value = "/actualizar", produces = {"application/json"}, consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMedicamento(@RequestBody Medicamento medicamento) throws BadRequestException;

    // Carga masiva desde CSV o XLSX con validación previa y reporte por fila.
    @PostMapping(value = "/carga-masiva", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = {"application/json"})
    ResponseEntity<CargaMasivaMedicamentoRS> importarMasivo(@RequestParam("archivo") MultipartFile archivo) throws BadRequestException;

    // Descuenta la cantidad indicada del inventario y registra la salida.
    @PostMapping(value = "/{medicamentoId}/salida", produces = {"application/json"})
    ResponseEntity<MiRespuestaRS> registrarSalidaInventario(@PathVariable Long medicamentoId,
                                                         @RequestParam Integer cantidad,
                                                         @RequestParam(required = false) String motivo,
                                                         @RequestParam(required = false) Long referenciaId,
                                                         @RequestParam(required = false) String referenciaTipo) throws BadRequestException;

    // Consulta el historial de entradas y salidas de inventario por medicamento.
    @GetMapping(value = "/{medicamentoId}/historial", produces = {"application/json"})
    ResponseEntity<List<MovimientoMedicamento>> consultarHistorialInventario(@PathVariable Long medicamentoId) throws BadRequestException;
}
