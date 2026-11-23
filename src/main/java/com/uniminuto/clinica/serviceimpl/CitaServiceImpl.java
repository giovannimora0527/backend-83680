package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.HorarioMedicoRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.util.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion de la logica de las citas.
 *
 * Estados: pendiente, confirmada, programada, atendida (finalizada) y cancelada.
 * - Un veterinario no puede tener dos citas que se superpongan.
 * - Una mascota no puede tener dos citas simultaneas.
 * - No se asignan citas a medicos inactivos ni fuera de su horario de disponibilidad.
 * - Una cita cancelada no se edita (se reactiva) y conserva su historial.
 * - Una cita atendida no se edita porque ya es informacion historica.
 */
@Service
public class CitaServiceImpl implements CitaService {

    /** Estados que puede tener una cita. */
    private static final List<String> ESTADOS_VALIDOS =
            List.of("pendiente", "confirmada", "programada", "atendida", "cancelada");

    /** Estados permitidos al crear una cita nueva. */
    private static final List<String> ESTADOS_CREACION = List.of("pendiente", "confirmada", "programada");

    /** Duracion por defecto de una cita. */
    private static final int MINUTOS_POR_DEFECTO = 30;

    /** Duracion maxima de una cita. */
    private static final int MAXIMO_MINUTOS = 240;

    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private MascotaRepository mascotaRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private HorarioMedicoRepository horarioRepository;
    @Autowired
    private HistoriaMedicaRepository historiaRepository;

    // ------------------------------------------------------------------ consultas

