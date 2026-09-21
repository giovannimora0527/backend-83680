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

/**
 * Implementación de la lógica de negocio para Citas.
 * Gestiona las operaciones de filtrado, creación y actualización de citas.
 *
 * @author Alma Hernandez
 */

@Service

public class CitaServiceImpl implements CitaService {
    /**
     * Repositorio inyectado para acceder a los datos de citas.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Repositorio inyectado para acceder a los datos de citas en la base de datos.
     */
    @Override
    public List<Cita> filtrarCitasPorFecha(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin) throws BadRequestException {
        try {
            if (fechaInicio == null || fechaFin == null) {
                throw new BadRequestException("Las fechas de inicio y fin son obligatorias");
            }

            if (fechaInicio.isAfter(fechaFin)) {
                throw new BadRequestException("La fecha de inicio no puede ser posterior a la fecha de fin");
            }

            return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al filtrar las citas: " + e.getMessage());
        }
    }

    /**
     * Registra una nueva cita en el sistema.
     */
    @Override
    public MiRespuestaRS guardarCita(CitaRq citaRq) throws BadRequestException {
        try {
            // Validación de campos obligatorios
            if (citaRq.getFechaHora() == null || citaRq.getEstado() == null) {
                throw new BadRequestException("La fecha y el estado son obligatorios");
            }

            // Mapear Request a Entidad
            Cita cita = new Cita();
            cita.setClienteId(citaRq.getClienteId());
            cita.setMascotaId(citaRq.getMascotaId());
            cita.setMedicoId(citaRq.getMedicoId());
            cita.setFechaCita(citaRq.getFechaHora());
            cita.setEstado(citaRq.getEstado());
            cita.setMotivo(citaRq.getMotivo());

            // Guardar en BD
            citaRepository.save(cita);

            // Respuesta exitosa
            MiRespuestaRS respuesta = new MiRespuestaRS();
            respuesta.setStatus(200);
            respuesta.setMessage("Cita creada exitosamente");
            return respuesta;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al guardar la cita: " + e.getMessage());
        }
    }

    /**
     * Actualiza la información de una cita existente en el sistema.
     */
    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        try {
            // Validar que exista el ID
            if (citaRq.getId() == null) {
                throw new BadRequestException("El ID de la cita es obligatorio para actualizar");
            }

            // Buscar cita existente
            Optional<Cita> citaExistente = citaRepository.findById(citaRq.getId());
            if (!citaExistente.isPresent()) {
                throw new BadRequestException("No se encontró la cita con ID: " + citaRq.getId());
            }

            // Actualizar campos
            Cita cita = citaExistente.get();
            cita.setClienteId(citaRq.getClienteId());
            cita.setMascotaId(citaRq.getMascotaId());
            cita.setMedicoId(citaRq.getMedicoId());
            cita.setFechaCita(citaRq.getFechaHora());
            cita.setEstado(citaRq.getEstado());
            cita.setMotivo(citaRq.getMotivo());

            // Guardar cambios
            citaRepository.save(cita);

            // Respuesta exitosa
            MiRespuestaRS respuesta = new MiRespuestaRS();
            respuesta.setStatus(200);
            respuesta.setMessage("Cita actualizada exitosamente");
            return respuesta;
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("Error al actualizar la cita: " + e.getMessage());
        }
    }
}