package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.BloqueoHorario;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.exception.NotFoundException;
import com.uniminuto.clinica.models.BloqueoHorarioRq;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.BloqueoHorarioRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.CitaService;
import com.uniminuto.clinica.utils.Constantes;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
/** Implementa las operaciones de negocio de las citas, calendario médico, bloqueos y exportación a Excel. */
public class CitaServiceImpl implements CitaService {

    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("HH:mm");

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
    @Autowired
    /** Repositorio de bloqueos de horario. */
    private BloqueoHorarioRepository bloqueoHorarioRepository;

    public CitaServiceImpl() {
    }

    /** Recibe los repositorios necesarios para operar. */
    public CitaServiceImpl(CitaRepository citaRepository, ClienteRepository clienteRepository,
                           MascotaRepository mascotaRepository, MedicoRepository medicoRepository,
                           BloqueoHorarioRepository bloqueoHorarioRepository) {
        this.citaRepository = citaRepository;
        this.clienteRepository = clienteRepository;
        this.mascotaRepository = mascotaRepository;
        this.medicoRepository = medicoRepository;
        this.bloqueoHorarioRepository = bloqueoHorarioRepository;
    }

    @Override
    public List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal)
            throws BadRequestException {
        return listarCitasPorFecha(fechaInicial, fechaFinal, null, null);
    }

    @Override
    // Lista las citas aplicando filtros opcionales de rango de fechas, veterinario y estado.
    public List<Cita> listarCitasPorFecha(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                                          Long medicoId, String estado) throws BadRequestException {
        List<Cita> citas;
        boolean tieneFechas = fechaInicial != null || fechaFinal != null;

        if (tieneFechas) {
            validarRangoFechas(fechaInicial, fechaFinal);
            if (medicoId != null) {
                buscarMedico(medicoId);
                citas = citaRepository.findByMedico_IdAndFechaHoraBetweenOrderByFechaHoraDesc(
                        medicoId, fechaInicial, fechaFinal);
            } else {
                citas = citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(fechaInicial, fechaFinal);
            }
        } else {
            if (medicoId != null) {
                buscarMedico(medicoId);
                citas = citaRepository.findByMedico_IdOrderByFechaHoraDesc(medicoId);
            } else {
                citas = citaRepository.findAllByOrderByFechaHoraDesc();
            }
        }

        if (estado != null && !estado.isBlank()) {
            String estadoFiltro = normalizarEstado(estado);
            citas = citas.stream()
                    .filter(c -> estadoFiltro.equalsIgnoreCase(normalizarEstado(c.getEstado())))
                    .collect(Collectors.toList());
        }

        citas.forEach(this::asegurarFechaHoraFin);
        return citas;
    }

    @Override
    // Valida el identificador y devuelve la cita si existe.
    public Cita consultarCita(Long citaId) throws BadRequestException {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita no puede ser nulo");
        }
        Cita cita = buscarCita(citaId);
        asegurarFechaHoraFin(cita);
        return cita;
    }

    @Override
    // Lista las citas de una mascota; se usa en el historial del paciente y combos de historia/fórmula.
    public List<Cita> listarCitasPorMascota(Integer mascotaId) throws BadRequestException {
        if (mascotaId == null) {
            throw new BadRequestException("El ID de la mascota no puede ser nulo");
        }
        if (!mascotaRepository.existsById(mascotaId)) {
            throw new NotFoundException("Mascota no encontrada");
        }
        List<Cita> citas = citaRepository.findByMascota_MascotaIdOrderByFechaHoraDesc(mascotaId);
        citas.forEach(this::asegurarFechaHoraFin);
        return citas;
    }

    @Override
    // Valida la solicitud y las reglas de negocio, crea la cita y la guarda en la base de datos.
    public MiRespuestaRS crearCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        if (citaRq.getClienteId() == null || citaRq.getMascotaId() == null) {
            throw new BadRequestException("El cliente y la mascota son obligatorios");
        }

        String estado = normalizarEstado(citaRq.getEstado());
        if (!Constantes.ESTADOS_INICIALES_CITA.contains(estado)) {
            throw new BadRequestException("Una cita nueva solo puede quedar Pendiente, Programada o Confirmada");
        }
        if (citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se pueden registrar citas en fechas anteriores a la fecha actual");
        }

        LocalDateTime fechaHoraFin = calcularYValidarFechaHoraFin(citaRq.getFechaHora(), citaRq.getFechaHoraFin());

        Cliente cliente = clienteRepository.findById(citaRq.getClienteId())
                .orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
        Mascota mascota = mascotaRepository.findById(citaRq.getMascotaId())
                .orElseThrow(() -> new NotFoundException("Mascota no encontrada"));
        if (mascota.getCliente() == null || !mascota.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BadRequestException("La mascota seleccionada no pertenece al cliente");
        }
        Medico medico = buscarMedico(citaRq.getMedicoId());
        validarDisponibilidad(null, medico.getId(), mascota.getMascotaId(), citaRq.getFechaHora(), fechaHoraFin);

        Cita cita = new Cita();
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setFechaHoraFin(fechaHoraFin);
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        cita.setFechaActualizacion(LocalDateTime.now());
        cita.setHistorialCambios(construirLineaHistorial(null, citaRq.getUsuarioCambio(),
                "Cita registrada en estado " + estado + " para " + formatearRango(citaRq.getFechaHora(), fechaHoraFin)
                        + " con Dr(a). " + nombreMedico(medico)));
        citaRepository.save(cita);
        return respuesta(HttpStatus.CREATED.value(), "Cita creada correctamente");
    }

    @Override
    // Actualiza la cita teniendo en cuenta su estado actual, disponibilidad y bloqueos.
    public MiRespuestaRS actualizarCita(CitaRq citaRq) throws BadRequestException {
        validarCita(citaRq);
        if (citaRq.getCitaId() == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para actualizar");
        }
        Cita cita = buscarCita(citaRq.getCitaId());
        asegurarFechaHoraFin(cita);

        // Una cita atendida ya es información histórica y una cancelada solo se recupera con "reactivar".
        if (Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())
                || Constantes.CITA_FINALIZADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita ya fue atendida, su información no se puede modificar");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita está cancelada, para modificarla primero debe reactivarla");
        }

        String estado = normalizarEstado(citaRq.getEstado());
        if (!Constantes.ESTADOS_CITA.contains(estado)) {
            throw new BadRequestException("El estado de la cita no es válido");
        }

        LocalDateTime nuevaFechaHoraFin = calcularYValidarFechaHoraFin(citaRq.getFechaHora(), citaRq.getFechaHoraFin());

        boolean cambiaFechaInicio = !cita.getFechaHora().equals(citaRq.getFechaHora());
        boolean cambiaFechaFin = !nuevaFechaHoraFin.equals(cita.getFechaHoraFin());
        if (cambiaFechaInicio && citaRq.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("No se puede mover la cita a una fecha anterior a la fecha actual");
        }

        Medico medico = buscarMedico(citaRq.getMedicoId());
        boolean cambiaMedico = !medico.getId().equals(cita.getMedico().getId());
        if ((cambiaFechaInicio || cambiaFechaFin || cambiaMedico) && !Constantes.CITA_CANCELADA.equals(estado)) {
            validarDisponibilidad(cita.getId(), medico.getId(), cita.getMascota().getMascotaId(),
                    citaRq.getFechaHora(), nuevaFechaHoraFin);
        }

        StringBuilder detalleCambio = new StringBuilder("Actualización");
        if (cambiaFechaInicio || cambiaFechaFin) {
            detalleCambio.append(" | Horario: ")
                    .append(formatearRango(cita.getFechaHora(), cita.getFechaHoraFin()))
                    .append(" -> ")
                    .append(formatearRango(citaRq.getFechaHora(), nuevaFechaHoraFin));
        }
        if (cambiaMedico) {
            detalleCambio.append(" | Veterinario: ")
                    .append(nombreMedico(cita.getMedico()))
                    .append(" -> ")
                    .append(nombreMedico(medico));
        }
        if (!cita.getEstado().equalsIgnoreCase(estado)) {
            detalleCambio.append(" | Estado: ").append(cita.getEstado()).append(" -> ").append(estado);
        }

        // El cliente y la mascota no se cambian: si son otros se debe registrar una cita nueva.
        cita.setMedico(medico);
        cita.setFechaHora(citaRq.getFechaHora());
        cita.setFechaHoraFin(nuevaFechaHoraFin);
        cita.setEstado(estado);
        cita.setMotivo(citaRq.getMotivo().trim());
        cita.setFechaActualizacion(LocalDateTime.now());
        cita.setHistorialCambios(construirLineaHistorial(cita.getHistorialCambios(),
                citaRq.getUsuarioCambio(), detalleCambio.toString()));
        citaRepository.save(cita);
        return respuesta(HttpStatus.OK.value(), "Cita actualizada correctamente");
    }

    @Override
    // Marca directamente una cita como atendida registrando el seguimiento del cambio.
    public MiRespuestaRS marcarCitaAtendida(Long citaId, String usuarioCambio) throws BadRequestException {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita es obligatorio");
        }
        Cita cita = buscarCita(citaId);
        asegurarFechaHoraFin(cita);

        if (Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("La cita ya se encuentra marcada como atendida");
        }
        if (Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("No se puede marcar como atendida una cita cancelada");
        }

        String estadoAnterior = cita.getEstado();
        cita.setEstado(Constantes.CITA_ATENDIDA);
        cita.setFechaActualizacion(LocalDateTime.now());
        cita.setHistorialCambios(construirLineaHistorial(cita.getHistorialCambios(), usuarioCambio,
                "Cita marcada como ATENDIDA (estado anterior: " + estadoAnterior + ")"));
        citaRepository.save(cita);
        return respuesta(HttpStatus.OK.value(), "Cita marcada como atendida correctamente");
    }

    @Override
    // Reactiva una cita cancelada, es la única forma de volver a activar una cita cancelada.
    public MiRespuestaRS reactivarCita(Long citaId) throws BadRequestException {
        if (citaId == null) {
            throw new BadRequestException("El ID de la cita es obligatorio para reactivar");
        }
        Cita cita = buscarCita(citaId);
        asegurarFechaHoraFin(cita);

        if (!Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())) {
            throw new ConflictException("Solo se pueden reactivar citas canceladas");
        }
        if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la cita ya pasó, debe registrar una cita nueva");
        }
        validarDisponibilidad(cita.getId(), cita.getMedico().getId(), cita.getMascota().getMascotaId(),
                cita.getFechaHora(), cita.getFechaHoraFin());

        cita.setEstado(Constantes.CITA_PENDIENTE);
        cita.setFechaActualizacion(LocalDateTime.now());
        cita.setHistorialCambios(construirLineaHistorial(cita.getHistorialCambios(), null,
                "Cita reactivada desde estado CANCELADA -> PENDIENTE"));
        citaRepository.save(cita);
        return respuesta(HttpStatus.OK.value(), "Cita reactivada correctamente, quedó en estado Pendiente");
    }

    @Override
    // Lista los bloqueos de horario activos filtrando opcionalmente por veterinario y rango de fechas.
    public List<BloqueoHorario> listarBloqueosHorario(Long medicoId, LocalDateTime fechaInicial,
                                                      LocalDateTime fechaFinal) throws BadRequestException {
        if (fechaInicial != null || fechaFinal != null) {
            validarRangoFechas(fechaInicial, fechaFinal);
        }

        List<BloqueoHorario> bloqueos;
        if (medicoId != null) {
            buscarMedico(medicoId);
            bloqueos = bloqueoHorarioRepository.findByMedico_IdAndActivoTrueOrderByFechaHoraInicioAsc(medicoId);
        } else {
            bloqueos = bloqueoHorarioRepository.findByActivoTrueOrderByFechaHoraInicioAsc();
        }

        if (fechaInicial != null && fechaFinal != null) {
            bloqueos = bloqueos.stream()
                    .filter(b -> b.getFechaHoraInicio().isBefore(fechaFinal.plusSeconds(1))
                            && b.getFechaHoraFin().isAfter(fechaInicial.minusSeconds(1)))
                    .collect(Collectors.toList());
        }
        return bloqueos;
    }

    @Override
    // Crea un nuevo bloqueo de horario para un veterinario validando que no choque con citas activas.
    public MiRespuestaRS crearBloqueoHorario(BloqueoHorarioRq bloqueoRq) throws BadRequestException {
        if (bloqueoRq == null || bloqueoRq.getMedicoId() == null
                || bloqueoRq.getFechaHoraInicio() == null || bloqueoRq.getFechaHoraFin() == null
                || bloqueoRq.getMotivo() == null || bloqueoRq.getMotivo().isBlank()) {
            throw new BadRequestException("Veterinario, fecha/hora de inicio, fecha/hora de fin y motivo son obligatorios");
        }
        if (!bloqueoRq.getFechaHoraFin().isAfter(bloqueoRq.getFechaHoraInicio())) {
            throw new BadRequestException("La fecha y hora de fin del bloqueo debe ser posterior a la de inicio");
        }

        Medico medico = buscarMedico(bloqueoRq.getMedicoId());

        // Verificar que no exista otro bloqueo activo solapado para el mismo médico.
        if (bloqueoHorarioRepository != null) {
            List<BloqueoHorario> bloqueosSolapados = bloqueoHorarioRepository
                    .findByMedico_IdAndActivoTrueAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                            medico.getId(), bloqueoRq.getFechaHoraFin(), bloqueoRq.getFechaHoraInicio());
            if (!bloqueosSolapados.isEmpty()) {
                throw new ConflictException("El veterinario ya tiene un bloqueo activo en ese rango de horario");
            }
        }

        // Verificar que el veterinario no tenga citas activas (pendientes, programadas o confirmadas) en ese horario.
        List<Cita> citasVentana = citaRepository.findByMedico_IdAndFechaHoraBetween(
                medico.getId(),
                bloqueoRq.getFechaHoraInicio().minusHours(12),
                bloqueoRq.getFechaHoraFin().plusHours(12));
        for (Cita cita : citasVentana) {
            if (!Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado())
                    && !Constantes.CITA_ATENDIDA.equalsIgnoreCase(cita.getEstado())) {
                LocalDateTime inicioCita = cita.getFechaHora();
                LocalDateTime finCita = obtenerFinCita(cita);
                if (inicioCita.isBefore(bloqueoRq.getFechaHoraFin())
                        && finCita.isAfter(bloqueoRq.getFechaHoraInicio())) {
                    throw new ConflictException(
                            "No se puede bloquear el horario porque el veterinario tiene una cita activa con la mascota "
                                    + (cita.getMascota() != null ? cita.getMascota().getNombreMascota() : "")
                                    + " a las " + inicioCita.format(FORMATO_FECHA_HORA));
                }
            }
        }

        BloqueoHorario bloqueo = new BloqueoHorario();
        bloqueo.setMedico(medico);
        bloqueo.setFechaHoraInicio(bloqueoRq.getFechaHoraInicio());
        bloqueo.setFechaHoraFin(bloqueoRq.getFechaHoraFin());
        bloqueo.setMotivo(bloqueoRq.getMotivo().trim());
        bloqueo.setActivo(true);
        bloqueo.setFechaCreacion(LocalDateTime.now());
        bloqueoHorarioRepository.save(bloqueo);

        return respuesta(HttpStatus.CREATED.value(), "Bloqueo de horario registrado correctamente");
    }

    @Override
    // Inactiva un bloqueo de horario para liberar la agenda del veterinario.
    public MiRespuestaRS inactivarBloqueoHorario(Long bloqueoId) throws BadRequestException {
        if (bloqueoId == null) {
            throw new BadRequestException("El ID del bloqueo de horario es obligatorio");
        }
        BloqueoHorario bloqueo = bloqueoHorarioRepository.findById(bloqueoId)
                .orElseThrow(() -> new NotFoundException("Bloqueo de horario no encontrado"));
        bloqueo.setActivo(false);
        bloqueoHorarioRepository.save(bloqueo);
        return respuesta(HttpStatus.OK.value(), "Bloqueo de horario eliminado correctamente");
    }

    @Override
    // Genera un archivo Excel (.xlsx) con las citas del veterinario asociado y rango de fechas seleccionado.
    public byte[] exportarCitasExcel(LocalDateTime fechaInicial, LocalDateTime fechaFinal,
                                     Long medicoId, String estado) throws BadRequestException {
        List<Cita> citas = listarCitasPorFecha(fechaInicial, fechaFinal, medicoId, estado);
        Medico medicoFiltro = medicoId != null ? buscarMedico(medicoId) : null;

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Agenda de Citas");

            // Estilos del reporte Excel
            CellStyle estiloTitulo = workbook.createCellStyle();
            Font fuenteTitulo = workbook.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());
            estiloTitulo.setFont(fuenteTitulo);
            estiloTitulo.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloTitulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloTitulo.setAlignment(HorizontalAlignment.CENTER);

            CellStyle estiloMeta = workbook.createCellStyle();
            Font fuenteMeta = workbook.createFont();
            fuenteMeta.setBold(true);
            estiloMeta.setFont(fuenteMeta);

            CellStyle estiloEncabezado = workbook.createCellStyle();
            Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            estiloEncabezado.setFont(fuenteEncabezado);
            estiloEncabezado.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
            estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);
            aplicarBordes(estiloEncabezado);

            CellStyle estiloCelda = workbook.createCellStyle();
            aplicarBordes(estiloCelda);

            // Encabezado del documento
            Row filaTitulo = sheet.createRow(0);
            Cell celdaTitulo = filaTitulo.createCell(0);
            celdaTitulo.setCellValue("REPORTE DE CITAS MÉDICAS VETERINARIAS");
            celdaTitulo.setCellStyle(estiloTitulo);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 13));

            Row filaMeta1 = sheet.createRow(1);
            Cell cMed = filaMeta1.createCell(0);
            cMed.setCellValue("Veterinario:");
            cMed.setCellStyle(estiloMeta);
            filaMeta1.createCell(1).setCellValue(medicoFiltro != null
                    ? nombreMedico(medicoFiltro) + " (" + (medicoFiltro.getEspecializacion() != null
                    ? medicoFiltro.getEspecializacion().getNombre() : "General") + ")"
                    : "Todos los veterinarios");

            Cell cGen = filaMeta1.createCell(4);
            cGen.setCellValue("Fecha generación:");
            cGen.setCellStyle(estiloMeta);
            filaMeta1.createCell(5).setCellValue(LocalDateTime.now().format(FORMATO_FECHA_HORA));

            Row filaMeta2 = sheet.createRow(2);
            Cell cRango = filaMeta2.createCell(0);
            cRango.setCellValue("Rango de fechas:");
            cRango.setCellStyle(estiloMeta);
            String textoRango = (fechaInicial != null && fechaFinal != null)
                    ? fechaInicial.format(FORMATO_FECHA) + " a " + fechaFinal.format(FORMATO_FECHA)
                    : "Todas las fechas";
            filaMeta2.createCell(1).setCellValue(textoRango);

            Cell cEst = filaMeta2.createCell(4);
            cEst.setCellValue("Filtro estado:");
            cEst.setCellStyle(estiloMeta);
            filaMeta2.createCell(5).setCellValue((estado != null && !estado.isBlank()) ? estado.toUpperCase() : "TODOS");

            // Fila de encabezados de tabla
            String[] columnas = {
                    "ID", "Fecha", "Hora Inicio", "Hora Fin", "Estado",
                    "Veterinario", "Especialidad", "Propietario", "Documento",
                    "Teléfono", "Mascota (Paciente)", "Raza", "Edad", "Motivo de Consulta"
            };
            Row filaHeader = sheet.createRow(4);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = filaHeader.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloEncabezado);
            }

            // Contadores de resumen
            int pendientes = 0;
            int programadas = 0;
            int confirmadas = 0;
            int atendidas = 0;
            int canceladas = 0;

            int rowIdx = 5;
            for (Cita cita : citas) {
                asegurarFechaHoraFin(cita);
                Row row = sheet.createRow(rowIdx++);

                String est = cita.getEstado() != null ? cita.getEstado().toUpperCase() : "";
                switch (est) {
                    case Constantes.CITA_PENDIENTE -> pendientes++;
                    case Constantes.CITA_PROGRAMADA -> programadas++;
                    case Constantes.CITA_CONFIRMADA -> confirmadas++;
                    case Constantes.CITA_ATENDIDA, Constantes.CITA_FINALIZADA -> atendidas++;
                    case Constantes.CITA_CANCELADA -> canceladas++;
                    default -> { }
                }

                Cliente cli = cita.getCliente();
                Mascota mas = cita.getMascota();
                Medico med = cita.getMedico();

                crearCelda(row, 0, cita.getId() != null ? String.valueOf(cita.getId()) : "-", estiloCelda);
                crearCelda(row, 1, cita.getFechaHora() != null ? cita.getFechaHora().format(FORMATO_FECHA) : "-", estiloCelda);
                crearCelda(row, 2, cita.getFechaHora() != null ? cita.getFechaHora().format(FORMATO_HORA) : "-", estiloCelda);
                crearCelda(row, 3, cita.getFechaHoraFin() != null ? cita.getFechaHoraFin().format(FORMATO_HORA) : "-", estiloCelda);
                crearCelda(row, 4, est, estiloCelda);
                crearCelda(row, 5, nombreMedico(med), estiloCelda);
                crearCelda(row, 6, (med != null && med.getEspecializacion() != null) ? med.getEspecializacion().getNombre() : "-", estiloCelda);
                crearCelda(row, 7, cli != null ? (cli.getNombres() + " " + cli.getApellidos()).trim() : "-", estiloCelda);
                crearCelda(row, 8, cli != null ? (cli.getTipoDocumento() + " " + cli.getNumeroDocumento()).trim() : "-", estiloCelda);
                crearCelda(row, 9, cli != null && cli.getTelefono() != null ? cli.getTelefono() : "-", estiloCelda);
                crearCelda(row, 10, mas != null ? mas.getNombreMascota() : "-", estiloCelda);
                crearCelda(row, 11, (mas != null && mas.getRaza() != null) ? mas.getRaza().getNombre() : "-", estiloCelda);
                crearCelda(row, 12, (mas != null && mas.getEdad() != null) ? mas.getEdad() + " año(s)" : "-", estiloCelda);
                crearCelda(row, 13, cita.getMotivo() != null ? cita.getMotivo() : "-", estiloCelda);
            }

            // Bloque de resumen de totales
            rowIdx++;
            Row filaResumenHeader = sheet.createRow(rowIdx++);
            Cell cellResTitulo = filaResumenHeader.createCell(0);
            cellResTitulo.setCellValue("RESUMEN DE CITAS POR ESTADO");
            cellResTitulo.setCellStyle(estiloMeta);

            agregarFilaResumen(sheet, rowIdx++, "Citas Pendientes:", pendientes, estiloMeta, estiloCelda);
            agregarFilaResumen(sheet, rowIdx++, "Citas Programadas:", programadas, estiloMeta, estiloCelda);
            agregarFilaResumen(sheet, rowIdx++, "Citas Confirmadas:", confirmadas, estiloMeta, estiloCelda);
            agregarFilaResumen(sheet, rowIdx++, "Citas Atendidas / Finalizadas:", atendidas, estiloMeta, estiloCelda);
            agregarFilaResumen(sheet, rowIdx++, "Citas Canceladas:", canceladas, estiloMeta, estiloCelda);
            agregarFilaResumen(sheet, rowIdx, "TOTAL CITAS:", citas.size(), estiloMeta, estiloCelda);

            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            throw new BadRequestException("No fue posible generar el reporte Excel de citas");
        }
    }

    private void crearCelda(Row row, int col, String valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellValue(valor);
        cell.setCellStyle(estilo);
    }

    private void agregarFilaResumen(Sheet sheet, int rowIdx, String etiqueta, int cantidad,
                                    CellStyle estiloEtiqueta, CellStyle estiloValor) {
        Row row = sheet.createRow(rowIdx);
        Cell c0 = row.createCell(0);
        c0.setCellValue(etiqueta);
        c0.setCellStyle(estiloEtiqueta);
        Cell c1 = row.createCell(1);
        c1.setCellValue(cantidad);
        c1.setCellStyle(estiloValor);
    }

    private void aplicarBordes(CellStyle style) {
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private Cita buscarCita(Long citaId) throws BadRequestException {
        return citaRepository.findById(citaId)
                .orElseThrow(() -> new NotFoundException("Cita no encontrada"));
    }

    private Medico buscarMedico(Long medicoId) throws BadRequestException {
        return medicoRepository.findById(medicoId)
                .orElseThrow(() -> new NotFoundException("Veterinario no encontrado"));
    }

    private String normalizarEstado(String estado) {
        if (estado == null) {
            return "";
        }
        String limpio = estado.trim().toUpperCase();
        // FINALIZADA se normaliza a ATENDIDA para mantener compatibilidad con historias médicas.
        if (Constantes.CITA_FINALIZADA.equals(limpio)) {
            return Constantes.CITA_ATENDIDA;
        }
        return limpio;
    }

    private void asegurarFechaHoraFin(Cita cita) {
        if (cita != null && cita.getFechaHora() != null && cita.getFechaHoraFin() == null) {
            cita.setFechaHoraFin(cita.getFechaHora().plusMinutes(Constantes.DURACION_DEFECTO_CITA_MINUTOS));
        }
    }

    private LocalDateTime obtenerFinCita(Cita cita) {
        if (cita.getFechaHoraFin() != null) {
            return cita.getFechaHoraFin();
        }
        return cita.getFechaHora().plusMinutes(Constantes.DURACION_DEFECTO_CITA_MINUTOS);
    }

    private LocalDateTime calcularYValidarFechaHoraFin(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin)
            throws BadRequestException {
        if (fechaHoraFin == null) {
            return fechaHoraInicio.plusMinutes(Constantes.DURACION_DEFECTO_CITA_MINUTOS);
        }
        if (!fechaHoraFin.isAfter(fechaHoraInicio)) {
            throw new BadRequestException("La hora de finalización debe ser posterior a la hora de inicio");
        }
        if (!fechaHoraInicio.toLocalDate().equals(fechaHoraFin.toLocalDate())) {
            throw new BadRequestException("La cita debe iniciar y finalizar el mismo día");
        }
        return fechaHoraFin;
    }

    private void validarDisponibilidad(Long citaId, Long medicoId, Integer mascotaId,
                                       LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin)
            throws BadRequestException {
        // 1. Validar que el horario no coincida con un bloqueo activo del veterinario.
        if (bloqueoHorarioRepository != null) {
            List<BloqueoHorario> bloqueos = bloqueoHorarioRepository
                    .findByMedico_IdAndActivoTrueAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                            medicoId, fechaHoraFin, fechaHoraInicio);
            if (!bloqueos.isEmpty()) {
                BloqueoHorario bloqueo = bloqueos.get(0);
                throw new ConflictException("El veterinario tiene un bloqueo de horario (" + bloqueo.getMotivo()
                        + ") desde " + bloqueo.getFechaHoraInicio().format(FORMATO_FECHA_HORA)
                        + " hasta " + bloqueo.getFechaHoraFin().format(FORMATO_FECHA_HORA));
            }
        }

        // 2. Validar margen de 15 minutos entre inicios y solapamiento de intervalos [inicio, fin].
        LocalDateTime margenInicio = fechaHoraInicio.minusMinutes(Constantes.MINUTOS_MARGEN_CITA);
        LocalDateTime margenFin = fechaHoraInicio.plusMinutes(Constantes.MINUTOS_MARGEN_CITA);

        List<Cita> citasMedicoCercanas = citaRepository.findByMedico_IdAndFechaHoraBetween(
                medicoId, fechaHoraInicio.minusHours(12), fechaHoraFin.plusHours(12));
        if (existeConflictoHorario(citasMedicoCercanas, citaId, fechaHoraInicio, fechaHoraFin, margenInicio, margenFin)) {
            throw new ConflictException(
                    "El veterinario no está disponible, ya tiene una cita que se cruza o está a menos de 15 minutos de ese horario");
        }

        List<Cita> citasMascotaCercanas = citaRepository.findByMascota_MascotaIdAndFechaHoraBetween(
                mascotaId, fechaHoraInicio.minusHours(12), fechaHoraFin.plusHours(12));
        if (existeConflictoHorario(citasMascotaCercanas, citaId, fechaHoraInicio, fechaHoraFin, margenInicio, margenFin)) {
            throw new ConflictException("La mascota ya tiene una cita programada en ese horario");
        }
    }

    private boolean existeConflictoHorario(List<Cita> citas, Long citaIdActual,
                                           LocalDateTime nuevoInicio, LocalDateTime nuevoFin,
                                           LocalDateTime margenInicio, LocalDateTime margenFin) {
        if (citas == null) {
            return false;
        }
        for (Cita cita : citas) {
            boolean esLaMismaCita = cita.getId() != null && cita.getId().equals(citaIdActual);
            boolean estaCancelada = Constantes.CITA_CANCELADA.equalsIgnoreCase(cita.getEstado());
            if (esLaMismaCita || estaCancelada) {
                continue;
            }
            LocalDateTime existenteInicio = cita.getFechaHora();
            LocalDateTime existenteFin = obtenerFinCita(cita);

            // Regla 1: Ninguna cita activa a menos de 15 minutos de la hora de inicio (rango inclusivo).
            boolean dentroMargen15Min = !existenteInicio.isBefore(margenInicio) && !existenteInicio.isAfter(margenFin);
            // Regla 2: Solapamiento entre los rangos [nuevoInicio, nuevoFin] y [existenteInicio, existenteFin].
            boolean solapaRango = existenteInicio.isBefore(nuevoFin) && existenteFin.isAfter(nuevoInicio);

            if (dentroMargen15Min || solapaRango) {
                return true;
            }
        }
        return false;
    }

    private String construirLineaHistorial(String historialActual, String usuarioCambio, String detalle) {
        String autor = (usuarioCambio != null && !usuarioCambio.isBlank()) ? " (" + usuarioCambio.trim() + ")" : "";
        String nuevaLinea = "[" + LocalDateTime.now().format(FORMATO_FECHA_HORA) + "]" + autor + " " + detalle;
        if (historialActual == null || historialActual.isBlank()) {
            return nuevaLinea;
        }
        return historialActual + "\n" + nuevaLinea;
    }

    private String formatearRango(LocalDateTime inicio, LocalDateTime fin) {
        if (inicio == null) {
            return "-";
        }
        if (fin == null) {
            return inicio.format(FORMATO_FECHA_HORA);
        }
        return inicio.format(FORMATO_FECHA_HORA) + " - " + fin.format(FORMATO_HORA);
    }

    private String nombreMedico(Medico medico) {
        if (medico == null) {
            return "-";
        }
        return ((medico.getNombres() != null ? medico.getNombres() : "") + " "
                + (medico.getApellidos() != null ? medico.getApellidos() : "")).trim();
    }

    private void validarCita(CitaRq citaRq) throws BadRequestException {
        if (citaRq == null || citaRq.getMedicoId() == null || citaRq.getFechaHora() == null
                || citaRq.getEstado() == null || citaRq.getEstado().isBlank()
                || citaRq.getMotivo() == null || citaRq.getMotivo().isBlank()) {
            throw new BadRequestException("Veterinario, fecha, hora, estado y motivo son obligatorios");
        }
    }

    private void validarRangoFechas(LocalDateTime fechaInicial, LocalDateTime fechaFinal) throws BadRequestException {
        if (fechaInicial == null || fechaFinal == null || fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("El rango de fechas no es válido");
        }
    }

    private MiRespuestaRS respuesta(int status, String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(status);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
