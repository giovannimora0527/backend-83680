package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.HistoriaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.FormulaMedicaService;
import com.uniminuto.clinica.service.HistoriaMedicaService;
import com.uniminuto.clinica.utils.Constantes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
/** Implementa las operaciones de atención médica (historias clínicas) y su integración con fórmulas médicas. */
public class HistoriaMedicaServiceImpl implements HistoriaMedicaService {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    /** Repositorio de historias médicas. */
    private HistoriaMedicaRepository historiaMedicaRepository;

    @Autowired
    /** Repositorio usado para comprobar las anotaciones relacionadas. */
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    @Autowired
    /** Repositorio de clientes. */
    private ClienteRepository clienteRepository;

    @Autowired
    /** Repositorio de mascotas. */
    private MascotaRepository mascotaRepository;

    @Autowired
    /** Repositorio de citas. */
    private CitaRepository citaRepository;

    @Autowired
    /** Repositorio de médicos. */
    private MedicoRepository medicoRepository;

    @Autowired
    /** Servicio de fórmulas médicas para prescribir durante la atención. */
    private FormulaMedicaService formulaMedicaService;

    @Override
    // Devuelve las historias médicas ordenadas de la consulta más reciente a la más antigua.
    public List<HistoriaMedica> listarHistoriasMedicas() throws BadRequestException {
        return historiaMedicaRepository.findAllByOrderByFechaHoraDesc();
    }

    @Override
    // Valida el identificador y devuelve la historia si existe.
    public HistoriaMedica consultarHistoriaMedica(Long historiaMedicaId) throws BadRequestException {
        if (historiaMedicaId == null) {
            throw new BadRequestException("El ID de la historia médica no puede ser nulo");
        }
        return buscarHistoria(historiaMedicaId);
    }

    @Override
    @Transactional
    // Registra la atención médica de la cita, actualiza el estado de la cita a ATENDIDA y crea las fórmulas prescritas.
    public MiRespuestaRS crearHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        validarHistoriaMedica(historiaMedicaRq);

        Cliente cliente = clienteRepository.findById(historiaMedicaRq.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(historiaMedicaRq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada"));
        if (mascota.getCliente() == null || !mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente");
        }

        Cita cita = citaRepository.findById(historiaMedicaRq.getCitaId())
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
        if (!cita.getMascota().getMascotaId().equals(mascota.getMascotaId())) {
            throw new BadRequestException("La cita seleccionada pertenece a otra mascota");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new BadRequestException("No se puede registrar atención médica para una cita cancelada");
        }
        if (historiaMedicaRepository.existsByCita_Id(cita.getId())) {
            throw new ConflictException("La cita ya tiene una atención / historia clínica registrada");
        }
        if (historiaMedicaRq.getFechaHora().isBefore(cita.getFechaHora())) {
            throw new BadRequestException("La fecha de la atención médica no puede ser anterior a la fecha de la cita");
        }

        Medico medico = medicoRepository.findById(historiaMedicaRq.getMedicoId())
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));

