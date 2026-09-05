package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.MedicoApi;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.service.MedicoService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MedicoApiController implements MedicoApi {

    @Autowired
    private MedicoService medicoService;


    @Override
    public ResponseEntity<List<Medico>> listarMedicos() throws BadRequestException {
        return ResponseEntity.ok(this.medicoService.listarMedicos());
    }
}
