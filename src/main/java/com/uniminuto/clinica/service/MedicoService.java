package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Medico;
import java.util.List;
import org.apache.coyote.BadRequestException;

/**
 *
 * @author lmora
 */
public interface MedicoService {
   List<Medico> listarMedicos(); 
   
   List<Medico> buscarPorEspecialidad(String codigo) 
           throws BadRequestException;
}
