package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
/** Implementa las operaciones de negocio de las citas. */
public class CitaServiceImpl implements CitaService {
    @Autowired
    /** Repositorio de citas. */
    private CitaRepository citaRepository;
    @Autowired
    /** Repositorio de clientes. */
    private ClienteRepository clienteRepository;
    @Autowired
    /** Repositorio de mascotas. */
    private MascotaRepository mascotaRepository;
    @Autowired
    /** Repositorio de médicos. */
    private MedicoRepository medicoRepository;

    /** Recibe los repositorios necesarios para operar. */
    public CitaServiceImpl(CitaRepository citaRepository, ClienteRepository clienteRepository,
                           MascotaRepository mascotaRepository, MedicoRepository medicoRepository) {
        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
    }

    @Override
    // Consulta las citas que pertenecen al rango de fechas recibido.
    public List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        validarRangoFechas(fechaInicial, fechaFinal);
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicial, fechaFinal);
    }

    @Override
    // Valida la solicitud, crea la cita y la guarda en la base de datos.
    public MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        Cita cita = new Cita();
        asignarDatos(cita, citaRq);
        citaRepository.save(cita);
        return respuesta("Cita creada correctamente");
    }

    @Override
    // Busca la cita existente, actualiza sus datos y guarda los cambios.
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        if (citaRq.getCitaId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para actualizar");
        }
        Cita cita = citaRepository.findById(citaRq.getCitaId())
                .orElseThrow(() -> new BadRequestException("Cita no encontrada"));
        asignarDatos(cita, citaRq);
        citaRepository.save(cita);
        return respuesta("Cita actualizada correctamente");
    }

    private void asignarDatos(Cita cita, CitaRq citaRq) throws BadRequestException {
        // Busca cliente, mascota y médico antes de relacionarlos con la cita.
        Cliente cliente = clienteRepository.findById(citaRq.getClienteId())
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(citaRq.getMascotaId())
                .orElseThrow(() -> new BadRequestException("Mascota no encontrada"));
        Medico medico = medicoRepository.findById(citaRq.getMedicoId())
                .orElseThrow(() -> new BadRequestException("Médico no encontrado"));
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(citaRq.getEstado().trim());
        cita.setMotivo(citaRq.getMotivo().trim());
    }

    private void validarCita(CitaRq citaRq) throws BadRequestException {
        // Comprueba que todos los datos obligatorios de la cita estén presentes.
        if (citaRq == null || citaRq.getClienteId() == null || citaRq.getMascotaId() == null
                || citaRq.getMedicoId() == null || citaRq.getFechaHora() == null
                || citaRq.getEstado() == null || citaRq.getEstado().isBlank()
                || citaRq.getMotivo() == null || citaRq.getMotivo().isBlank()) {
            throw new BadRequestException("Cliente, mascota, médico, fecha, estado y motivo son obligatorios");
        }
    }

    private void validarRangoFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        // Evita consultar con fechas vacías o en un orden incorrecto.
        if (fechaInicial == null || fechaFinal == null || fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("El rango de fechas no es válido");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        // Devuelve el formato de respuesta usado por las operaciones exitosas.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