        // Si la cita estaba Pendiente, Programada o Confirmada, pasa automáticamente a ATENDIDA al registrar la atención.
        if (!Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())) {
            String estadoPrevio = cita.getEstado();
            cita.setEstado(Constantes.CITA_ATENDIDA);
            cita.setFechaActualizacion(LocalDateTime.now());
            String linea = "[" + LocalDateTime.now().format(FORMATO_FECHA_HORA)
                    + "] Atención médica registrada por Dr(a). " + medico.getNombres() + " " + medico.getApellidos()
                    + " (Estado: " + estadoPrevio + " -> ATENDIDA)";
            cita.setHistorialCambios(cita.getHistorialCambios() == null || cita.getHistorialCambios().isBlank()
                    ? linea
                    : cita.getHistorialCambios() + "\n" + linea);
            citaRepository.save(cita);
        }

        HistoriaMedica historiaMedica = new HistoriaMedica();
        historiaMedica.setCliente(cliente);
        historiaMedica.setMascota(mascota);
        historiaMedica.setCita(cita);
        historiaMedica.setMedico(medico);
        historiaMedica.setFechaHora(historiaMedicaRq.getFechaHora());
        historiaMedica.setMotivo(historiaMedicaRq.getMotivo().trim());
        historiaMedica.setDiagnostico(historiaMedicaRq.getDiagnostico().trim());
        historiaMedica.setObservaciones(limpiarTexto(historiaMedicaRq.getObservaciones()));
        historiaMedica.setEstado(Constantes.HISTORIA_ABIERTA);
        historiaMedica.setFechaCreacion(LocalDateTime.now());
        historiaMedicaRepository.save(historiaMedica);

        int formulasCreadas = 0;
        if (historiaMedicaRq.getFormulas() != null && !historiaMedicaRq.getFormulas().isEmpty()) {
            for (FormulaMedicaRq formulaRq : historiaMedicaRq.getFormulas()) {
                if (formulaRq == null || formulaRq.getMedicamentoId() == null) {
                    continue;
                }
                formulaRq.setCitaId(cita.getId());
                formulaRq.setMascotaId(mascota.getMascotaId());
                if (formulaRq.getMedicoId() == null) {
                    formulaRq.setMedicoId(medico.getId());
                }
                if (formulaRq.getFechaEmision() == null) {
                    formulaRq.setFechaEmision(historiaMedicaRq.getFechaHora().toLocalDate());
                }
                formulaMedicaService.crearFormulaMedica(formulaRq);
                formulasCreadas++;
            }
        }

        String mensaje = formulasCreadas > 0
                ? "Atención médica e historia clínica registradas con " + formulasCreadas + " fórmula(s) médica(s)"
                : "Atención médica e historia clínica creadas correctamente";
        return respuesta(HttpStatus.CREATED.value(), mensaje);
    }

    @Override
    // Actualiza solo el diagnóstico, las observaciones y el estado de una historia abierta.
    public MiRespuestaRS actualizarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        if (historiaMedicaRq == null || historiaMedicaRq.getHistoriaMedicaId() == null) {
            throw new BadRequestException("El ID de la historia médica es obligatorio para actualizar");
        }
        if (historiaMedicaRq.getDiagnostico() == null || historiaMedicaRq.getDiagnostico().isBlank()
                || historiaMedicaRq.getEstado() == null || historiaMedicaRq.getEstado().isBlank()) {
            throw new BadRequestException("El diagnóstico y el estado son obligatorios");
        }
        String estado = historiaMedicaRq.getEstado().trim().toUpperCase();
        if (!Constantes.ESTADOS_HISTORIA.contains(estado)) {
            throw new BadRequestException("El estado de la historia médica no es válido");
        }

        HistoriaMedica historiaMedica = buscarHistoria(historiaMedicaRq.getHistoriaMedicaId());
        // Una historia cerrada es información clínica definitiva.
        if (Constantes.HISTORIA_CERRADA.equalsIgnoreCase(historiaMedica.getEstado())) {
            throw new ConflictException("La historia médica está cerrada y no se puede modificar");
        }

        historiaMedica.setDiagnostico(historiaMedicaRq.getDiagnostico().trim());
        historiaMedica.setObservaciones(limpiarTexto(historiaMedicaRq.getObservaciones()));
        historiaMedica.setEstado(estado);
        historiaMedicaRepository.save(historiaMedica);

        return respuesta(HttpStatus.OK.value(), "Historia médica actualizada correctamente");
    }

    @Override
    // Busca y elimina la historia médica indicada.
    public MiRespuestaRS eliminarHistoriaMedica(Long historiaMedicaId) throws BadRequestException {
        if (historiaMedicaId == null) {
            throw new BadRequestException("El ID de la historia médica es obligatorio para eliminar");
        }

        HistoriaMedica historiaMedica = buscarHistoria(historiaMedicaId);

        // No se puede eliminar una historia que todavía tiene anotaciones.
        if (anotacionHistoriaRepository.existsByHistoriaMedica_Id(historiaMedicaId)) {
            throw new ConflictException(
                    "No se puede eliminar la historia médica porque tiene anotaciones asignadas");
        }

        historiaMedicaRepository.delete(historiaMedica);

        return respuesta(HttpStatus.OK.value(), "Historia médica eliminada correctamente");
    }

    private HistoriaMedica buscarHistoria(Long historiaMedicaId) throws BadRequestException {
        return historiaMedicaRepository.findById(historiaMedicaId)
                .orElseThrow(() -> new NotFoundException("Historia médica no encontrada"));
    }

    private void validarHistoriaMedica(HistoriaMedicaRq historiaMedicaRq) throws BadRequestException {
        if (historiaMedicaRq == null || historiaMedicaRq.getClienteId() == null
                || historiaMedicaRq.getMascotaId() == null || historiaMedicaRq.getCitaId() == null
                || historiaMedicaRq.getMedicoId() == null || historiaMedicaRq.getFechaHora() == null
                || historiaMedicaRq.getMotivo() == null || historiaMedicaRq.getMotivo().isBlank()
                || historiaMedicaRq.getDiagnostico() == null || historiaMedicaRq.getDiagnostico().isBlank()) {
            throw new BadRequestException(
                    "Cliente, mascota, cita, veterinario, fecha, motivo y diagnóstico son obligatorios");
        }
    }

    private String limpiarTexto(String texto) {
        return texto == null ? null : texto.trim();
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
