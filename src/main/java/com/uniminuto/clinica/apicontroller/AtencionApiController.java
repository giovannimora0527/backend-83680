package com.uniminuto.clinica.apicontroller;

import com.uniminuto.clinica.api.AtencionApi;
import com.uniminuto.clinica.models.AtencionInfoRs;
import com.uniminuto.clinica.models.AtencionRq;
import com.uniminuto.clinica.models.AtencionRs;
import com.uniminuto.clinica.service.AtencionService;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.util.Descarga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador de la atencion medica, solo delega a los servicios.
 */
@RestController
public class AtencionApiController implements AtencionApi {

    @Autowired
    private AtencionService atencionService;
    @Autowired
    private FormulaMedicaService formulaMedicaService;

    @Override
    public ResponseEntity<AtencionInfoRs> consultarAtencion(Long citaId) {
        return ResponseEntity.ok(atencionService.consultarAtencion(citaId));
    }

    @Override
    public ResponseEntity<AtencionRs> finalizarAtencion(AtencionRq rq) {
        return ResponseEntity.ok(atencionService.finalizarAtencion(rq));
    }

    @Override
    public ResponseEntity<byte[]> prefacturaPdf(Long citaId) {
        return Descarga.archivo(atencionService.prefacturaPdf(citaId), "prefactura_cita_" + citaId + ".pdf", Descarga.PDF);
    }

    @Override
    public ResponseEntity<byte[]> formulaPdf(Long formulaId) {
        return Descarga.archivo(formulaMedicaService.formulaPdf(formulaId), "formula_" + formulaId + ".pdf", Descarga.PDF);
    }
}
