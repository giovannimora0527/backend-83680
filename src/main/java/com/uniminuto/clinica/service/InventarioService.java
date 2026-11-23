package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.CargaInventarioLog;
import com.uniminuto.clinica.entity.MedicamentoLote;
import com.uniminuto.clinica.models.LoteRs;
import com.uniminuto.clinica.models.CargaResultadoRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoRs;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Logica del modulo de inventario de medicamentos y productos.
 */
public interface InventarioService {

    /**
     * Lista los medicamentos con existencias, precios y alertas.
     *
     * @return inventario.
     */
    List<MedicamentoRs> listarInventario();

    /**
     * Consulta un medicamento con todos sus lotes.
     *
     * @param medicamentoId id.
     * @return detalle.
     */
    MedicamentoRs consultarMedicamento(Long medicamentoId);

    /**
     * Crea un medicamento o producto (opcionalmente con su primer lote).
     *
     * @param rq datos.
     * @return respuesta.
     */
    MiRespuestaRS guardarMedicamento(MedicamentoRq rq);

    /**
     * Edita los datos de un medicamento (las cantidades solo cambian con lotes o despachos).
     *
     * @param rq datos.
     * @return respuesta.
     */
    MiRespuestaRS actualizarMedicamento(MedicamentoRq rq);

    /**
     * Agrega un lote (entrada de inventario).
     *
     * @param rq datos del lote.
     * @return respuesta.
     */
    MiRespuestaRS agregarLote(LoteRq rq);

    /**
     * Historial de entradas y salidas de un medicamento.
     *
     * @param medicamentoId id.
     * @return movimientos.
     */
    List<MovimientoRs> listarMovimientos(Long medicamentoId);

    /**
     * Unidades que se pueden dispensar (lotes vigentes).
     *
     * @param medicamentoId id.
     * @return cantidad disponible.
     */
    int stockDisponible(Long medicamentoId);

    /**
     * Valida un archivo de carga masiva sin guardar nada (vista previa).
     *
     * @param archivo csv o xlsx.
     * @return filas con sus errores.
     */
    CargaResultadoRs previsualizarCarga(MultipartFile archivo);

    /**
     * Importa las filas validas del archivo (cada fila en su propia transaccion) y guarda el log.
     *
     * @param archivo csv o xlsx.
     * @return resultado.
     */
    CargaResultadoRs confirmarCarga(MultipartFile archivo);

    /**
     * Log de cargas realizadas.
     *
     * @return logs.
     */
    List<CargaInventarioLog> listarCargas();

    /**
     * Plantilla CSV con los encabezados esperados.
     *
     * @return contenido del archivo.
     */
    byte[] plantillaCarga();

    /**
     * Convierte un lote y calcula su estado (VIGENTE, POR_VENCER, VENCIDO, AGOTADO).
     *
     * @param lote entidad.
     * @return lote para mostrar.
     */
    LoteRs convertirLote(MedicamentoLote lote);
}
