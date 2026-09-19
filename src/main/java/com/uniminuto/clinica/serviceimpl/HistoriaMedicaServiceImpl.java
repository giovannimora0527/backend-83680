package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/** Implementa las operaciones de historias médicas. */
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    @Autowired
    /** Repositorio de historias médicas. */
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    /** Repositorio usado para comprobar las anotaciones relacionadas. */
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Override
    // Devuelve las historias médicas ordenadas de la más reciente a la más antigua.
    public List<HistoriaMedica> listarHistoriasMedicas() throws BadRequestException {
        return historiaMedicaRepository.findAllByOrderByFechaCreacionDesc();
    }

    @Override
    // Valida los datos, crea una historia y registra su fecha de creación.
    public MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        validarHistoriaMedica(historiaMedicaRq);

        HistoriaMedica historiaMedica = new HistoriaMedica();
        historiaMedica.setPacienteId(historiaMedicaRq.getPacienteId());
        historiaMedica.setFechaCreacion(LocalDateTime.now());
        historiaMedicaRepository.save(historiaMedica);

        return respuesta("Historia médica creada correctamente");
    }

    @Override
    // Busca una historia existente, modifica sus datos y guarda los cambios.
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
    // Busca y elimina la historia médica indicada.
    public MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException {
        if (historiaMedicaId == null) {
            throw new BadRequestException("El ID de la historia médica es obligatorio para eliminar");
        }

        HistoriaMedica historiaMedica = historiaMedicaRepository.findById(historiaMedicaId)
                .orElseThrow(() -> new BadRequestException("Historia médica no encontrada"));

        // No se puede eliminar una historia que todavía tiene anotaciones.
        if (anotacionHistoriaRepository.existsByHistoriaMedica_Id(historiaMedicaId)) {
            throw new BadRequestException(
                    "No se puede eliminar la historia médica porque tiene anotaciones asignadas");
        }

        historiaMedicaRepository.delete(historiaMedica);

        return respuesta("Historia médica eliminada correctamente");
    }

    private void validarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        // Comprueba que la solicitud tenga un ID de paciente válido.
        if (historiaMedicaRq == null || historiaMedicaRq.getPacienteId() == null
                || historiaMedicaRq.getPacienteId() <= 0) {
            throw new BadRequestException("El ID del paciente es obligatorio y debe ser válido");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        // Crea una respuesta estándar para informar el resultado de la operación.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
