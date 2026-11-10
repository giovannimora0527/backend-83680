package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaMedicamentoRS;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Define las operaciones de negocio para el inventario de medicamentos. */
public interface MedicamentoService {

    /** Lista los medicamentos registrados en el inventario. */
    List<Medicamento> obtenerMedicamentos() throws BadRequestException;

    /** Consulta un medicamento por su identificador. */
    Medicamento consultarMedicamento(Long medicamentoId) throws BadRequestException;

    /** Registra un nuevo medicamento y valida su integridad. */
    MiRespuestaRS guardarMedicamento(Medicamento medicamento) throws BadRequestException;

    /** Actualiza la información de un medicamento existente. */
    MiRespuestaRS actualizarMedicamento(Medicamento medicamento) throws BadRequestException;

    /** Importa medicamentos desde un archivo CSV o Excel validando la estructura y los datos. */
    CargaMasivaMedicamentoRS importarMasivo(MultipartFile archivo) throws BadRequestException;

    /** Registra una salida del inventario y genera el movimiento asociado. */
    MiRespuestaRS registrarSalidaInventario(Long medicamentoId, Integer cantidad, String motivo, Long referenciaId, String referenciaTipo) throws BadRequestException;

    /** Consulta el historial de movimientos del medicamento. */
    List<MovimientoMedicamento> consultarHistorialInventario(Long medicamentoId) throws BadRequestException;
}
