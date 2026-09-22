package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la interfaz CitaService para manejar la lógica de negocio de las citas médicas[cite: 21].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Service
public class CitaServiceImpl implements CitaService {

    /** Repositorio JPA para la persistencia de la entidad Cita[cite: 21]. */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Filtra las citas del sistema dado un rango de fechas, ordenadas de la más reciente a la más antigua[cite: 21].
     *
     * @param fechaInicio Fecha y hora inicial de la consulta[cite: 21].
     * @param fechaFin Fecha y hora final de la consulta[cite: 21].
     * @return Lista de citas encontradas dentro del rango especificado[cite: 21].
     */
    @Override
    public List<Cita> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicio, fechaFin);
    }

    /**
     * Registra y almacena una nueva cita médica en el sistema[cite: 21].
     *
     * @param cita Objeto Cita que contiene la información a guardar[cite: 21].
     * @return La cita guardada exitosamente[cite: 21].
     */
    @Override
    public Cita guardarCita(Cita cita) {
        return citaRepository.save(cita);
    }

    /**
     * Actualiza los datos de una cita existente buscando por su identificador[cite: 21].
     *
     * @param id Identificador único de la cita a modificar[cite: 21].
     * @param detallesCita Objeto con los nuevos valores de la cita[cite: 21].
     * @return La cita actualizada en la base de datos[cite: 21].
     * @throws RuntimeException si la cita no existe con el id proporcionado[cite: 21].
     */
    @Override
    public Cita actualizarCita(Long id, Cita detallesCita) {
        Cita citaExistente = citaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con id: " + id));

        citaExistente.setClienteId(detallesCita.getClienteId());
        citaExistente.setMascotaId(detallesCita.getMascotaId());
        citaExistente.setMedicoId(detallesCita.getMedicoId());
        citaExistente.setFechaHora(detallesCita.getFechaHora());
        citaExistente.setEstado(detallesCita.getEstado());
        citaExistente.setMotivo(detallesCita.getMotivo());

        return citaRepository.save(citaExistente);
    }
}