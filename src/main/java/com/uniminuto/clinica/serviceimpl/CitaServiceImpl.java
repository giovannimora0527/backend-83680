package com.uniminuto.clinica.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.service.CitaService;

/**
 * Implementa las operaciones relacionadas con las citas de la clínica.
 */
@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    /**
     * Obtiene las citas que se encuentran dentro de un rango de fechas,
     * ordenadas desde la más reciente hasta la más antigua.
     *
     * @param fechaInicial fecha inicial del rango de búsqueda.
     * @param fechaFinal fecha final del rango de búsqueda.
     * @return lista de citas encontradas.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public List<Cita> obtenerCitasPorFecha(
            LocalDateTime fechaInicial,
            LocalDateTime fechaFinal) throws BadRequestException {

        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                fechaInicial,
                fechaFinal
        );
    }

    /**
     * Crea una nueva cita con los datos recibidos.
     *
     * @param citaRq datos de la cita que se desea crear.
     * @return cita creada.
     * @throws BadRequestException si ocurre un error en la solicitud.
     */
    @Override
    public Cita crearCita(CitaRq citaRq) throws BadRequestException {

        Cita cita = new Cita();

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());

        return citaRepository.save(cita);
    }

    /**
     * Actualiza una cita existente.
     *
     * @param id identificador de la cita que se desea actualizar.
     * @param citaRq nuevos datos de la cita.
     * @return cita actualizada.
     * @throws BadRequestException si la cita no existe.
     */
    @Override
    public Cita actualizarCita(
            Long id,
            CitaRq citaRq) throws BadRequestException {

        Cita cita = citaRepository.findById(id)
                .orElseThrow(() ->
                        new BadRequestException("La cita no existe"));

        cita.setClienteId(citaRq.getClienteId());
        cita.setMascotaId(citaRq.getMascotaId());
        cita.setMedicoId(citaRq.getMedicoId());
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado());
        cita.setMotivo(citaRq.getMotivo());

        return citaRepository.save(cita);
    }
}