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
 *
 * Estados de una cita: pendiente, confirmada, programada, atendida y cancelada.
 * - Una cita cancelada no se puede editar, primero hay que reactivarla (operacion explicita).
 * - Una cita atendida no se puede editar porque ya es informacion historica.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /** Estados que puede tener una cita. */
    private static final List<String> ESTADOS_VALIDOS =
            List.of("pendiente", "confirmada", "programada", "atendida", "cancelada");

    /** Estados permitidos al crear una cita nueva. */
    private static final List<String> ESTADOS_CREACION =
            List.of("pendiente", "confirmada", "programada");

    /** Minutos de separacion minima entre dos citas del mismo medico o mascota. */
    private static final int MINUTOS_SEPARACION = 15;

    /** Repositorio de citas. */
    @Autowired
    private CitaRepository citaRepository;

    /** Repositorio de clientes. */
    @Autowired
    private ClienteRepository clienteRepository;

    /** Repositorio de mascotas. */
    @Autowired
    private MascotaRepository mascotaRepository;

    /** Repositorio de medicos. */
    @Autowired
    private MedicoRepository medicoRepository;

    @Override
    public List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        List<Cita> citas;
        // Paso 1. Si no mandan fechas se listan todas
        if (fechaInicial == null && fechaFinal == null) {
            citas = citaRepository.findAllByOrderByFechaHoraDesc();
        } else {
            // Paso 2. Validar las fechas
            if (fechaInicial == null || fechaFinal == null) {
                throw new BadRequestException("Debe enviar la fecha inicial y la fecha final");
            }
            if (fechaInicial.isAfter(fechaFinal)) {
                throw new BadRequestException("La fecha inicial no puede ser mayor que la fecha final");
            }
            // Paso 3. El rango va desde el inicio del primer dia hasta el final del ultimo dia
            citas = citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                    fechaInicial.atStartOfDay(), fechaFinal.atTime(23, 59, 59));
        }
        // Paso 4. Convertir cada cita al objeto de respuesta
        return this.convertirLista(citas);
    }

    @Override
    public List<CitaRs> listarCitasPorMascota(Integer mascotaId) {
        // Valida que la mascota exista antes de buscar sus citas
        this.buscarMascota(mascotaId);
        return this.convertirLista(citaRepository.findByMascotaMascotaIdOrderByFechaHoraDesc(mascotaId));
    }

    @Override
    public CitaRs consultarCita(Long citaId) {
        return this.convertirCita(this.buscarCita(citaId));
    }

    @Override
    public Cita buscarCita(Long citaId) {
        if (citaId == null || citaId <= 0) {
            throw new BadRequestException("El ID de la cita no puede ser nulo o menor que 1");
        }
        Optional<Cita> optCita = citaRepository.findById(citaId);
        if (optCita.isEmpty()) {
            throw new BadRequestException("Cita no encontrada");
        }
        return optCita.get();
    }

    @Override
    public CitaRs convertirCita(Cita cita) {
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
        return rs;
    }

    @Override
    public MiRespuestaRS guardarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoCita(citaRq);
        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden registrar citas en fechas u horas pasadas");
        }
        // Si no mandan estado queda programada
        String estado = citaRq.getEstado() == null || citaRq.getEstado().isBlank()
                ? "programada" : citaRq.getEstado().toLowerCase();
        if (!ESTADOS_CREACION.contains(estado)) {
            throw new BadRequestException("Una cita nueva solo puede estar pendiente, confirmada o programada");
        }

        // Paso 2. Buscar el cliente, la mascota y el medico
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());

        // Paso 3. Validar las reglas de la cita
        this.validarMascotaDelCliente(mascota, cliente);
        this.validarMedicoLibre(medico.getId(), citaRq.getFechaHora(), null);
        this.validarMascotaLibre(mascota.getMascotaId(), citaRq.getFechaHora(), null);

        // Paso 4. Crear la cita para guardar
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo());

        // Paso 5. Guardar la cita
        this.citaRepository.save(cita);
        return this.respuestaExito("Cita guardada correctamente");
    }

    @Override
    public MiRespuestaRS actualizarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoCita(citaRq);
        String estado = citaRq.getEstado() == null ? null : citaRq.getEstado().toLowerCase();
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException("El estado debe ser pendiente, confirmada, programada, atendida o cancelada");
        }

        // Paso 2. Buscar la cita y revisar si su estado actual permite editarla
        Cita citaActualizar = this.buscarCita(citaRq.getCitaId());
        if (citaActualizar.getEstado().equals("cancelada")) {
            throw new BadRequestException("La cita está cancelada. Si desea usarla de nuevo primero debe reactivarla");
        }
        if (citaActualizar.getEstado().equals("atendida")) {
            throw new BadRequestException("La cita ya fue atendida y su información no se puede modificar");
        }

        // Si cambian la fecha, la nueva no puede estar en el pasado
        boolean cambioFecha = !citaActualizar.getFechaHora().equals(citaRq.getFechaHora());
        if (cambioFecha && citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede mover la cita a una fecha u hora pasada");
        }

        // Paso 3. Buscar el cliente, la mascota y el medico
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());

        // Paso 4. Validar las reglas (una cita cancelada no ocupa al medico ni a la mascota)
        this.validarMascotaDelCliente(mascota, cliente);
        if (!estado.equals("cancelada")) {
            this.validarMedicoLibre(medico.getId(), citaRq.getFechaHora(), citaActualizar.getId());
            this.validarMascotaLibre(mascota.getMascotaId(), citaRq.getFechaHora(), citaActualizar.getId());
        }

        // Paso 5. Actualizar los datos de la cita
        citaActualizar.setCliente(cliente);
        citaActualizar.setMascota(mascota);
        citaActualizar.setMedico(medico);
        citaActualizar.setFechaHora(citaRq.getFechaHora());
        citaActualizar.setEstado(estado);
        citaActualizar.setMotivo(citaRq.getMotivo());

        // Paso 6. Guardar la cita actualizada
        this.citaRepository.save(citaActualizar);
        return this.respuestaExito("Cita actualizada correctamente");
    }

    @Override
    public MiRespuestaRS reactivarCita(Long citaId) {
        // Paso 1. Solo se reactivan citas canceladas
        Cita cita = this.buscarCita(citaId);
        if (!cita.getEstado().equals("cancelada")) {
            throw new BadRequestException("Solo se pueden reactivar citas canceladas");
        }
        // Paso 2. La fecha debe seguir siendo futura y el horario debe estar libre
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la cita ya pasó, debe crear una cita nueva");
        }
        this.validarMedicoLibre(cita.getMedico().getId(), cita.getFechaHora(), cita.getId());
        this.validarMascotaLibre(cita.getMascota().getMascotaId(), cita.getFechaHora(), cita.getId());

        // Paso 3. Queda pendiente
        cita.setEstado("pendiente");
        this.citaRepository.save(cita);
        return this.respuestaExito("Cita reactivada correctamente, quedó en estado pendiente");
    }

    /**
     * Convierte una lista de entidades a objetos de respuesta.
     *
     * @param citas entidades.
     * @return lista de respuesta.
     */
    private List<CitaRs> convertirLista(List<Cita> citas) {
        List<CitaRs> respuesta = new ArrayList<>();
        for (Cita cita : citas) {
            respuesta.add(this.convertirCita(cita));
        }
        return respuesta;
    }

    /**
     * Busca un cliente, lanza error si no existe.
     *
     * @param clienteId id del cliente.
     * @return el cliente.
     */
    private Cliente buscarCliente(Long clienteId) {
        Optional<Cliente> optCliente = clienteRepository.findById(clienteId);
        if (optCliente.isEmpty()) {
            throw new BadRequestException("Cliente no encontrado");
        }
        return optCliente.get();
    }

    /**
     * Busca una mascota, lanza error si no existe.
     *
     * @param mascotaId id de la mascota.
     * @return la mascota.
     */
    private Mascota buscarMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota no puede ser nulo");
        }
        Optional<Mascota> optMascota = mascotaRepository.findById(mascotaId);
        if (optMascota.isEmpty()) {
            throw new BadRequestException("Mascota no encontrada");
        }
        return optMascota.get();
    }

    /**
     * Busca un medico, lanza error si no existe.
     *
     * @param medicoId id del medico.
     * @return el medico.
     */
    private Medico buscarMedico(Long medicoId) {
        Optional<Medico> optMedico = medicoRepository.findById(medicoId);
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }
        return optMedico.get();
    }

    /**
     * Valida que la mascota sea del cliente.
     *
     * @param mascota mascota seleccionada.
     * @param cliente cliente seleccionado.
     */
    private void validarMascotaDelCliente(Mascota mascota, Cliente cliente) {
        if (!mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota no pertenece al cliente seleccionado");
        }
    }

    /**
     * Valida que el medico no tenga otra cita activa 15 minutos antes o despues.
     *
     * @param medicoId  id del medico.
     * @param fechaHora fecha y hora de la cita.
     * @param citaId    id de la cita que se esta editando (null si es nueva).
     */
    private void validarMedicoLibre(Long medicoId, LocalDateTime fechaHora, Long citaId) {
        List<Cita> citasDelMedico = citaRepository.findByMedicoIdAndFechaHoraBetween(medicoId,
                fechaHora.minusMinutes(MINUTOS_SEPARACION), fechaHora.plusMinutes(MINUTOS_SEPARACION));
        if (this.hayOtraCitaActiva(citasDelMedico, citaId)) {
            throw new BadRequestException("El veterinario no está disponible: ya tiene una cita 15 minutos antes o después de esa hora");
        }
    }

    /**
     * Valida que la mascota no tenga otra cita activa al mismo tiempo (15 minutos antes o despues).
     *
     * @param mascotaId id de la mascota.
     * @param fechaHora fecha y hora de la cita.
     * @param citaId    id de la cita que se esta editando (null si es nueva).
     */
    private void validarMascotaLibre(Integer mascotaId, LocalDateTime fechaHora, Long citaId) {
        List<Cita> citasDeLaMascota = citaRepository.findByMascotaMascotaIdAndFechaHoraBetween(mascotaId,
                fechaHora.minusMinutes(MINUTOS_SEPARACION), fechaHora.plusMinutes(MINUTOS_SEPARACION));
        if (this.hayOtraCitaActiva(citasDeLaMascota, citaId)) {
            throw new BadRequestException("La mascota ya tiene otra cita a esa misma hora");
        }
    }

    /**
     * Revisa si en la lista hay una cita distinta a la que se edita y que no este cancelada.
     *
     * @param citas  citas encontradas en el rango.
     * @param citaId id de la cita que se edita (puede ser null).
     * @return true si hay choque.
     */
    private boolean hayOtraCitaActiva(List<Cita> citas, Long citaId) {
        for (Cita otraCita : citas) {
            boolean esOtraCita = !otraCita.getId().equals(citaId);
            boolean estaActiva = !otraCita.getEstado().equals("cancelada");
            if (esOtraCita && estaActiva) {
                return true;
            }
        }
        return false;
    }

    /**
     * Valida los campos obligatorios de la cita.
     *
     * @param citaRq datos de la cita.
     */
    private void validarObjetoCita(CitaRq citaRq) {
        if (citaRq == null) {
            throw new BadRequestException("Los datos de la cita son obligatorios");
        }
        if (citaRq.getClienteId() == null || citaRq.getClienteId() <= 0) {
            throw new BadRequestException("Debe seleccionar un cliente");
        }
        if (citaRq.getMascotaId() == null || citaRq.getMascotaId() <= 0) {
            throw new BadRequestException("Debe seleccionar una mascota");
        }
        if (citaRq.getMedicoId() == null || citaRq.getMedicoId() <= 0) {
            throw new BadRequestException("Debe seleccionar un veterinario");
        }
        if (citaRq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita son obligatorias");
        }
        if (citaRq.getMotivo() == null || citaRq.getMotivo().isBlank()) {
            throw new BadRequestException("El motivo de la consulta es obligatorio");
        }
    }

    /**
     * Arma la respuesta de exito.
     *
     * @param mensaje mensaje para el usuario.
     * @return respuesta.
     */
    private MiRespuestaRS respuestaExito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
