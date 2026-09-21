package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de las citas.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /**
     * Repositorio de citas.
     */
    @Autowired
    private CitaRepository citaRepository;

    /**
     * Repositorio de clientes.
     */
    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Repositorio de mascotas.
     */
    @Autowired
    private MascotaRepository mascotaRepository;

    /**
     * Repositorio de medicos.
     */
    @Autowired
    private MedicoRepository medicoRepository;

    /**
     * Lista las citas entre dos fechas, de la mas reciente a la mas antigua.
     *
     * @param fechaInicial primer dia del rango.
     * @param fechaFinal   ultimo dia del rango.
     * @return las citas del rango.
     */
    @Override
    public List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        // Paso 1. Validar las fechas
        if (fechaInicial == null || fechaFinal == null) {
            throw new BadRequestException("La fecha inicial y la fecha final son obligatorias");
        }

        if (fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("La fecha inicial no puede ser mayor que la fecha final");
        }

        // Paso 2. El rango va desde el inicio del primer dia hasta el final del ultimo dia
        LocalDateTime inicio = fechaInicial.atStartOfDay();
        LocalDateTime fin = fechaFinal.atTime(23, 59, 59);

        // Paso 3. Consultar las citas ya ordenadas de la mas reciente a la mas antigua
        List<Cita> citas = citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(inicio, fin);

        // Paso 4. Convertir cada cita al objeto de respuesta
        List<CitaRs> respuesta = new ArrayList<>();
        for (Cita cita : citas) {
            CitaRs rs = new CitaRs();
            rs.setId(cita.getId());
            rs.setClienteId(cita.getCliente().getClienteId());
            rs.setClienteNombre(cita.getCliente().getNombres() + " " + cita.getCliente().getApellidos());
            rs.setMascotaId(cita.getMascota().getMascotaId());
            rs.setMascotaNombre(cita.getMascota().getNombreMascota());
            rs.setMedicoId(cita.getMedico().getId());
            rs.setMedicoNombre(cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos());
            rs.setFechaHora(cita.getFechaHora());
            rs.setEstado(cita.getEstado());
            rs.setMotivo(cita.getMotivo());
            respuesta.add(rs);
        }

        return respuesta;
    }

    /**
     * Guarda una cita nueva con estado programada.
     *
     * @param citaRq datos de la cita.
     * @return respuesta de exito.
     */
    @Override
    public MiRespuestaRS guardarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoCita(citaRq);

        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha y hora de la cita no puede estar en el pasado");
        }

        // Paso 2. Buscar el cliente, la mascota y el medico
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());

        // Paso 3. Validar las reglas de la cita
        this.validarMascotaDelCliente(mascota, cliente);
        this.validarMedicoLibre(medico.getId(), citaRq.getFechaHora(), null);

        // Paso 4. Crear la cita para guardar
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado("programada");
        cita.setMotivo(citaRq.getMotivo());

        // Paso 5. Guardar la cita
        this.citaRepository.save(cita);

        // Paso 6. Devolver la respuesta de exito
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cita guardada correctamente");

        return rta;
    }

    /**
     * Actualiza una cita que ya existe.
     *
     * @param citaRq datos de la cita, incluido su id y su estado.
     * @return respuesta de exito.
     */
    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoCita(citaRq);

        if (citaRq.getCitaId() == null) {
            throw new BadRequestException("El ID de la cita no puede ser nulo");
        }

        String estado = citaRq.getEstado();
        if (estado == null || (!estado.equals("programada")
                && !estado.equals("completada") && !estado.equals("cancelada"))) {
            throw new BadRequestException("El estado debe ser programada, completada o cancelada");
        }

        // Paso 2. Buscar la cita, el cliente, la mascota y el medico
        Optional<Cita> optCita = citaRepository.findById(citaRq.getCitaId());
        if (optCita.isEmpty()) {
            throw new BadRequestException("Cita no encontrada");
        }

        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());

        // Paso 3. Validar las reglas de la cita (una cita cancelada no ocupa al medico)
        this.validarMascotaDelCliente(mascota, cliente);
        if (!estado.equals("cancelada")) {
            this.validarMedicoLibre(medico.getId(), citaRq.getFechaHora(), citaRq.getCitaId());
        }

        // Paso 4. Actualizar los datos de la cita
        Cita citaActualizar = optCita.get();
        citaActualizar.setCliente(cliente);
        citaActualizar.setMascota(mascota);
        citaActualizar.setMedico(medico);
        citaActualizar.setFechaHora(citaRq.getFechaHora());
        citaActualizar.setEstado(estado);
        citaActualizar.setMotivo(citaRq.getMotivo());

        // Paso 5. Guardar la cita actualizada
        this.citaRepository.save(citaActualizar);

        // Paso 6. Devolver la respuesta de exito
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage("Cita actualizada correctamente");

        return rta;
    }

    /**
     * Busca un cliente en la base de datos.
     *
     * @param clienteId identificador del cliente.
     * @return el cliente encontrado.
     */
    private Cliente buscarCliente(Long clienteId) {
        Optional<Cliente> optCliente = clienteRepository.findById(clienteId);
        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }
        return optCliente.get();
    }

    /**
     * Busca una mascota en la base de datos.
     *
     * @param mascotaId identificador de la mascota.
     * @return la mascota encontrada.
     */
    private Mascota buscarMascota(Integer mascotaId) {
        Optional<Mascota> optMascota = mascotaRepository.findById(mascotaId);
        if (optMascota.isEmpty()) {
            throw new BadRequestException("Mascota no encontrada");
        }
        return optMascota.get();
    }

    /**
     * Busca un medico en la base de datos.
     *
     * @param medicoId identificador del medico.
     * @return el medico encontrado.
     */
    private Medico buscarMedico(Long medicoId) {
        Optional<Medico> optMedico = medicoRepository.findById(medicoId);
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }
        return optMedico.get();
    }

    /**
     * Valida que la mascota sea del cliente que pide la cita.
     *
     * @param mascota mascota de la cita.
     * @param cliente cliente de la cita.
     */
    private void validarMascotaDelCliente(Mascota mascota, Cliente cliente) {
        if (!mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota no pertenece al cliente");
        }
    }

    /**
     * Valida que el medico no tenga otra cita (que no este cancelada) a la misma fecha y hora.
     *
     * @param medicoId  identificador del medico.
     * @param fechaHora fecha y hora de la cita.
     * @param citaId    id de la cita que se esta actualizando (null si la cita es nueva).
     */
    private void validarMedicoLibre(Long medicoId, LocalDateTime fechaHora, Long citaId) {
        List<Cita> citasDelMedico = citaRepository.findByMedicoIdAndFechaHora(medicoId, fechaHora);

        for (Cita otraCita : citasDelMedico) {
            boolean esOtraCita = !otraCita.getId().equals(citaId);
            boolean estaActiva = !otraCita.getEstado().equals("cancelada");

            if (esOtraCita && estaActiva) {
                throw new BadRequestException("El médico ya tiene una cita a esa fecha y hora");
            }
        }
    }

    /**
     * Valida los datos obligatorios de la cita.
     *
     * @param citaRq objeto a validar.
     */
    private void validarObjetoCita(CitaRq citaRq) {
        if (citaRq == null) {
            throw new BadRequestException("El objeto CitaRq no puede ser nulo");
        }

        if (citaRq.getClienteId() == null || citaRq.getClienteId() <= 0) {
            throw new BadRequestException("El ID del cliente no puede ser nulo o menor que 1");
        }

        if (citaRq.getMascotaId() == null || citaRq.getMascotaId() <= 0) {
            throw new BadRequestException("El ID de la mascota no puede ser nulo o menor que 1");
        }

        if (citaRq.getMedicoId() == null || citaRq.getMedicoId() <= 0) {
            throw new BadRequestException("El ID del médico no puede ser nulo o menor que 1");
        }

        if (citaRq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita no puede ser nula");
        }
    }
}
