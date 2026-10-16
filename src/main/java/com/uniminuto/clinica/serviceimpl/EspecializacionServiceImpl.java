package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
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

    /** Repositorio de especializaciones. */
    @Autowired
    private EspecializacionRepository especializacionRepository;

    @Override
    public List<Especializacion> listarEspecializaciones() {
        return especializacionRepository.findAllByOrderByNombreAsc();
    }
}