    @Override
    public List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        List<Cita> citas;
        if (fechaInicial == null && fechaFinal == null) {
            citas = citaRepository.findAllByOrderByFechaHoraDesc();
        } else {
            if (fechaInicial == null || fechaFinal == null) {
                throw new BadRequestException("Debe enviar la fecha inicial y la fecha final");
            }
            if (fechaInicial.isAfter(fechaFinal)) {
                throw new BadRequestException("La fecha inicial no puede ser mayor que la fecha final");
            }
            citas = citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(
                    fechaInicial.atStartOfDay(), fechaFinal.atTime(23, 59, 59));
        }
        return citas.stream().map(this::convertirCita).toList();
    }

    @Override
    public List<CitaRs> listarCitasPorMascota(Integer mascotaId) {
        this.buscarMascota(mascotaId);
        return citaRepository.findByMascotaMascotaIdOrderByFechaHoraDesc(mascotaId).stream().map(this::convertirCita).toList();
    }

    @Override
    public List<CitaRs> listarCitasMedico(Long medicoId, LocalDateTime desde, LocalDateTime hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new BadRequestException("Rango de fechas inválido");
        }
        return citaRepository.findByMedicoIdAndFechaHoraBetweenOrderByFechaHoraAsc(medicoId, desde, hasta)
                .stream().map(this::convertirCita).toList();
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
        return citaRepository.findById(citaId)
                .orElseThrow(() -> new BadRequestException("Cita no encontrada", HttpStatus.NOT_FOUND));
    }

    @Override
    public CitaRs convertirCita(Cita cita) {
        CitaRs rs = new CitaRs();
        rs.setId(cita.getId());
        rs.setClienteId(cita.getCliente().getClienteId());
        rs.setClienteNombre(cita.getCliente().getNombres() + " " + cita.getCliente().getApellidos());
        rs.setClienteTelefono(cita.getCliente().getTelefono());
        rs.setClienteDocumento(cita.getCliente().getTipoDocumento() + " " + cita.getCliente().getNumeroDocumento());
        rs.setMascotaId(cita.getMascota().getMascotaId());
        rs.setMascotaNombre(cita.getMascota().getNombreMascota());
        rs.setMascotaEdad(cita.getMascota().getEdad());
        if (cita.getMascota().getRaza() != null) {
            rs.setMascotaEspecie(cita.getMascota().getRaza().getEspecie());
            rs.setMascotaRaza(cita.getMascota().getRaza().getNombre());
        }
        rs.setMedicoId(cita.getMedico().getId());
        rs.setMedicoNombre(cita.getMedico().getNombres() + " " + cita.getMedico().getApellidos());
        rs.setFechaHora(cita.getFechaHora());
        rs.setFechaHoraFin(cita.getFechaHoraFin() != null ? cita.getFechaHoraFin()
                : cita.getFechaHora().plusMinutes(MINUTOS_POR_DEFECTO));
        rs.setEstado(cita.getEstado());
        rs.setMotivo(cita.getMotivo());
        rs.setAtendidaConHistoria(historiaRepository.existsByCitaId(cita.getId()));
        return rs;
    }

    // ------------------------------------------------------------------ crear y editar

    @Override
    @Transactional
    public MiRespuestaRS guardarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada y las fechas
        this.validarObjetoCita(citaRq);
        LocalDateTime inicio = citaRq.getFechaHora();
        LocalDateTime fin = this.calcularFin(inicio, citaRq.getFechaHoraFin());
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden registrar citas en fechas u horas pasadas");
        }
        String estado = citaRq.getEstado() == null || citaRq.getEstado().isBlank()
                ? "programada" : citaRq.getEstado().toLowerCase();
        if (!ESTADOS_CREACION.contains(estado)) {
            throw new BadRequestException("Una cita nueva solo puede estar pendiente, confirmada o programada");
        }

        // Paso 2. Buscar el cliente, la mascota y el medico
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());

        // Paso 3. Reglas de negocio
        this.validarMascotaDelCliente(mascota, cliente);
        this.validarDisponibilidad(medico, mascota, inicio, fin, null);

        // Paso 4. Guardar
        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(inicio);
        cita.setFechaHoraFin(fin);
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        this.citaRepository.save(cita);
        return this.respuestaExito("Cita guardada correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarCita(CitaRq citaRq) {
        // Paso 1. Validar el objeto de entrada
        this.validarObjetoCita(citaRq);
        String estado = citaRq.getEstado() == null ? null : citaRq.getEstado().toLowerCase();
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new BadRequestException("El estado debe ser pendiente, confirmada, programada, atendida o cancelada");
        }

        // Paso 2. Revisar si el estado actual permite editar
        Cita cita = this.buscarCita(citaRq.getCitaId());
        this.validarEditable(cita);
        LocalDateTime inicio = citaRq.getFechaHora();
        LocalDateTime fin = this.calcularFin(inicio, citaRq.getFechaHoraFin());
        boolean cambioFecha = !cita.getFechaHora().equals(inicio);
        if (cambioFecha && inicio.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede mover la cita a una fecha u hora pasada");
        }

        // Paso 3. Buscar el cliente, la mascota y el medico
        Cliente cliente = this.buscarCliente(citaRq.getClienteId());
        Mascota mascota = this.buscarMascota(citaRq.getMascotaId());
        Medico medico = this.buscarMedico(citaRq.getMedicoId());
        this.validarMascotaDelCliente(mascota, cliente);
        if (!estado.equals("cancelada")) {
            this.validarDisponibilidad(medico, mascota, inicio, fin, cita.getId());
        }

        // Paso 4. Guardar
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(inicio);
        cita.setFechaHoraFin(fin);
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        this.citaRepository.save(cita);
        return this.respuestaExito("Cita actualizada correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS reprogramarCita(Long citaId, LocalDateTime inicio, LocalDateTime fin) {
        Cita cita = this.buscarCita(citaId);
        this.validarEditable(cita);
        if (inicio == null) {
            throw new BadRequestException("La nueva fecha y hora son obligatorias");
        }
        if (inicio.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede mover la cita a una fecha u hora pasada");
        }
        LocalDateTime nuevoFin = fin != null ? fin
                : inicio.plus(Duration.between(cita.getFechaHora(), cita.getFechaHoraFin() != null
                ? cita.getFechaHoraFin() : cita.getFechaHora().plusMinutes(MINUTOS_POR_DEFECTO)));
        nuevoFin = this.calcularFin(inicio, nuevoFin);
        this.validarDisponibilidad(cita.getMedico(), cita.getMascota(), inicio, nuevoFin, cita.getId());
        cita.setFechaHora(inicio);
        cita.setFechaHoraFin(nuevoFin);
        citaRepository.save(cita);
        return this.respuestaExito("Cita reprogramada para el "
                + inicio.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @Override
    @Transactional
    public MiRespuestaRS cambiarEstado(Long citaId, String estado) {
        Cita cita = this.buscarCita(citaId);
        String nuevo = estado == null ? "" : estado.toLowerCase();
        if (!ESTADOS_VALIDOS.contains(nuevo)) {
            throw new BadRequestException("Estado no válido");
        }
        this.validarEditable(cita);
        if (nuevo.equals("atendida") && cita.getFechaHora().isAfter(LocalDateTime.now().plusHours(1))) {
            throw new BadRequestException("No se puede marcar como atendida una cita que aún no ha ocurrido");
        }
        cita.setEstado(nuevo);
        citaRepository.save(cita);
        return this.respuestaExito("La cita quedó en estado " + nuevo);
    }

    @Override
    @Transactional
    public MiRespuestaRS reactivarCita(Long citaId) {
        Cita cita = this.buscarCita(citaId);
        if (!cita.getEstado().equals("cancelada")) {
            throw new BadRequestException("Solo se pueden reactivar citas canceladas");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la cita ya pasó, debe crear una cita nueva");
        }
        this.validarDisponibilidad(cita.getMedico(), cita.getMascota(), cita.getFechaHora(),
                this.calcularFin(cita.getFechaHora(), cita.getFechaHoraFin()), cita.getId());
        cita.setEstado("pendiente");
        this.citaRepository.save(cita);
        return this.respuestaExito("Cita reactivada correctamente, quedó en estado pendiente");
    }

    // ------------------------------------------------------------------ horarios y reporte

    @Override
    public List<HorarioMedico> listarHorarios(Long medicoId) {
        this.buscarMedico(medicoId);
        return horarioRepository.findByMedicoIdOrderByDiaSemanaAscHoraInicioAsc(medicoId);
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarHorarios(Long medicoId, List<HorarioMedico> horarios) {
        this.buscarMedico(medicoId);
        if (horarios == null) {
            horarios = List.of();
        }
        for (HorarioMedico h : horarios) {
            if (h.getDiaSemana() == null || h.getDiaSemana() < 1 || h.getDiaSemana() > 7) {
                throw new BadRequestException("El día de la semana debe estar entre 1 (lunes) y 7 (domingo)");
            }
            if (h.getHoraInicio() == null || h.getHoraFin() == null || !h.getHoraInicio().isBefore(h.getHoraFin())) {
                throw new BadRequestException("La hora de inicio debe ser menor que la hora de fin");
            }
        }
        horarioRepository.deleteAll(horarioRepository.findByMedicoIdOrderByDiaSemanaAscHoraInicioAsc(medicoId));
        for (HorarioMedico h : horarios) {
            HorarioMedico nuevo = new HorarioMedico();
            nuevo.setMedicoId(medicoId);
            nuevo.setDiaSemana(h.getDiaSemana());
            nuevo.setHoraInicio(h.getHoraInicio());
            nuevo.setHoraFin(h.getHoraFin());
            nuevo.setActivo(true);
            horarioRepository.save(nuevo);
        }
        return this.respuestaExito("Horarios actualizados");
    }

    @Override
    public byte[] reporteCitasExcel(Long medicoId, LocalDate desde, LocalDate hasta) {
        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new BadRequestException("Seleccione una fecha inicial y final válidas");
        }
        Medico medico = this.buscarMedico(medicoId);
        List<CitaRs> citas = this.listarCitasMedico(medicoId, desde.atStartOfDay(), hasta.atTime(23, 59, 59));
        DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<List<Object>> filas = new ArrayList<>();
        Map<String, Integer> porEstado = new LinkedHashMap<>();
        ESTADOS_VALIDOS.forEach(e -> porEstado.put(e, 0));
        for (CitaRs c : citas) {
            filas.add(List.of(c.getId(), c.getFechaHora().format(f), c.getFechaHoraFin().format(f), c.getClienteNombre(),
                    c.getMascotaNombre(), c.getMascotaEspecie() == null ? "" : c.getMascotaEspecie(),
                    c.getMotivo() == null ? "" : c.getMotivo(), c.getEstado()));
            porEstado.merge(c.getEstado(), 1, Integer::sum);
        }
        String resumen = "Pendientes: " + porEstado.get("pendiente") + "  Confirmadas: " + porEstado.get("confirmada")
                + "  Programadas: " + porEstado.get("programada") + "  Atendidas/finalizadas: " + porEstado.get("atendida")
                + "  Canceladas: " + porEstado.get("cancelada");
        return ExcelUtil.tabla("Citas del Dr(a). " + medico.getNombres() + " " + medico.getApellidos(),
                "Desde " + desde + " hasta " + hasta + "   |   " + resumen,
                List.of("Id", "Inicio", "Fin", "Propietario", "Mascota", "Especie", "Motivo", "Estado"),
                filas, List.of("Total citas", citas.size()));
    }

    // ------------------------------------------------------------------ validaciones

    /**
     * Reglas de agenda: medico activo, dentro de su horario y sin cruces con otras citas.
     *
     * @param medico  medico.
     * @param mascota mascota.
     * @param inicio  inicio.
     * @param fin     fin.
     * @param citaId  cita que se edita (null si es nueva).
     */
    private void validarDisponibilidad(Medico medico, Mascota mascota, LocalDateTime inicio, LocalDateTime fin, Long citaId) {
        if (!Boolean.TRUE.equals(medico.getActivo())) {
            throw new BadRequestException("No se pueden asignar citas a un médico inactivo");
        }
        if (!inicio.toLocalDate().equals(fin.toLocalDate())) {
            throw new BadRequestException("La cita debe iniciar y terminar el mismo día");
        }
        List<HorarioMedico> horariosDelDia = horarioRepository
                .findByMedicoIdAndDiaSemanaAndActivoTrue(medico.getId(), inicio.getDayOfWeek().getValue());
        boolean dentroDelHorario = horariosDelDia.stream().anyMatch(h ->
                !inicio.toLocalTime().isBefore(h.getHoraInicio()) && !fin.toLocalTime().isAfter(h.getHoraFin()));
        if (!dentroDelHorario) {
            throw new BadRequestException("El veterinario no está disponible en ese horario (revise sus horarios de atención)");
        }
        boolean cruceMedico = citaRepository.findCrucesMedico(medico.getId(), inicio, fin).stream()
                .anyMatch(c -> !c.getId().equals(citaId));
        if (cruceMedico) {
            throw new BadRequestException("El veterinario ya tiene una cita que se superpone con ese horario");
        }
        boolean cruceMascota = citaRepository.findCrucesMascota(mascota.getMascotaId(), inicio, fin).stream()
                .anyMatch(c -> !c.getId().equals(citaId));
        if (cruceMascota) {
            throw new BadRequestException("La mascota ya tiene otra cita en ese mismo horario");
        }
    }

    /**
     * Una cita cancelada o atendida no se puede editar.
     *
     * @param cita cita.
     */
    private void validarEditable(Cita cita) {
        if (cita.getEstado().equals("cancelada")) {
            throw new BadRequestException("La cita está cancelada. Si desea usarla de nuevo primero debe reactivarla");
        }
        if (cita.getEstado().equals("atendida")) {
            throw new BadRequestException("La cita ya fue atendida y su información no se puede modificar");
        }
    }

    /**
     * Calcula y valida la hora de fin.
     *
     * @param inicio inicio.
     * @param fin    fin enviado (puede ser null).
     * @return fin valido.
     */
    private LocalDateTime calcularFin(LocalDateTime inicio, LocalDateTime fin) {
        LocalDateTime resultado = fin != null ? fin : inicio.plusMinutes(MINUTOS_POR_DEFECTO);
        if (!resultado.isAfter(inicio)) {
            throw new BadRequestException("La hora de fin debe ser posterior a la hora de inicio");
        }
        if (Duration.between(inicio, resultado).toMinutes() > MAXIMO_MINUTOS) {
            throw new BadRequestException("Una cita no puede durar más de 4 horas");
        }
        return resultado;
    }

    private Cliente buscarCliente(Long clienteId) {
        return clienteRepository.findById(clienteId)
                .orElseThrow(() -> new BadRequestException("Cliente no encontrado"));
    }

    private Mascota buscarMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota no puede ser nulo");
        }
        return mascotaRepository.findById(mascotaId)
                .orElseThrow(() -> new BadRequestException("Mascota no encontrada"));
    }

    private Medico buscarMedico(Long medicoId) {
        if (medicoId == null) {
            throw new BadRequestException("Debe seleccionar un veterinario");
        }
        return medicoRepository.findById(medicoId)
                .orElseThrow(() -> new BadRequestException("Médico no encontrado"));
    }

    private void validarMascotaDelCliente(Mascota mascota, Cliente cliente) {
        if (!mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota no pertenece al cliente seleccionado");
        }
    }

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

    private MiRespuestaRS respuestaExito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
