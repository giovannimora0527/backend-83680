package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.service.EspecializacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementacion del servicio de especializaciones.
 */
@Service
public class EspecializacionServiceImpl implements EspecializacionService {

    @Autowired
    private EspecializacionRepository especializacionRepository;

    @Override
    public List<Especializacion> listarEspecializaciones() {
        return especializacionRepository.findAllByOrderByNombreAsc();
    }

    @Override
    public MiRespuestaRS guardarEspecializacion(Especializacion rq) {
        this.validar(rq, null);
        Especializacion nueva = new Especializacion();
        nueva.setNombre(rq.getNombre().trim());
        nueva.setDescripcion(rq.getDescripcion());
        nueva.setCodigoEspecializacion(rq.getCodigoEspecializacion().trim().toUpperCase());
        nueva.setActivo(true);
        especializacionRepository.save(nueva);
        return this.exito("Especialidad registrada");
    }

    @Override
    public MiRespuestaRS actualizarEspecializacion(Especializacion rq) {
        Especializacion actual = this.buscar(rq == null ? null : rq.getId());
        this.validar(rq, actual.getId());
        actual.setNombre(rq.getNombre().trim());
        actual.setDescripcion(rq.getDescripcion());
        actual.setCodigoEspecializacion(rq.getCodigoEspecializacion().trim().toUpperCase());
        especializacionRepository.save(actual);
        return this.exito("Especialidad actualizada");
    }

    @Override
    public MiRespuestaRS cambiarEstado(Long id, Boolean activo) {
        Especializacion actual = this.buscar(id);
        actual.setActivo(Boolean.TRUE.equals(activo));
        especializacionRepository.save(actual);
        return this.exito(Boolean.TRUE.equals(activo) ? "Especialidad activada" : "Especialidad inactivada");
    }

    /**
     * Valida campos y que no se repitan nombre ni codigo.
     */
    private void validar(Especializacion rq, Long idActual) {
        if (rq == null || rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        if (rq.getCodigoEspecializacion() == null || !rq.getCodigoEspecializacion().trim().matches("[A-Za-z0-9_-]{2,10}")) {
            throw new BadRequestException("El código debe tener entre 2 y 10 caracteres (letras, números, _ o -)");
        }
        for (Especializacion e : especializacionRepository.findAll()) {
            if (e.getId().equals(idActual)) {
                continue;
            }
            if (e.getNombre().equalsIgnoreCase(rq.getNombre().trim())) {
                throw new BadRequestException("Ya existe una especialidad con ese nombre");
            }
            if (e.getCodigoEspecializacion().equalsIgnoreCase(rq.getCodigoEspecializacion().trim())) {
                throw new BadRequestException("Ya existe una especialidad con ese código");
            }
        }
    }

    private Especializacion buscar(Long id) {
        if (id == null) {
            throw new BadRequestException("El id es obligatorio");
        }
        return especializacionRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Especialidad no encontrada"));
    }

    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
