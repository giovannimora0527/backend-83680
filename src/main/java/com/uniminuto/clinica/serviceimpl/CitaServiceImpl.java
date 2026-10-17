package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.BusinessRuleException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Implementacion del servicio de citas. Centraliza las reglas de negocio:
 * existencia de cliente, mascota y veterinario, pertenencia mascota-cliente,
 * fechas futuras, choques de horario (+-15 minutos) y restricciones por estado.
 */
@Service
public class CitaServiceImpl implements CitaService {

    private static final String ESTADO_PROGRAMADA = "PROGRAMADA";
    private static final String ESTADO_ATENDIDA = "ATENDIDA";
    private static final String ESTADO_CANCELADA = "CANCELADA";
    private static final Set<String> ESTADOS = Set.of(
            "PENDIENTE", "CONFIRMADA", ESTADO_ATENDIDA, ESTADO_CANCELADA, ESTADO_PROGRAMADA);
    private static final int MARGEN_MINUTOS = 15;

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    /** {@inheritDoc} */
    @Override
    public Page<Cita> listarCitas(String estado, Long medicoId, LocalDateTime desde,
                                  LocalDateTime hasta, Pageable pageable) throws BadRequestException {
        if (desde != null && hasta != null) {
            this.validarFechas(desde, hasta);
        }
        String estadoFiltro = null;
        if (estado != null && !estado.isBlank()) {
            estadoFiltro = this.normalizarEstado(estado, null);
        }
        return citaRepository.buscar(estadoFiltro, medicoId, desde, hasta, pageable);
    }

    /** {@inheritDoc} */
    @Override
    public List<Cita> listarCitasRango(LocalDateTime fechaInicio, LocalDateTime fechaFin)
            throws BadRequestException {
        this.validarFechas(fechaInicio, fechaFin);
        List<Cita> citas = citaRepository.findByFechaHoraBetween(fechaInicio, fechaFin);
        citas.sort((c1, c2) -> c1.getFechaHora().compareTo(c2.getFechaHora()));
        return citas;
    }

    /** {@inheritDoc} */
    @Override
    public Cita consultarCita(Long id) {
        return citaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La cita no existe"));
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException {
        this.validarCamposObligatorios(citaRq);
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());
        String estado = this.normalizarEstado(citaRq.getEstado(), ESTADO_PROGRAMADA);

        this.validarReglas(citaRq, cliente, mascota, medico, null, true);

        Cita nueva = new Cita();
        nueva.setCliente(cliente);
        nueva.setMascota(mascota);
        nueva.setMedico(medico);
        nueva.setFechaHora(citaRq.getFechaHora());
        nueva.setMotivo(citaRq.getMotivo());
        nueva.setEstado(estado);
        citaRepository.save(nueva);

        return this.respuesta("Se agendo una nueva cita para la mascota ", mascota, medico, citaRq);
    }

    /** {@inheritDoc} */
    @Override
    @Transactional
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        if (citaRq == null || citaRq.getId() == null) {
            throw new BadRequestException("El id de la cita es obligatorio");
        }
        this.validarCamposObligatorios(citaRq);
        Cita cita = this.consultarCita(citaRq.getId());

