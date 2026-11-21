package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.CargaMasivaMedicamento;
import com.uniminuto.clinica.entity.LoteMedicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaMasivaMedicamentoRS;
import com.uniminuto.clinica.models.ErrorCargaMasivaMedicamento;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.LoteMedicamentoRq;
import com.uniminuto.clinica.models.LoteMedicamentoRS;
import com.uniminuto.clinica.repository.LoteMedicamentoRepository;
import com.uniminuto.clinica.repository.CargaMasivaMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Service
/** Implementa las operaciones del inventario de medicamentos. */
public class MedicamentoServiceImpl implements MedicamentoService {

    private static final Pattern CODIGO_PATTERN = Pattern.compile("^[A-Za-z0-9]{8}$");

    @Autowired
    /** Repositorio de medicamentos. */
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    /** Repositorio que registra los movimientos de inventario. */
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;

    @Autowired
    private LoteMedicamentoRepository loteMedicamentoRepository;

    @Autowired
    private CargaMasivaMedicamentoRepository cargaMasivaRepository;

    @Override
    public List<Medicamento> obtenerMedicamentos() throws BadRequestException {
        return medicamentoRepository.findAllByOrderByFechaVenceAsc();
    }

    @Override
    public Medicamento consultarMedicamento(Long medicamentoId) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        return medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
    }

    @Override
    @Transactional
    public MiRespuestaRS guardarMedicamento(Medicamento medicamento) throws BadRequestException {
        validarMedicamento(medicamento);

        String nombre = medicamento.getNombre().trim();
        String codigo = medicamento.getCodigo().trim().toUpperCase();

        if (medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese nombre");
        }

        if (medicamentoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese código");
        }

        medicamento.setNombre(nombre);
        medicamento.setCodigo(codigo);
        medicamento.setDescripcion(medicamento.getDescripcion() == null ? null : medicamento.getDescripcion().trim());
        medicamento
                .setPresentacion(medicamento.getPresentacion() == null ? null : medicamento.getPresentacion().trim());
        medicamento.setLote(medicamento.getLote() == null ? null : medicamento.getLote().trim());
        medicamento.setRegistroInvima(
                medicamento.getRegistroInvima() == null ? null : medicamento.getRegistroInvima().trim());
        medicamento.setActivo(medicamento.getActivo() == null || medicamento.getActivo());
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());

        Medicamento guardado = medicamentoRepository.save(medicamento);
        if (guardado.getCantidadDisponible() != null && guardado.getCantidadDisponible() > 0) {
            registrarMovimientoEntrada(guardado.getId(), guardado.getCantidadDisponible(),
                    "INGRESO INICIAL INVENTARIO (Lote: " + (guardado.getLote() != null ? guardado.getLote() : "N/A")
                            + ")",
                    "INGRESO_INICIAL", guardado.getId());
        }
        return respuesta("Medicamento registrado correctamente");
    }

    @Override
    public CargaMasivaMedicamentoRS importarMasivo(MultipartFile archivo) throws BadRequestException {
        return importarMasivo(archivo, true, "INVENTARIO");
    }

    @Override
    @Transactional
    public CargaMasivaMedicamentoRS importarMasivo(MultipartFile archivo, boolean confirmar, String usuario)
            throws BadRequestException {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Debe seleccionar un archivo para realizar la carga masiva");
        }

        String nombreArchivo = archivo.getOriginalFilename() == null ? ""
                : archivo.getOriginalFilename().toLowerCase(Locale.ROOT);
        List<String[]> filas;

        try {
            if (nombreArchivo.endsWith(".csv")) {
                filas = leerCsv(archivo);
            } else if (nombreArchivo.endsWith(".xlsx")) {
                filas = leerXlsx(archivo);
            } else {
                throw new BadRequestException("El archivo debe estar en formato CSV o XLSX");
            }
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo cargado");
        }

        if (filas.isEmpty()) {
            throw new BadRequestException("El archivo no tiene registros para importar");
        }

        List<String> encabezadosEsperados = Arrays.asList(
                "nombre", "descripcion", "presentacion", "codigo", "lote",
                "registroInvima", "requiereRegistroInvima", "precioCompra", "precioVenta",
                "cantidadDisponible", "stockMinimo", "fechaCompra", "fechaIngresoInventario",
                "fechaVence", "fechaProximoPedido", "activo");

        String[] encabezados = filas.getFirst();
        for (int i = 0; i < encabezadosEsperados.size(); i++) {
            String esperado = encabezadosEsperados.get(i);
            if (i >= encabezados.length || encabezados[i] == null
                    || !encabezados[i].trim().equalsIgnoreCase(esperado)) {
                throw new BadRequestException(
                        "La estructura del archivo es inválida. Encabezados esperados: " + encabezadosEsperados);
            }
        }

        CargaMasivaMedicamentoRS resultado = new CargaMasivaMedicamentoRS();
        List<Medicamento> validos = new ArrayList<>();
        Set<String> codigosArchivo = new HashSet<>();
        Set<String> nombresArchivo = new HashSet<>();

        for (int i = 1; i < filas.size(); i++) {
            String[] fila = filas.get(i);
            resultado.setTotalRegistros(resultado.getTotalRegistros() + 1);

            if (fila == null || fila.length == 0
                    || Arrays.stream(fila).allMatch(value -> value == null || value.trim().isEmpty())) {
                continue;
            }

            try {
                Medicamento medicamento = construirMedicamentoDesdeFila(fila, i + 1);
                validarMedicamento(medicamento);
                if (medicamentoRepository.existsByCodigoIgnoreCase(medicamento.getCodigo().trim().toUpperCase())) {
                    throw new BadRequestException("Código duplicado en la base de datos");
                }
                if (medicamentoRepository.existsByNombreIgnoreCase(medicamento.getNombre().trim())) {
                    throw new BadRequestException("Nombre duplicado en la base de datos");
                }
                if (!codigosArchivo.add(medicamento.getCodigo().trim().toUpperCase(Locale.ROOT))) {
                    throw new BadRequestException("Código duplicado en el archivo");
                }
                if (!nombresArchivo.add(medicamento.getNombre().trim().toLowerCase(Locale.ROOT))) {
                    throw new BadRequestException("Nombre duplicado en el archivo");
                }
                medicamento.setFechaCreacionRegistro(LocalDateTime.now());
                medicamento.setFechaModificacionRegistro(LocalDateTime.now());
                validos.add(medicamento);
            } catch (BadRequestException ex) {
                ErrorCargaMasivaMedicamento error = new ErrorCargaMasivaMedicamento();
                error.setFila(i + 1);
                error.setMensaje(ex.getMessage());
                resultado.getErrores().add(error);
            }
        }

        resultado.setRegistrosValidos(validos.size());
        resultado.setRegistrosInvalidos(resultado.getErrores().size());

        if (!confirmar) {
            resultado.setImportados(validos);
            return resultado;
        }

        List<Medicamento> importados = validos.isEmpty() ? List.of() : medicamentoRepository.saveAll(validos);
        for (Medicamento imp : importados) {
            if (imp.getCantidadDisponible() != null && imp.getCantidadDisponible() > 0) {
                registrarMovimientoEntrada(imp.getId(), imp.getCantidadDisponible(),
                        "CARGA MASIVA INVENTARIO", "CARGA_MASIVA", imp.getId(), usuario);
            }
        }
        resultado.setImportados(importados);
        CargaMasivaMedicamento carga = new CargaMasivaMedicamento();
        carga.setNombreArchivo(archivo.getOriginalFilename());
        carga.setTotalRegistros(resultado.getTotalRegistros());
        carga.setRegistrosImportados(importados.size());
        carga.setRegistrosInvalidos(resultado.getRegistrosInvalidos());
        carga.setUsuarioRegistro(usuario == null || usuario.isBlank() ? "SISTEMA" : usuario);
        carga.setFechaRegistro(LocalDateTime.now());
        cargaMasivaRepository.save(carga);
        return resultado;
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarMedicamento(Medicamento medicamento) throws BadRequestException {
        if (medicamento == null || medicamento.getId() == null) {
            throw new BadRequestException("El identificador del medicamento es obligatorio para actualizar");
        }

        Medicamento medicamentoActual = medicamentoRepository.findByIdForUpdate(medicamento.getId())
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        asegurarLoteInicial(medicamentoActual);

        validarMedicamento(medicamento);

        String nombre = medicamento.getNombre().trim();
        String codigo = medicamento.getCodigo().trim().toUpperCase();

        if (!nombre.equalsIgnoreCase(medicamentoActual.getNombre())
                && medicamentoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese nombre");
        }

        if (!codigo.equalsIgnoreCase(medicamentoActual.getCodigo())
                && medicamentoRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new BadRequestException("Ya existe un medicamento registrado con ese código");
        }

        int stockAnterior = medicamentoActual.getCantidadDisponible() != null
                ? medicamentoActual.getCantidadDisponible()
                : 0;
        int stockNuevo = medicamento.getCantidadDisponible() != null ? medicamento.getCantidadDisponible() : 0;

        medicamentoActual.setNombre(nombre);
        medicamentoActual
                .setDescripcion(medicamento.getDescripcion() == null ? null : medicamento.getDescripcion().trim());
        medicamentoActual
                .setPresentacion(medicamento.getPresentacion() == null ? null : medicamento.getPresentacion().trim());
        medicamentoActual.setCodigo(codigo);
        medicamentoActual.setRegistroInvima(
                medicamento.getRegistroInvima() == null ? null : medicamento.getRegistroInvima().trim());
        medicamentoActual.setRequiereRegistroInvima(Boolean.TRUE.equals(medicamento.getRequiereRegistroInvima()));
        medicamentoActual.setPrecioVenta(medicamento.getPrecioVenta());
        medicamentoActual.setStockMinimo(medicamento.getStockMinimo());
        medicamentoActual.setFechaProximoPedido(medicamento.getFechaProximoPedido());
        medicamentoActual.setActivo(medicamento.getActivo() == null || medicamento.getActivo());
        medicamentoActual.setFechaModificacionRegistro(LocalDateTime.now());

        if (stockNuevo > stockAnterior) {
            medicamentoActual.setCantidadDisponible(stockNuevo);
            medicamentoRepository.save(medicamentoActual);
            registrarMovimientoEntrada(medicamentoActual.getId(), stockNuevo - stockAnterior,
                    "AJUSTE DE ENTRADA / REABASTECIMIENTO", "AJUSTE_INVENTARIO", medicamentoActual.getId());
        } else if (stockNuevo < stockAnterior) {
            consumirLotesParaAjuste(medicamentoActual, stockAnterior - stockNuevo);
            medicamentoActual.setCantidadDisponible(stockNuevo);
            medicamentoRepository.save(medicamentoActual);
        } else {
            medicamentoActual.setCantidadDisponible(stockNuevo);
            medicamentoRepository.save(medicamentoActual);
        }
        return respuesta("Medicamento actualizado correctamente");
    }

    private void registrarMovimientoEntrada(Long medicamentoId, Integer cantidad, String motivo,
            String referenciaTipo, Long referenciaId) {
        registrarMovimientoEntrada(medicamentoId, cantidad, motivo, referenciaTipo, referenciaId, "INVENTARIO");
    }

    private void registrarMovimientoEntrada(Long medicamentoId, Integer cantidad, String motivo,
            String referenciaTipo, Long referenciaId, String usuario) {
        if (movimientoMedicamentoRepository == null || medicamentoId == null || cantidad == null || cantidad <= 0) {
            return;
        }
        Medicamento medicamento = medicamentoRepository.findByIdForUpdate(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        LoteMedicamento lote = crearLoteDesdeMedicamento(medicamento, cantidad);
        MovimientoMedicamento mov = new MovimientoMedicamento();
        mov.setMedicamentoId(medicamentoId);
        mov.setTipo("ENTRADA");
        mov.setCantidad(cantidad);
        mov.setMotivo(motivo);
        mov.setReferenciaTipo(referenciaTipo);
        mov.setReferenciaId(referenciaId);
        mov.setUsuarioRegistro(usuario == null || usuario.isBlank() ? "SISTEMA" : usuario.trim());
        mov.setFechaRegistro(LocalDateTime.now());
        mov.setCodigoLote(lote.getCodigoLote());
        movimientoMedicamentoRepository.save(mov);
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarSalidaInventario(Long medicamentoId, Integer cantidad, String motivo,
            Long referenciaId, String referenciaTipo) throws BadRequestException {
        return registrarSalidaInventario(medicamentoId, cantidad, motivo, referenciaId, referenciaTipo, null);
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarSalidaInventario(Long medicamentoId, Integer cantidad, String motivo,
            Long referenciaId, String referenciaTipo, String usuarioRegistro) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            throw new BadRequestException("La cantidad a descontar debe ser mayor que cero");
        }

        Medicamento medicamento = medicamentoRepository.findByIdForUpdate(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));

        if (!Boolean.TRUE.equals(medicamento.getActivo())) {
            throw new BadRequestException("No se puede dispensar un medicamento inactivo");
        }
        asegurarLoteInicial(medicamento);
        List<LoteMedicamento> lotes = loteMedicamentoRepository.buscarDisponiblesPorVencer(medicamentoId, LocalDate.now());
        int disponibleVigente = lotes.stream().mapToInt(LoteMedicamento::getCantidadDisponible).sum();
        if (disponibleVigente < cantidad) {
            if (medicamento.getCantidadDisponible() >= cantidad) {
                throw new BadRequestException("No hay suficientes lotes vigentes; las unidades restantes están vencidas");
            }
            throw new BadRequestException("No hay suficiente inventario para despachar " + cantidad + " unidades");
        }

        int cantidadPendiente = cantidad;
        List<MovimientoMedicamento> movimientos = new ArrayList<>();
        for (LoteMedicamento lote : lotes) {
            if (cantidadPendiente == 0) break;
            int salida = Math.min(cantidadPendiente, lote.getCantidadDisponible());
            lote.setCantidadDisponible(lote.getCantidadDisponible() - salida);
            movimientos.add(crearMovimientoSalida(medicamentoId, salida, motivo, referenciaId,
                    referenciaTipo, usuarioRegistro, lote.getCodigoLote()));
            cantidadPendiente -= salida;
        }
        loteMedicamentoRepository.saveAll(lotes);
        int stockRegistrado = medicamento.getCantidadDisponible() == null ? 0 : medicamento.getCantidadDisponible();
        medicamento.setCantidadDisponible(Math.max(0, stockRegistrado - cantidad));
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);
        movimientoMedicamentoRepository.saveAll(movimientos);

        return respuesta("Salida registrada por FEFO: "
                + movimientos.stream().map(m -> m.getCodigoLote() + " (" + m.getCantidad() + ")")
                        .reduce((a, b) -> a + ", " + b).orElse(""));
    }

    @Override
    public List<MovimientoMedicamento> consultarHistorialInventario(Long medicamentoId) throws BadRequestException {
        if (medicamentoId == null || medicamentoId <= 0) {
            throw new BadRequestException("El identificador del medicamento es obligatorio");
        }
        consultarMedicamento(medicamentoId);
        return movimientoMedicamentoRepository.findByMedicamentoIdOrderByFechaRegistroDesc(medicamentoId);
    }

    @Override
    @Transactional
    public MiRespuestaRS registrarLote(Long medicamentoId, LoteMedicamentoRq solicitud) {
        Medicamento medicamento = medicamentoRepository.findByIdForUpdate(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        validarLote(solicitud);
        if (!Boolean.TRUE.equals(medicamento.getActivo())) {
            throw new BadRequestException("No se pueden registrar lotes para un medicamento inactivo");
        }

        LoteMedicamento lote = new LoteMedicamento();
        lote.setMedicamento(medicamento);
        lote.setCodigoLote(solicitud.getCodigoLote().trim());
        lote.setCantidadDisponible(solicitud.getCantidad());
        lote.setPrecioCompra(solicitud.getPrecioCompra());
        lote.setFechaCompra(solicitud.getFechaCompra());
        lote.setFechaIngreso(solicitud.getFechaIngreso());
        lote.setFechaVencimiento(solicitud.getFechaVencimiento());
        lote.setActivo(true);
        lote.setFechaCreacion(LocalDateTime.now());
        loteMedicamentoRepository.save(lote);

        int stock = medicamento.getCantidadDisponible() == null ? 0 : medicamento.getCantidadDisponible();
        medicamento.setCantidadDisponible(stock + solicitud.getCantidad());
        medicamento.setLote(lote.getCodigoLote());
        medicamento.setPrecioCompra(solicitud.getPrecioCompra());
        medicamento.setFechaCompra(solicitud.getFechaCompra());
        medicamento.setFechaIngresoInventario(solicitud.getFechaIngreso());
        medicamento.setFechaVence(primeraFechaVencimiento(medicamentoId, solicitud.getFechaVencimiento()));
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);

        MovimientoMedicamento movimiento = new MovimientoMedicamento();
        movimiento.setMedicamentoId(medicamentoId);
        movimiento.setTipo("ENTRADA");
        movimiento.setCantidad(solicitud.getCantidad());
        movimiento.setMotivo("INGRESO DE LOTE " + lote.getCodigoLote());
        movimiento.setReferenciaTipo("INGRESO_LOTE");
        movimiento.setReferenciaId(lote.getId());
        movimiento.setUsuarioRegistro("INVENTARIO");
        movimiento.setFechaRegistro(LocalDateTime.now());
        movimiento.setCodigoLote(lote.getCodigoLote());
        movimientoMedicamentoRepository.save(movimiento);
        return respuesta("Lote registrado correctamente");
    }

    @Override
    @Transactional
    public List<LoteMedicamentoRS> listarLotes(Long medicamentoId) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        asegurarLoteInicial(medicamento);
        return loteMedicamentoRepository
                .findByMedicamentoIdOrderByFechaVencimientoAscFechaIngresoAscIdAsc(medicamentoId)
                .stream().map(l -> new LoteMedicamentoRS(l.getId(), medicamentoId, l.getCodigoLote(),
                        l.getCantidadDisponible(), l.getPrecioCompra(), l.getFechaCompra(), l.getFechaIngreso(),
                        l.getFechaVencimiento(), l.getActivo()))
                .toList();
    }

    @Override
    @Transactional
    public int obtenerStockVigente(Long medicamentoId) {
        Medicamento medicamento = medicamentoRepository.findById(medicamentoId)
                .orElseThrow(() -> new BadRequestException("Medicamento no encontrado"));
        asegurarLoteInicial(medicamento);
        return loteMedicamentoRepository.buscarDisponiblesPorVencer(medicamentoId, LocalDate.now())
                .stream().mapToInt(LoteMedicamento::getCantidadDisponible).sum();
    }

    private void asegurarLoteInicial(Medicamento medicamento) {
        if (loteMedicamentoRepository.existsByMedicamentoId(medicamento.getId())
                || medicamento.getCantidadDisponible() == null || medicamento.getCantidadDisponible() <= 0) {
            return;
        }
        crearLoteDesdeMedicamento(medicamento, medicamento.getCantidadDisponible());
    }

    private LoteMedicamento crearLoteDesdeMedicamento(Medicamento medicamento, Integer cantidad) {
        LoteMedicamento lote = new LoteMedicamento();
        lote.setMedicamento(medicamento);
        lote.setCodigoLote(medicamento.getLote() == null || medicamento.getLote().isBlank()
                ? "LEGACY-" + medicamento.getId() : medicamento.getLote().trim());
        lote.setCantidadDisponible(cantidad);
        lote.setPrecioCompra(medicamento.getPrecioCompra());
        lote.setFechaCompra(medicamento.getFechaCompra() == null ? LocalDate.now() : medicamento.getFechaCompra());
        lote.setFechaIngreso(medicamento.getFechaIngresoInventario() == null
                ? LocalDate.now() : medicamento.getFechaIngresoInventario());
        lote.setFechaVencimiento(medicamento.getFechaVence() == null
                ? LocalDate.now().plusYears(1) : medicamento.getFechaVence());
        lote.setActivo(true);
        lote.setFechaCreacion(LocalDateTime.now());
        return loteMedicamentoRepository.save(lote);
    }

    private void consumirLotesParaAjuste(Medicamento medicamento, int cantidad) {
        asegurarLoteInicial(medicamento);
        List<LoteMedicamento> lotes = loteMedicamentoRepository.buscarTodosParaAjuste(medicamento.getId());
        int total = lotes.stream().mapToInt(LoteMedicamento::getCantidadDisponible).sum();
        if (total < cantidad) {
            throw new BadRequestException("No se puede reducir el inventario por debajo de las existencias trazadas por lote");
        }
        int pendiente = cantidad;
        List<MovimientoMedicamento> movimientos = new ArrayList<>();
        for (LoteMedicamento lote : lotes) {
            if (pendiente == 0) break;
            int salida = Math.min(pendiente, lote.getCantidadDisponible());
            lote.setCantidadDisponible(lote.getCantidadDisponible() - salida);
            movimientos.add(crearMovimientoSalida(medicamento.getId(), salida,
                    "AJUSTE DE SALIDA DE INVENTARIO", medicamento.getId(), "AJUSTE_INVENTARIO",
                    "INVENTARIO", lote.getCodigoLote()));
            pendiente -= salida;
        }
        loteMedicamentoRepository.saveAll(lotes);
        movimientoMedicamentoRepository.saveAll(movimientos);
    }

    private MovimientoMedicamento crearMovimientoSalida(Long medicamentoId, int cantidad, String motivo,
            Long referenciaId, String referenciaTipo, String usuario, String codigoLote) {
        MovimientoMedicamento movimiento = new MovimientoMedicamento();
        movimiento.setMedicamentoId(medicamentoId);
        movimiento.setTipo("SALIDA");
        movimiento.setCantidad(cantidad);
        movimiento.setMotivo(motivo == null || motivo.isBlank() ? "DESPACHO" : motivo.trim());
        movimiento.setReferenciaId(referenciaId);
        movimiento.setReferenciaTipo(referenciaTipo == null || referenciaTipo.isBlank() ? "AJUSTE" : referenciaTipo.trim());
        movimiento.setUsuarioRegistro(usuario == null || usuario.isBlank() ? "SISTEMA" : usuario.trim());
        movimiento.setFechaRegistro(LocalDateTime.now());
        movimiento.setCodigoLote(codigoLote);
        return movimiento;
    }

    private LocalDate primeraFechaVencimiento(Long medicamentoId, LocalDate fallback) {
        return loteMedicamentoRepository
                .findByMedicamentoIdOrderByFechaVencimientoAscFechaIngresoAscIdAsc(medicamentoId)
                .stream().filter(l -> l.getCantidadDisponible() > 0)
                .map(LoteMedicamento::getFechaVencimiento).filter(Objects::nonNull)
                .filter(fecha -> !fecha.isBefore(LocalDate.now())).findFirst().orElse(fallback);
    }

    private void validarLote(LoteMedicamentoRq lote) {
        if (lote == null || lote.getCodigoLote() == null || lote.getCodigoLote().isBlank()
                || lote.getCodigoLote().trim().length() > 50 || lote.getCantidad() == null || lote.getCantidad() <= 0
                || lote.getPrecioCompra() == null || lote.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0
                || lote.getFechaCompra() == null || lote.getFechaIngreso() == null
                || lote.getFechaVencimiento() == null) {
            throw new BadRequestException("Lote, cantidad positiva, precio de compra y fechas son obligatorios");
        }
        if (lote.getFechaCompra().isAfter(lote.getFechaIngreso())
                || lote.getFechaIngreso().isAfter(LocalDate.now())
                || lote.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BadRequestException("Las fechas del lote no son válidas o el lote ya está vencido");
        }
    }

    private List<String[]> leerCsv(MultipartFile archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(archivo.getInputStream()))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                if (linea.isBlank()) {
                    continue;
                }
                filas.add(linea.split(",", -1));
            }
        }
        return filas;
    }

    private List<String[]> leerXlsx(MultipartFile archivo) throws IOException {
        List<String[]> filas = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(archivo.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row == null) {
                    continue;
                }
                List<String> valores = new ArrayList<>();
                int ultimaColumna = Math.max(16, row.getLastCellNum());
                for (int i = 0; i < ultimaColumna; i++) {
                    if (row.getCell(i) == null) {
                        valores.add("");
                    } else {
                        valores.add(row.getCell(i).toString().trim());
                    }
                }
                filas.add(valores.toArray(String[]::new));
            }
        }
        return filas;
    }

    private Medicamento construirMedicamentoDesdeFila(String[] fila, int numeroFila) throws BadRequestException {
        if (fila.length < 16) {
            throw new BadRequestException("La fila " + numeroFila + " no tiene la cantidad mínima de columnas");
        }

        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(normalizarCampo(fila[0], "nombre"));
        medicamento.setDescripcion(normalizarCampoOpcional(fila[1]));
        medicamento.setPresentacion(normalizarCampoOpcional(fila[2]));
        medicamento.setCodigo(normalizarCampo(fila[3], "codigo"));
        medicamento.setLote(normalizarCampoOpcional(fila[4]));
        medicamento.setRegistroInvima(normalizarCampoOpcional(fila[5]));
        medicamento.setRequiereRegistroInvima(parseBoolean(fila[6], "requiereRegistroInvima"));
        medicamento.setPrecioCompra(parseBigDecimal(fila[7], "precioCompra"));
        medicamento.setPrecioVenta(parseBigDecimal(fila[8], "precioVenta"));
        medicamento.setCantidadDisponible(parseInteger(fila[9], "cantidadDisponible"));
        medicamento.setStockMinimo(parseInteger(fila[10], "stockMinimo"));
        medicamento.setFechaCompra(parseLocalDate(fila[11], "fechaCompra"));
        medicamento.setFechaIngresoInventario(parseLocalDate(fila[12], "fechaIngresoInventario"));
        medicamento.setFechaVence(parseLocalDate(fila[13], "fechaVence"));
        medicamento.setFechaProximoPedido(parseLocalDateOpcional(fila[14]));
        medicamento.setActivo(parseBoolean(fila[15], "activo"));
        return medicamento;
    }

    private String normalizarCampo(String valor, String nombreCampo) throws BadRequestException {
        if (valor == null || valor.trim().isEmpty()) {
            throw new BadRequestException("El campo '" + nombreCampo + "' es obligatorio");
        }
        return valor.trim();
    }

    private String normalizarCampoOpcional(String valor) {
        if (valor == null) {
            return null;
        }
        String resultado = valor.trim();
        return resultado.isEmpty() ? null : resultado;
    }

    private BigDecimal parseBigDecimal(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return new BigDecimal(texto.replace(".", "").replace(",", "."));
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe ser numérico");
        }
    }

    private Integer parseInteger(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe ser un número entero");
        }
    }

    private LocalDate parseLocalDate(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampo(valor, nombreCampo);
        try {
            return LocalDate.parse(texto);
        } catch (DateTimeParseException ex) {
            throw new BadRequestException("El campo '" + nombreCampo + "' debe tener formato yyyy-MM-dd");
        }
    }

    private LocalDate parseLocalDateOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private Boolean parseBoolean(String valor, String nombreCampo) throws BadRequestException {
        String texto = normalizarCampoOpcional(valor);
        if (texto == null) {
            return false;
        }
        String normalizado = texto.trim().toLowerCase(Locale.ROOT);
        if ("true".equals(normalizado) || "1".equals(normalizado) || "si".equals(normalizado)
                || "yes".equals(normalizado)) {
            return true;
        }
        if ("false".equals(normalizado) || "0".equals(normalizado) || "no".equals(normalizado)) {
            return false;
        }
        throw new BadRequestException("El campo '" + nombreCampo + "' debe ser verdadero o falso");
    }

    private void validarMedicamento(Medicamento medicamento) throws BadRequestException {
        if (medicamento == null) {
            throw new BadRequestException("La información del medicamento es obligatoria");
        }

        if (medicamento.getNombre() == null || medicamento.getNombre().isBlank()) {
            throw new BadRequestException("El nombre del medicamento es obligatorio");
        }

        if (medicamento.getCodigo() == null || medicamento.getCodigo().isBlank()) {
            throw new BadRequestException("El código del medicamento es obligatorio");
        }

        if (!CODIGO_PATTERN.matcher(medicamento.getCodigo().trim()).matches()) {
            throw new BadRequestException("El código debe tener exactamente 8 caracteres alfanuméricos");
        }

        if (medicamento.getPrecioCompra() == null || medicamento.getPrecioCompra().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio de compra debe ser mayor que cero");
        }

        if (medicamento.getPrecioVenta() == null || medicamento.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio de venta debe ser mayor que cero");
        }

        if (medicamento.getCantidadDisponible() == null || medicamento.getCantidadDisponible() < 0) {
            throw new BadRequestException("La cantidad disponible no es válida");
        }

        if (medicamento.getStockMinimo() == null || medicamento.getStockMinimo() < 0) {
            throw new BadRequestException("El stock mínimo no es válido");
        }

        if (medicamento.getFechaCompra() == null) {
            throw new BadRequestException("La fecha de compra es obligatoria");
        }

        if (medicamento.getFechaIngresoInventario() == null) {
            throw new BadRequestException("La fecha de ingreso al inventario es obligatoria");
        }

        if (medicamento.getFechaVence() == null) {
            throw new BadRequestException("La fecha de vencimiento es obligatoria");
        }

        if (medicamento.getFechaVence().isBefore(LocalDate.now()) && medicamento.getCantidadDisponible() > 0) {
            throw new BadRequestException("El producto no puede tener fecha de vencimiento vencida");
        }

        if (Boolean.TRUE.equals(medicamento.getRequiereRegistroInvima())
                && (medicamento.getRegistroInvima() == null || medicamento.getRegistroInvima().isBlank())) {
            throw new BadRequestException("El registro Invima es obligatorio para medicamentos con registro sanitario");
        }
    }

    private MiRespuestaRS respuesta(String mensaje) {
        MiRespuestaRS respuesta = new MiRespuestaRS();
        respuesta.setStatus(200);
        respuesta.setMessage(mensaje);
        return respuesta;
    }
}
