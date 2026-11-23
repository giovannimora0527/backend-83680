package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.model.MedicamentoRq;
import com.uniminuto.clinica.model.RespuestaRs;
import org.apache.coyote.BadRequestException;

import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface MedicamentoService {
    List<Medicamento> listarMedicamentos();

    RespuestaRs guardarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException;

    RespuestaRs actualizarMedicamento(MedicamentoRq medicamentoRq) throws BadRequestException;

    Map<String, Object> previsualizarCarga(MultipartFile archivo) throws Exception;

    RespuestaRs confirmarCarga(com.uniminuto.clinica.model.CargaInventarioConfirmarRq carga) throws Exception;
}