        // Una cancelada no se reactiva y una atendida es informacion historica.
        if (ESTADO_CANCELADA.equals(cita.getEstado()) || ESTADO_ATENDIDA.equals(cita.getEstado())) {
            throw new BusinessRuleException("Una cita " + cita.getEstado().toLowerCase()
                    + " no puede modificarse");
        }

        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());
        String estado = this.normalizarEstado(citaRq.getEstado(), cita.getEstado());

        boolean cambiaFecha = !cita.getFechaHora().equals(citaRq.getFechaHora());
        this.validarReglas(citaRq, cliente, mascota, medico, cita.getId(), cambiaFecha);

        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setMotivo(citaRq.getMotivo());
        cita.setEstado(estado);
        citaRepository.save(cita);

        return this.respuesta("Se actualizo la cita de la mascota ", mascota, medico, citaRq);
    }

    /**
     * Valida las reglas de negocio de una cita.
     *
     * @param rq                datos recibidos.
     * @param cliente           cliente de la cita.
     * @param mascota           mascota de la cita.
     * @param medico            veterinario de la cita.
     * @param excluirId         id de la cita a ignorar al buscar choques (al editar) o null.
     * @param validarFechaFutura si es true, exige que la fecha no sea anterior a la actual.
     */
    private void validarReglas(CitaRq rq, Cliente cliente, Mascota mascota, Medico medico,
                               Long excluirId, boolean validarFechaFutura) {
        if (mascota.getCliente() == null || !mascota.getCliente().getId().equals(cliente.getId())) {
            throw new BusinessRuleException("La mascota no pertenece al cliente seleccionado");
        }
        if (validarFechaFutura && rq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("No se pueden registrar citas en fechas anteriores a la actual");
        }
        LocalDateTime desde = rq.getFechaHora().minusMinutes(MARGEN_MINUTOS);
        LocalDateTime hasta = rq.getFechaHora().plusMinutes(MARGEN_MINUTOS);
        if (citaRepository.existeCitaMedicoEnRango(medico, desde, hasta, excluirId)) {
            throw new ConflictException("El veterinario no esta disponible en ese horario");
        }
        if (citaRepository.existeCitaMascotaEnRango(mascota, desde, hasta, excluirId)) {
            throw new ConflictException("La mascota ya tiene una cita en ese horario");
        }
    }

    /**
     * Verifica que los campos obligatorios vengan diligenciados.
     *
     * @param rq datos recibidos.
     * @throws BadRequestException si falta algun campo.
     */
    private void validarCamposObligatorios(CitaRq rq) throws BadRequestException {
        if (rq == null) {
            throw new BadRequestException("La cita no puede ser nula");
        }
        if (rq.getClienteId() == null) {
            throw new BadRequestException("El cliente es obligatorio");
        }
        if (rq.getMascotaId() == null) {
            throw new BadRequestException("La mascota es obligatoria");
        }
        if (rq.getMedicoId() == null) {
            throw new BadRequestException("El veterinario es obligatorio");
        }
        if (rq.getFechaHora() == null) {
            throw new BadRequestException("La fecha y hora de la cita son obligatorias");
        }
        if (rq.getMotivo() == null || rq.getMotivo().isBlank()) {
            throw new BadRequestException("El motivo de la cita es obligatorio");
        }
    }

    /**
     * Valida que el rango de fechas sea coherente.
     *
     * @param inicio fecha inicial.
     * @param fin    fecha final.
     * @throws BadRequestException si alguna es nula o el inicio es posterior al fin.
     */
    private void validarFechas(LocalDateTime inicio, LocalDateTime fin) throws BadRequestException {
        if (inicio == null || fin == null) {
            throw new BadRequestException("Las fechas no pueden ser nulas");
        }
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha de inicio no puede ser mayor a la fecha de fin");
        }
    }

    /**
     * Normaliza el estado a mayusculas y valida que sea permitido.
     *
     * @param estado      estado recibido.
     * @param porDefecto  valor a usar si no viene estado.
     * @return estado normalizado.
     * @throws BadRequestException si el estado no es valido.
     */
    private String normalizarEstado(String estado, String porDefecto) throws BadRequestException {
        if (estado == null || estado.isBlank()) {
            return porDefecto;
        }
        String normalizado = estado.trim().toUpperCase();
        if (!ESTADOS.contains(normalizado)) {
            throw new BadRequestException("Estado de cita no valido. Use: " + String.join(", ", ESTADOS));
        }
        return normalizado;
    }

    /**
     * Busca un cliente o lanza 404.
     *
     * @param id id del cliente.
     * @return cliente encontrado.
     */
    private Cliente buscarCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El cliente no existe"));
    }

    /**
     * Busca una mascota o lanza 404.
     *
     * @param id id de la mascota.
     * @return mascota encontrada.
     */
    private Mascota buscarMascota(Integer id) {
        return mascotaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La mascota no existe"));
    }

    /**
     * Busca un veterinario o lanza 404.
     *
     * @param id id del veterinario.
     * @return veterinario encontrado.
     */
    private Medico buscarMedico(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("El veterinario no existe"));
    }

    /**
     * Construye la respuesta de confirmacion.
     *
     * @param prefijo texto inicial del mensaje.
     * @param mascota mascota de la cita.
     * @param medico  veterinario de la cita.
     * @param rq      datos de la cita.
     * @return respuesta con el mensaje.
     */
    private MiRespuestaRS respuesta(String prefijo, Mascota mascota, Medico medico, CitaRq rq) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setMessage(prefijo + mascota.getNombreMascota() + " con el veterinario "
                + medico.getNombres() + " " + medico.getApellidos()
                + " para el dia " + rq.getFechaHora());
        return rta;
    }
}