package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    // Lista todas las citas o solo las que pertenecen al rango de fechas recibido.
    public List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        if (fechaInicial == null && fechaFinal == null) {
            return citaRepository.findAllByOrderByFechaHoraDesc();
        }
        validarRangoFechas(fechaInicial, fechaFinal);
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicial, fechaFinal);
    }

    @Override
    // Valida el identificador y devuelve la cita si existe.
    public Cita consultarCita(Long citaId) throws BadRequestException {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita no puede ser nulo");
        }
        return buscarCita(citaId);
    }

    @Override
    // Lista las citas de una mascota; se usa en los combos de historia clínica y fórmula.
    public List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota no puede ser nulo");
        }
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("Mascota no encontrada");
        }
        return citaRepository.findByMascota_MascotaIdOrderByFechaHoraDesc(mascotaId);
    }

    @Override
    // Valida la solicitud y las reglas de negocio, crea la cita y la guarda en la base de datos.
    public MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        if (citaRq.getClienteId() == null || citaRq.getMascotaId() == null) {
            throw new BadRequestException("El cliente y la mascota son obligatorios");
        }

        String estado = citaRq.getEstado().trim().toUpperCase();
        if (!Constantes.ESTADOS_INICIALES_CITA.contains(estado)) {
            throw new BadRequestException("Una cita nueva solo puede quedar Pendiente, Programada o Confirmada");
        }
        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden registrar citas en fechas anteriores a la fecha actual");
        }

        Cliente cliente = clienteRepository.findById(citaRq.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(citaRq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada"));
        if (mascota.getCliente() == null || !mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente");
        }
        Medico medico = buscarMedico(citaRq.getMedicoId());
        validarDisponibilidad(null, medico.getId(), mascota.getMascotaId(), citaRq.getFechaHora());

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        citaRepository.save(cita);
        return respuesta(HttpStatus.CREATED.value(), "Cita creada correctamente");
    }

    @Override
    // Actualiza la cita teniendo en cuenta su estado actual.
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        if (citaRq.getCitaId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para actualizar");
        }
        Cita cita = buscarCita(citaRq.getCitaId());

        // Una cita atendida ya es información histórica y una cancelada solo se recupera con "reactivar".
        if (Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita ya fue atendida, su información no se puede modificar");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita está cancelada, para modificarla primero debe reactivarla");
        }

        String estado = citaRq.getEstado().trim().toUpperCase();
        if (!Constantes.ESTADOS_CITA.contains(estado)) {
            throw new BadRequestException("El estado de la cita no es válido");
        }

        boolean cambiaFecha = !cita.getFechaHora().equals(citaRq.getFechaHora());
        if (cambiaFecha && citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede mover la cita a una fecha anterior a la fecha actual");
        }

        Medico medico = buscarMedico(citaRq.getMedicoId());
        boolean cambiaMedico = !medico.getId().equals(cita.getMedico().getId());
        if ((cambiaFecha || cambiaMedico) && !Constantes.CITA_CANCELADA.equals(estado)) {
            validarDisponibilidad(cita.getId(), medico.getId(), cita.getMascota().getMascotaId(),
                    citaRq.getFechaHora());
        }

        // El cliente y la mascota no se cambian: si son otros se debe registrar una cita nueva.
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        citaRepository.save(cita);
        return respuesta(HttpStatus.OK.value(), "Cita actualizada correctamente");
    }

    @Override
    // Reactiva una cita cancelada, es la única forma de volver a activar una cita cancelada.
    public MiRespuestaRS reactivarCita(Long citaId) throws BadRequestException {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para reactivar");
        }
        Cita cita = buscarCita(citaId);
        if (!Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("Solo se pueden reactivar citas canceladas");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la cita ya pasó, debe registrar una cita nueva");
        }
        validarDisponibilidad(cita.getId(), cita.getMedico().getId(), cita.getMascota().getMascotaId(),
                cita.getFechaHora());

        cita.setEstado(Constantes.CITA_PENDIENTE);
        citaRepository.save(cita);
        return respuesta(HttpStatus.OK.value(), "Cita reactivada correctamente, quedó en estado Pendiente");
    }

    private Cita buscarCita(Long citaId) throws BadRequestException {
        // Busca la cita o informa que no existe.
        return citaRepository.findById(citaId)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
    }

    private Medico buscarMedico(Long medicoId) throws BadRequestException {
        // Busca el veterinario o informa que no existe.
        return medicoRepository.findById(medicoId)
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));
    }

    private void validarDisponibilidad(Long citaId, Long medicoId, Integer mascotaId, LocalDateTime fechaHora)
            throws BadRequestException {
        // Se bloquean 15 minutos antes y 15 minutos después de la hora solicitada.
        LocalDateTime inicio = fechaHora.minusMinutes(Constantes.MINUTOS_MARGEN_CITA);
        LocalDateTime fin = fechaHora.plusMinutes(Constantes.MINUTOS_MARGEN_CITA);

        List<Cita> citasMedico = citaRepository.findByMedico_IdAndFechaHoraBetween(medicoId, inicio, fin);
        if (existeCitaActiva(citasMedico, citaId)) {
            throw new ConflictException(
                    "El veterinario no está disponible, ya tiene una cita 15 minutos antes o después de esa hora");
        }

        List<Cita> citasMascota = citaRepository.findByMascota_MascotaIdAndFechaHoraBetween(mascotaId, inicio, fin);
        if (existeCitaActiva(citasMascota, citaId)) {
            throw new ConflictException("La mascota ya tiene una cita en ese horario");
        }
    }

    private boolean existeCitaActiva(List<Cita> citas, Long citaId) {
        // No se tienen en cuenta las citas canceladas ni la misma cita que se está editando.
        for (Cita cita : citas) {
            boolean esLaMismaCita = cita.getId().equals(citaId);
            boolean estaCancelada = Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado());
            if (!esLaMismaCita && !estaCancelada) {
                return true;
            }
        }
        return false;
    }

    private void validarCita(CitaRq citaRq) throws BadRequestException {
        // Comprueba que los datos obligatorios de la cita estén presentes.
        if (citaRq == null || citaRq.getMedicoId() == null || citaRq.getFechaHora() == null
                || citaRq.getEstado() == null || citaRq.getEstado().isBlank()
                || citaRq.getMotivo() == null || citaRq.getMotivo().isBlank()) {
            throw new BadRequestException("Veterinario, fecha, hora, estado y motivo son obligatorios");
        }
    }

    private void validarRangoFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        // Evita consultar con fechas vacías o en un orden incorrecto.
        if (fechaInicial == null || fechaFinal == null || fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("El rango de fechas no es válido");
        }
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        // Devuelve el formato de respuesta usado por las operaciones exitosas.
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
