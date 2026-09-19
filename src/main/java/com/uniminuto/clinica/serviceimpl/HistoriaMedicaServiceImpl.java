package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    @Autowired
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Override
    public List<HistoriaMedica> listarHistoriasMedicas() throws BadRequestException {
        return historiaMedicaRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    public MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        validarHistoriaMedica(historiaMedicaRq);

        HistoriaMedica historiaMedica = new HistoriaMedica();
        historiaMedica.setPacienteId(historiaMedicaRq.getPacienteId());
        historiaMedica.setFechaCreacion(LocalDateTime.now());
        historiaMedicaRepository.save(historiaMedica);

        return respuesta("Historia médica creada correctamente");
    }

    @Override
    public MiRespuestaRS actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        validarHistoriaMedica(historiaMedicaRq);
        if (historiaMedicaRq.getHistoriaMedicaId() == null) {
            throw new BadRequestException("El ID de la historia médica es obligatorio para actualizar");
        }

        HistoriaMedica historiaMedica = historiaMedicaRepository.findById(historiaMedicaRq.getHistoriaMedicaId())
                .orElseThrow(() -> new BadRequestException("Historia médica no encontrada"));
        historiaMedica.setPacienteId(historiaMedicaRq.getPacienteId());
        historiaMedicaRepository.save(historiaMedica);

        return respuesta("Historia médica actualizada correctamente");
    }

    @Override
    public MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException {
        if (historiaMedicaId == null) {
            throw new BadRequestException("El ID de la historia médica es obligatorio para eliminar");
        }

        HistoriaMedica historiaMedica = historiaMedicaRepository.findById(historiaMedicaId)
                .orElseThrow(() -> new BadRequestException("Historia médica no encontrada"));
        historiaMedicaRepository.delete(historiaMedica);

        return respuesta("Historia médica eliminada correctamente");
    }

    private void validarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        if (historiaMedicaRq == null || historiaMedicaRq.getPacienteId() == null
                || historiaMedicaRq.getPacienteId() <= 0) {
            throw new BadRequestException("El ID del paciente es obligatorio y debe ser válido");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
