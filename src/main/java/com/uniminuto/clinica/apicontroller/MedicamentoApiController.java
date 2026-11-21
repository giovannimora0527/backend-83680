package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicamentoApi;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaMedicamentoRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.LoteMedicamentoRq;
import com.uniminuto.clinica.models.LoteMedicamentoRS;
import com.uniminuto.clinica.service.MedicamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

@RestController
/** Controlador que expone el inventario de medicamentos. */
public class MedicamentoApiController implements MedicamentoApi {

    @Autowired
    /** Servicio con la lógica del inventario. */
    private MedicamentoService medicamentoService;

    @Override
    /** Delega la consulta de medicamentos. */
    public ResponseEntity<List<Medicamento>> listarMedicamentos() throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.obtenerMedicamentos());
    }

    @Override
    /** Delega la consulta de un medicamento por identificador. */
    public ResponseEntity<Medicamento> consultarMedicamento(Long medicamentoId) throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.consultarMedicamento(medicamentoId));
    }

    @Override
    /** Delega la creación de un medicamento. */
    public ResponseEntity<MiRespuestaRS> crearMedicamento(Medicamento medicamento) throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.guardarMedicamento(medicamento));
    }

    @Override
    /** Delega la actualización de un medicamento. */
    public ResponseEntity<MiRespuestaRS> actualizarMedicamento(Medicamento medicamento) throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.actualizarMedicamento(medicamento));
    }

    @Override
    /** Delega la carga masiva de inventario. */
    public ResponseEntity<CargaMasivaMedicamentoRS> importarMasivo(
            MultipartFile archivo, boolean confirmar, HttpServletRequest request) throws BadRequestException {
        String usuario = (String) request.getAttribute("authenticatedUsername");
        return ResponseEntity.ok(this.medicamentoService.importarMasivo(archivo, confirmar, usuario));
    }

    @Override
    /** Registra una salida del inventario y guarda el movimiento asociado. */
    public ResponseEntity<MiRespuestaRS> registrarSalidaInventario(Long medicamentoId, Integer cantidad, String motivo,
                                                                 Long referenciaId, String referenciaTipo) throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.registrarSalidaInventario(medicamentoId, cantidad, motivo, referenciaId, referenciaTipo));
    }

    @Override
    /** Consulta el historial de movimientos del medicamento. */
    public ResponseEntity<List<MovimientoMedicamento>> consultarHistorialInventario(Long medicamentoId) throws BadRequestException {
        return ResponseEntity.ok(this.medicamentoService.consultarHistorialInventario(medicamentoId));
    }

    @Override
    public ResponseEntity<List<LoteMedicamentoRS>> listarLotes(Long medicamentoId) {
        return ResponseEntity.ok(medicamentoService.listarLotes(medicamentoId));
    }

    @Override
    public ResponseEntity<MiRespuestaRS> registrarLote(Long medicamentoId, LoteMedicamentoRq lote) {
        return ResponseEntity.status(201).body(medicamentoService.registrarLote(medicamentoId, lote));
    }
}
