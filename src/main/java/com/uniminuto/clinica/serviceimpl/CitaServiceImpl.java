package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CitaServiceImpl implements CitaService {
    
    @Autowired
    private CitaRepository citaRepository;

    @Override
    public List<Cita> filtrarCitasPorFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws BadRequestException {
        if (fechaInicio == null || fechaFin == null) {
            throw new BadRequestException("Las fechas de inicio y fin son obligatorias");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return citaRepository.findByFechaCitaBetweenOrderByFechaCitaDesc(fechaInicio, fechaFin);
    }

    @Override
    public MiRespuestaRS guardarCita(CitaRq citaRq) throws BadRequestException {
        if (citaRq.getFechaHora() == null || citaRq.getEstado() == null) {
            throw new BadRequestException("La fecha y el estado son obligatorios");
        }
        Cita cita = new Cita();
        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaCita(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());
        
        citaRepository.save(cita);
        
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Cita creada exitosamente");
        return respuesta;
    }

    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        if (citaRq.getId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para actualizar");
        }
        Optional<Cita> citaExistente = citaRepository.findById(citaRq.getId());
        if (!citaExistente.isPresent()) {
            throw new BadRequestException("No se encontró la cita con ID: " + citaRq.getId());
        }
        
        Cita cita = citaExistente.get();
        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaCita(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());
        
        citaRepository.save(cita);
        
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage("Cita actualizada exitosamente");
        return respuesta;
    }
}