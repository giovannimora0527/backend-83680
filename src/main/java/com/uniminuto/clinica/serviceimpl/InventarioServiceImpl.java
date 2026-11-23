package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.CargaInventarioLog;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MedicamentoLote;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.CargaFilaRs;
import com.uniminuto.clinica.models.CargaResultadoRs;
import com.uniminuto.clinica.models.LoteRq;
import com.uniminuto.clinica.models.LoteRs;
import com.uniminuto.clinica.models.MedicamentoRq;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.MovimientoRs;
import com.uniminuto.clinica.repository.CargaInventarioLogRepository;
import com.uniminuto.clinica.repository.MedicamentoLoteRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.security.UsuarioActual;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.util.LectorArchivo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementacion del inventario: medicamentos, lotes, movimientos y carga masiva.
 */
@Service
public class InventarioServiceImpl implements InventarioService {

    /** Dias antes del vencimiento en que un lote se considera "proximo a vencer". */
    public static final int DIAS_POR_VENCER = 30;

    /** Encabezados obligatorios del archivo de carga masiva (en este orden). */
    public static final List<String> ENCABEZADOS = List.of("codigo", "nombre", "tipo", "descripcion", "presentacion",
            "registro_invima", "precio_compra", "precio_venta", "iva_porcentaje", "stock_minimo", "numero_lote",
            "cantidad", "fecha_compra", "fecha_vence");

    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private MedicamentoLoteRepository loteRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    @Autowired
    private CargaInventarioLogRepository cargaLogRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    // ------------------------------------------------------------------ consultas

    @Override
    public List<MedicamentoRs> listarInventario() {
        Map<Long, List<MedicamentoLote>> lotesPorMedicamento = loteRepository.findAll().stream()
                .collect(Collectors.groupingBy(l -> l.getMedicamento().getId()));
        List<MedicamentoRs> respuesta = new ArrayList<>();
        for (Medicamento medicamento : medicamentoRepository.findAllByOrderByNombreAsc()) {
            respuesta.add(this.convertir(medicamento,
                    lotesPorMedicamento.getOrDefault(medicamento.getId(), List.of()), false));
        }
        return respuesta;
    }

    @Override
    public MedicamentoRs consultarMedicamento(Long medicamentoId) {
        Medicamento medicamento = this.buscarMedicamento(medicamentoId);
        return this.convertir(medicamento, loteRepository.findLotesDeMedicamento(medicamentoId), true);
    }

    @Override
    public List<MovimientoRs> listarMovimientos(Long medicamentoId) {
        Medicamento medicamento = this.buscarMedicamento(medicamentoId);
        Map<Long, String> lotes = loteRepository.findLotesDeMedicamento(medicamentoId).stream()
                .collect(Collectors.toMap(MedicamentoLote::getId, MedicamentoLote::getNumeroLote));
        Map<Long, String> usuarios = this.nombresUsuarios();
        return movimientoRepository.findByMedicamentoIdOrderByFechaDescIdDesc(medicamentoId).stream()
                .map(mov -> this.convertirMovimiento(mov, medicamento, lotes, usuarios)).toList();
    }

    @Override
    public int stockDisponible(Long medicamentoId) {
        return loteRepository.findLotesVigentes(medicamentoId, LocalDate.now()).stream()
                .mapToInt(MedicamentoLote::getCantidadDisponible).sum();
    }

    // ------------------------------------------------------------------ medicamentos y lotes

    @Override
    @Transactional
    public MiRespuestaRS guardarMedicamento(MedicamentoRq rq) {
        this.validarMedicamento(rq);
        String codigo = rq.getCodigo().trim().toUpperCase();
        if (medicamentoRepository.findByCodigo(codigo).isPresent()) {
            throw new BadRequestException("Ya existe un producto con el código " + codigo);
        }
        if (medicamentoRepository.findByNombreIgnoreCase(rq.getNombre().trim()).isPresent()) {
            throw new BadRequestException("Ya existe un producto con el nombre " + rq.getNombre().trim());
        }
        Medicamento medicamento = new Medicamento();
        this.copiarDatos(rq, medicamento);
        medicamento.setActivo(true);
        medicamento.setFechaCreacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);

        // Si mandan el primer lote, entra al inventario de una vez
        if (rq.getLote() != null && rq.getLote().getCantidad() != null) {
            rq.getLote().setMedicamentoId(medicamento.getId());
            this.crearLote(medicamento, rq.getLote(), MovimientoInventario.ENTRADA, "Lote inicial");
        }
        return this.exito("Producto registrado correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS actualizarMedicamento(MedicamentoRq rq) {
        this.validarMedicamento(rq);
        Medicamento medicamento = this.buscarMedicamento(rq.getId());
        String codigo = rq.getCodigo().trim().toUpperCase();
        Optional<Medicamento> mismoCodigo = medicamentoRepository.findByCodigo(codigo);
        if (mismoCodigo.isPresent() && !mismoCodigo.get().getId().equals(medicamento.getId())) {
            throw new BadRequestException("Ya existe otro producto con el código " + codigo);
        }
        Optional<Medicamento> mismoNombre = medicamentoRepository.findByNombreIgnoreCase(rq.getNombre().trim());
        if (mismoNombre.isPresent() && !mismoNombre.get().getId().equals(medicamento.getId())) {
            throw new BadRequestException("Ya existe otro producto con ese nombre");
        }
        this.copiarDatos(rq, medicamento);
        if (rq.getActivo() != null) {
            medicamento.setActivo(rq.getActivo());
        }
        medicamento.setFechaModificacionRegistro(LocalDateTime.now());
        medicamentoRepository.save(medicamento);
        return this.exito("Producto actualizado correctamente");
    }

    @Override
    @Transactional
    public MiRespuestaRS agregarLote(LoteRq rq) {
        if (rq == null || rq.getMedicamentoId() == null) {
            throw new BadRequestException("Seleccione el producto");
        }
        Medicamento medicamento = this.buscarMedicamento(rq.getMedicamentoId());
        this.crearLote(medicamento, rq, MovimientoInventario.ENTRADA, "Ingreso de lote");
        return this.exito("Lote ingresado al inventario");
    }

    /**
     * Valida y guarda un lote con su movimiento de entrada.
     *
     * @param medicamento producto.
     * @param rq          datos del lote.
     * @param tipo        tipo de movimiento (ENTRADA o CARGA_MASIVA).
     * @param observacion texto del movimiento.
     */
    private void crearLote(Medicamento medicamento, LoteRq rq, String tipo, String observacion) {
        List<String> errores = this.validarLote(medicamento.getTipo(), rq.getNumeroLote(), rq.getCantidad(),
                rq.getPrecioCompra(), rq.getFechaCompra(), rq.getFechaVence());
        if (!errores.isEmpty()) {
            throw new BadRequestException(String.join(". ", errores));
        }
        if (loteRepository.existsByMedicamentoIdAndNumeroLoteIgnoreCase(medicamento.getId(), rq.getNumeroLote().trim())) {
            throw new BadRequestException("El lote " + rq.getNumeroLote().trim() + " ya existe para este producto");
        }
        MedicamentoLote lote = new MedicamentoLote();
        lote.setMedicamento(medicamento);
        lote.setNumeroLote(rq.getNumeroLote().trim().toUpperCase());
        lote.setFechaCompra(rq.getFechaCompra());
        lote.setFechaIngreso(LocalDateTime.now());
        lote.setFechaVence(rq.getFechaVence());
        lote.setPrecioCompra(rq.getPrecioCompra());
        lote.setCantidadInicial(rq.getCantidad());
        lote.setCantidadDisponible(rq.getCantidad());
        loteRepository.save(lote);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setMedicamentoId(medicamento.getId());
        mov.setLoteId(lote.getId());
        mov.setTipo(tipo);
        mov.setCantidad(rq.getCantidad());
        mov.setSaldoLote(rq.getCantidad());
        mov.setFecha(LocalDateTime.now());
        mov.setUsuarioId(UsuarioActual.get().getUsuarioId());
        mov.setObservacion(observacion);
        movimientoRepository.save(mov);
    }

    // ------------------------------------------------------------------ carga masiva

    @Override
    public CargaResultadoRs previsualizarCarga(MultipartFile archivo) {
        return this.validarArchivo(archivo);
    }

    @Override
    public CargaResultadoRs confirmarCarga(MultipartFile archivo) {
        CargaResultadoRs resultado = this.validarArchivo(archivo);
        Long usuarioId = UsuarioActual.get().getUsuarioId();
        TransactionTemplate transaccion = new TransactionTemplate(transactionManager);
        transaccion.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        int importadas = 0;
        for (CargaFilaRs fila : resultado.getFilas()) {
            if (!fila.getValida()) {
                continue;
            }
            // Cada fila va en su propia transaccion: si una falla, las demas no se afectan
            try {
                transaccion.executeWithoutResult(estado -> this.importarFila(fila, usuarioId));
                importadas++;
            } catch (RuntimeException e) {
                fila.setValida(false);
                fila.getErrores().add("No se pudo guardar: " + e.getMessage());
            }
        }
        resultado.setFilasImportadas(importadas);
        resultado.setFilasValidas(importadas);
        resultado.setFilasError(resultado.getTotalFilas() - importadas);

        // Log de cargue con el detalle de las filas rechazadas
        CargaInventarioLog log = new CargaInventarioLog();
        log.setUsuarioId(usuarioId);
        log.setFecha(LocalDateTime.now());
        log.setNombreArchivo(resultado.getNombreArchivo());
        log.setTotalFilas(resultado.getTotalFilas());
        log.setFilasOk(importadas);
        log.setFilasError(resultado.getFilasError());
        log.setDetalle(resultado.getFilas().stream().filter(f -> !f.getValida())
                .map(f -> "Fila " + f.getFila() + ": " + String.join("; ", f.getErrores()))
                .collect(Collectors.joining("\n")));
        cargaLogRepository.save(log);
        resultado.setLogId(log.getId());
        return resultado;
    }

    @Override
    public List<CargaInventarioLog> listarCargas() {
        return cargaLogRepository.findAllByOrderByFechaDesc();
    }

    @Override
    public byte[] plantillaCarga() {
        String contenido = String.join(",", ENCABEZADOS) + "\n"
                + "AMOX0500,Amoxicilina 500mg,MEDICAMENTO,Antibiótico,Tabletas x 10,INVIMA2023M-000111,"
                + "5000,9000,0,20,LT-001,100,2026-11-01,2028-11-01\n"
                + "GUANT001,Guantes de látex,UTENSILIO,Caja x 100,Caja,,15000,22000,19,5,GT-01,30,2026-11-01,\n";
        return ("﻿" + contenido).getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Lee el archivo y valida cada fila contra la base y contra las demas filas.
     *
     * @param archivo archivo subido.
     * @return filas validadas.
     */
    private CargaResultadoRs validarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BadRequestException("Seleccione un archivo .csv o .xlsx");
        }
        List<Map<String, String>> registros = LectorArchivo.leer(archivo, ENCABEZADOS);
        if (registros.isEmpty()) {
            throw new BadRequestException("El archivo no tiene registros");
        }
        Set<String> lotesEnArchivo = new HashSet<>();
        Map<String, String> nombrePorCodigo = new HashMap<>();
        List<CargaFilaRs> filas = new ArrayList<>();
        int numeroFila = 1;
        for (Map<String, String> registro : registros) {
            numeroFila++;
            CargaFilaRs fila = this.armarFila(registro, numeroFila);
            this.validarFila(fila, lotesEnArchivo, nombrePorCodigo);
            fila.setValida(fila.getErrores().isEmpty());
            filas.add(fila);
        }
        CargaResultadoRs resultado = new CargaResultadoRs();
        resultado.setNombreArchivo(archivo.getOriginalFilename());
        resultado.setFilas(filas);
        resultado.setTotalFilas(filas.size());
        resultado.setFilasValidas((int) filas.stream().filter(CargaFilaRs::getValida).count());
        resultado.setFilasError(resultado.getTotalFilas() - resultado.getFilasValidas());
        return resultado;
    }

    /**
     * Pasa un registro del archivo a la fila de respuesta.
     *
     * @param r      valores por encabezado.
     * @param numero numero de fila.
     * @return fila.
     */
    private CargaFilaRs armarFila(Map<String, String> r, int numero) {
        CargaFilaRs fila = new CargaFilaRs();
        fila.setFila(numero);
        fila.setCodigo(r.get("codigo").toUpperCase());
        fila.setNombre(r.get("nombre"));
        fila.setTipo(r.get("tipo").toUpperCase());
        fila.setDescripcion(r.get("descripcion"));
        fila.setPresentacion(r.get("presentacion"));
        fila.setRegistroInvima(r.get("registro_invima"));
        fila.setPrecioCompra(r.get("precio_compra"));
        fila.setPrecioVenta(r.get("precio_venta"));
        fila.setIvaPorcentaje(r.get("iva_porcentaje"));
        fila.setStockMinimo(r.get("stock_minimo"));
        fila.setNumeroLote(r.get("numero_lote").toUpperCase());
        fila.setCantidad(r.get("cantidad"));
        fila.setFechaCompra(r.get("fecha_compra"));
        fila.setFechaVence(r.get("fecha_vence"));
        return fila;
    }

    /**
     * Reglas de la carga: campos obligatorios, formatos, duplicados y coherencia con la base.
     *
     * @param fila            fila a validar (los errores se agregan en ella).
     * @param lotesEnArchivo  codigo+lote ya vistos en el archivo.
     * @param nombrePorCodigo nombre usado para cada codigo dentro del archivo.
     */
    private void validarFila(CargaFilaRs fila, Set<String> lotesEnArchivo, Map<String, String> nombrePorCodigo) {
        List<String> errores = fila.getErrores();
        if (!fila.getCodigo().matches("[A-Z0-9]{8}")) {
            errores.add("El código debe tener exactamente 8 caracteres alfanuméricos");
        }
        if (fila.getNombre().isBlank()) {
            errores.add("El nombre es obligatorio");
        }
        if (!Medicamento.TIPO_MEDICAMENTO.equals(fila.getTipo()) && !Medicamento.TIPO_UTENSILIO.equals(fila.getTipo())) {
            errores.add("El tipo debe ser MEDICAMENTO o UTENSILIO");
        }
        if (Medicamento.TIPO_MEDICAMENTO.equals(fila.getTipo()) && fila.getRegistroInvima().isBlank()) {
            errores.add("El registro Invima es obligatorio para medicamentos");
        }
        BigDecimal precioCompra = this.decimal(fila.getPrecioCompra(), "precio_compra", errores, true);
        BigDecimal precioVenta = this.decimal(fila.getPrecioVenta(), "precio_venta", errores, true);
        BigDecimal iva = this.decimal(fila.getIvaPorcentaje().isBlank() ? "0" : fila.getIvaPorcentaje(), "iva_porcentaje", errores, false);
        if (iva != null && iva.compareTo(BigDecimal.valueOf(100)) > 0) {
            errores.add("El IVA no puede ser mayor a 100");
        }
        if (precioCompra != null && precioVenta != null && precioVenta.compareTo(precioCompra) < 0) {
            errores.add("El precio de venta no puede ser menor al de compra");
        }
        this.entero(fila.getStockMinimo().isBlank() ? "0" : fila.getStockMinimo(), "stock_minimo", errores, false);
        Integer cantidad = this.entero(fila.getCantidad(), "cantidad", errores, true);
        LocalDate fechaCompra = this.fecha(fila.getFechaCompra(), "fecha_compra", errores, true);
        LocalDate fechaVence = this.fecha(fila.getFechaVence(), "fecha_vence",
                errores, Medicamento.TIPO_MEDICAMENTO.equals(fila.getTipo()));
        errores.addAll(this.validarLote(fila.getTipo(), fila.getNumeroLote(), cantidad, precioCompra, fechaCompra, fechaVence)
                .stream().filter(e -> !errores.contains(e) && !e.contains("obligatori")).toList());
        if (fila.getNumeroLote().isBlank()) {
            errores.add("El número de lote es obligatorio");
        }

        // Duplicados dentro del mismo archivo
        String llave = fila.getCodigo() + "|" + fila.getNumeroLote();
        if (!lotesEnArchivo.add(llave)) {
            errores.add("El lote " + fila.getNumeroLote() + " del código " + fila.getCodigo() + " está repetido en el archivo");
        }
        String nombreAnterior = nombrePorCodigo.putIfAbsent(fila.getCodigo(), fila.getNombre().toLowerCase());
        if (nombreAnterior != null && !nombreAnterior.equals(fila.getNombre().toLowerCase())) {
            errores.add("El código " + fila.getCodigo() + " aparece con otro nombre en el archivo");
        }

        // Coherencia con lo que ya existe en la base
        Optional<Medicamento> existente = medicamentoRepository.findByCodigo(fila.getCodigo());
        fila.setProductoNuevo(existente.isEmpty());
        if (existente.isPresent()) {
            if (!existente.get().getNombre().equalsIgnoreCase(fila.getNombre().trim())) {
                errores.add("El código ya está registrado con el nombre \"" + existente.get().getNombre() + "\"");
            } else if (loteRepository.existsByMedicamentoIdAndNumeroLoteIgnoreCase(existente.get().getId(), fila.getNumeroLote())) {
                errores.add("El lote " + fila.getNumeroLote() + " ya existe en el inventario (registro duplicado)");
            }
        } else if (!fila.getNombre().isBlank() && medicamentoRepository.findByNombreIgnoreCase(fila.getNombre().trim()).isPresent()) {
            errores.add("Ya existe un producto con ese nombre pero con otro código");
        }
    }

    /**
     * Guarda una fila valida: crea el producto si no existe y agrega el lote.
     *
     * @param fila      fila validada.
     * @param usuarioId usuario que hace la carga.
     */
    private void importarFila(CargaFilaRs fila, Long usuarioId) {
        Medicamento medicamento = medicamentoRepository.findByCodigo(fila.getCodigo()).orElseGet(() -> {
            Medicamento nuevo = new Medicamento();
            nuevo.setCodigo(fila.getCodigo());
            nuevo.setNombre(fila.getNombre().trim());
            nuevo.setDescripcion(fila.getDescripcion());
            nuevo.setPresentacion(fila.getPresentacion());
            nuevo.setTipo(fila.getTipo());
            nuevo.setRegistroInvima(fila.getRegistroInvima().isBlank() ? null : fila.getRegistroInvima());
            nuevo.setPrecioVenta(new BigDecimal(fila.getPrecioVenta().replace(",", ".")));
            nuevo.setIvaPorcentaje(new BigDecimal(fila.getIvaPorcentaje().isBlank() ? "0" : fila.getIvaPorcentaje().replace(",", ".")));
            nuevo.setStockMinimo(Integer.parseInt(fila.getStockMinimo().isBlank() ? "0" : fila.getStockMinimo()));
            nuevo.setActivo(true);
            nuevo.setFechaCreacionRegistro(LocalDateTime.now());
            return medicamentoRepository.save(nuevo);
        });
        MedicamentoLote lote = new MedicamentoLote();
        lote.setMedicamento(medicamento);
        lote.setNumeroLote(fila.getNumeroLote());
        lote.setFechaCompra(LocalDate.parse(fila.getFechaCompra()));
        lote.setFechaIngreso(LocalDateTime.now());
        lote.setFechaVence(fila.getFechaVence().isBlank() ? null : LocalDate.parse(fila.getFechaVence()));
        lote.setPrecioCompra(new BigDecimal(fila.getPrecioCompra().replace(",", ".")));
        lote.setCantidadInicial(Integer.parseInt(fila.getCantidad()));
        lote.setCantidadDisponible(lote.getCantidadInicial());
        loteRepository.save(lote);

        MovimientoInventario mov = new MovimientoInventario();
        mov.setMedicamentoId(medicamento.getId());
        mov.setLoteId(lote.getId());
        mov.setTipo(MovimientoInventario.CARGA_MASIVA);
        mov.setCantidad(lote.getCantidadInicial());
        mov.setSaldoLote(lote.getCantidadDisponible());
        mov.setFecha(LocalDateTime.now());
        mov.setUsuarioId(usuarioId);
        mov.setObservacion("Carga masiva - fila " + fila.getFila());
        movimientoRepository.save(mov);
    }

    // ------------------------------------------------------------------ validaciones y conversiones

    /**
     * Valida los datos del producto.
     *
     * @param rq datos.
     */
    private void validarMedicamento(MedicamentoRq rq) {
        if (rq == null) {
            throw new BadRequestException("Los datos del producto son obligatorios");
        }
        if (rq.getCodigo() == null || !rq.getCodigo().trim().toUpperCase().matches("[A-Z0-9]{8}")) {
            throw new BadRequestException("El código debe tener exactamente 8 caracteres alfanuméricos");
        }
        if (rq.getNombre() == null || rq.getNombre().isBlank()) {
            throw new BadRequestException("El nombre es obligatorio");
        }
        String tipo = rq.getTipo() == null ? "" : rq.getTipo().toUpperCase();
        if (!Medicamento.TIPO_MEDICAMENTO.equals(tipo) && !Medicamento.TIPO_UTENSILIO.equals(tipo)) {
            throw new BadRequestException("El tipo debe ser MEDICAMENTO o UTENSILIO");
        }
        if (Medicamento.TIPO_MEDICAMENTO.equals(tipo) && (rq.getRegistroInvima() == null || rq.getRegistroInvima().isBlank())) {
            throw new BadRequestException("El registro Invima es obligatorio para medicamentos");
        }
        if (rq.getPrecioVenta() == null || rq.getPrecioVenta().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("El precio de venta debe ser mayor a cero");
        }
        if (rq.getIvaPorcentaje() != null && (rq.getIvaPorcentaje().signum() < 0
                || rq.getIvaPorcentaje().compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new BadRequestException("El IVA debe estar entre 0 y 100");
        }
        if (rq.getStockMinimo() == null || rq.getStockMinimo() < 0) {
            throw new BadRequestException("El stock mínimo debe ser cero o mayor");
        }
    }

    /**
     * Reglas del lote: cantidad positiva, precio valido y fechas coherentes.
     *
     * @return lista de errores (vacia si esta bien).
     */
    private List<String> validarLote(String tipo, String numeroLote, Integer cantidad, BigDecimal precioCompra,
                                     LocalDate fechaCompra, LocalDate fechaVence) {
        List<String> errores = new ArrayList<>();
        if (numeroLote == null || numeroLote.isBlank()) {
            errores.add("El número de lote es obligatorio");
        }
        if (cantidad == null || cantidad <= 0) {
            errores.add("La cantidad debe ser mayor a cero");
        }
        if (precioCompra == null || precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El precio de compra debe ser mayor a cero");
        }
        if (fechaCompra == null) {
            errores.add("La fecha de compra es obligatoria");
        } else if (fechaCompra.isAfter(LocalDate.now())) {
            errores.add("La fecha de compra no puede ser futura");
        }
        if (Medicamento.TIPO_MEDICAMENTO.equals(tipo) && fechaVence == null) {
            errores.add("La fecha de vencimiento es obligatoria para medicamentos");
        }
        if (fechaVence != null && fechaCompra != null && !fechaVence.isAfter(fechaCompra)) {
            errores.add("La fecha de vencimiento debe ser posterior a la de compra");
        }
        if (fechaVence != null && fechaVence.isBefore(LocalDate.now())) {
            errores.add("No se puede ingresar un lote ya vencido");
        }
        return errores;
    }

    /** Convierte texto a decimal y registra el error si no es valido. */
    private BigDecimal decimal(String valor, String campo, List<String> errores, boolean obligatorio) {
        if (valor == null || valor.isBlank()) {
            if (obligatorio) {
                errores.add("El campo " + campo + " es obligatorio");
            }
            return null;
        }
        try {
            BigDecimal numero = new BigDecimal(valor.replace(",", "."));
            if (numero.signum() < 0) {
                errores.add("El campo " + campo + " no puede ser negativo");
            }
            return numero;
        } catch (NumberFormatException e) {
            errores.add("El campo " + campo + " debe ser numérico");
            return null;
        }
    }

    /** Convierte texto a entero y registra el error si no es valido. */
    private Integer entero(String valor, String campo, List<String> errores, boolean obligatorio) {
        if (valor == null || valor.isBlank()) {
            if (obligatorio) {
                errores.add("El campo " + campo + " es obligatorio");
            }
            return null;
        }
        try {
            int numero = Integer.parseInt(valor.trim());
            if (numero < 0) {
                errores.add("El campo " + campo + " no puede ser negativo");
            }
            return numero;
        } catch (NumberFormatException e) {
            errores.add("El campo " + campo + " debe ser un número entero");
            return null;
        }
    }

    /** Convierte texto yyyy-MM-dd a fecha y registra el error si no es valido. */
    private LocalDate fecha(String valor, String campo, List<String> errores, boolean obligatorio) {
        if (valor == null || valor.isBlank()) {
            if (obligatorio) {
                errores.add("El campo " + campo + " es obligatorio");
            }
            return null;
        }
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException e) {
            errores.add("El campo " + campo + " debe tener formato AAAA-MM-DD");
            return null;
        }
    }

    /**
     * Copia los datos editables del request a la entidad.
     */
    private void copiarDatos(MedicamentoRq rq, Medicamento medicamento) {
        medicamento.setCodigo(rq.getCodigo().trim().toUpperCase());
        medicamento.setNombre(rq.getNombre().trim());
        medicamento.setDescripcion(rq.getDescripcion());
        medicamento.setPresentacion(rq.getPresentacion());
        medicamento.setTipo(rq.getTipo().toUpperCase());
        medicamento.setRegistroInvima(rq.getRegistroInvima() == null || rq.getRegistroInvima().isBlank()
                ? null : rq.getRegistroInvima().trim());
        medicamento.setPrecioVenta(rq.getPrecioVenta());
        medicamento.setIvaPorcentaje(rq.getIvaPorcentaje() == null ? BigDecimal.ZERO : rq.getIvaPorcentaje());
        medicamento.setStockMinimo(rq.getStockMinimo());
        medicamento.setFechaProximoPedido(rq.getFechaProximoPedido());
    }

    /**
     * Arma la respuesta con existencias, precio de compra, vencimientos y alertas.
     */
    private MedicamentoRs convertir(Medicamento m, List<MedicamentoLote> lotes, boolean conLotes) {
        MedicamentoRs rs = new MedicamentoRs();
        rs.setId(m.getId());
        rs.setCodigo(m.getCodigo());
        rs.setNombre(m.getNombre());
        rs.setDescripcion(m.getDescripcion());
        rs.setPresentacion(m.getPresentacion());
        rs.setTipo(m.getTipo());
        rs.setRegistroInvima(m.getRegistroInvima());
        rs.setPrecioVenta(m.getPrecioVenta());
        rs.setIvaPorcentaje(m.getIvaPorcentaje());
        rs.setStockMinimo(m.getStockMinimo());
        rs.setFechaProximoPedido(m.getFechaProximoPedido());
        rs.setActivo(m.getActivo());

        LocalDate hoy = LocalDate.now();
        int total = lotes.stream().mapToInt(MedicamentoLote::getCantidadDisponible).sum();
        int disponible = lotes.stream().filter(l -> !l.estaVencido()).mapToInt(MedicamentoLote::getCantidadDisponible).sum();
        rs.setStockTotal(total);
        rs.setStockDisponible(disponible);
        lotes.stream().max(Comparator.comparing(MedicamentoLote::getFechaIngreso))
                .ifPresent(l -> rs.setPrecioCompra(l.getPrecioCompra()));
        lotes.stream().filter(l -> l.getCantidadDisponible() > 0 && !l.estaVencido() && l.getFechaVence() != null)
                .map(MedicamentoLote::getFechaVence).min(LocalDate::compareTo).ifPresent(rs::setProximoVencimiento);

        if (disponible == 0) {
            rs.getAlertas().add("AGOTADO");
        } else if (disponible <= m.getStockMinimo()) {
            rs.getAlertas().add("STOCK_BAJO");
        }
        if (rs.getProximoVencimiento() != null && !rs.getProximoVencimiento().isAfter(hoy.plusDays(DIAS_POR_VENCER))) {
            rs.getAlertas().add("POR_VENCER");
        }
        if (lotes.stream().anyMatch(l -> l.estaVencido() && l.getCantidadDisponible() > 0)) {
            rs.getAlertas().add("VENCIDO");
        }
        if (conLotes) {
            rs.setLotes(lotes.stream().map(this::convertirLote).toList());
        }
        return rs;
    }

    @Override
    public LoteRs convertirLote(MedicamentoLote l) {
        LoteRs rs = new LoteRs();
        rs.setId(l.getId());
        rs.setNumeroLote(l.getNumeroLote());
        rs.setFechaCompra(l.getFechaCompra());
        rs.setFechaIngreso(l.getFechaIngreso());
        rs.setFechaVence(l.getFechaVence());
        rs.setPrecioCompra(l.getPrecioCompra());
        rs.setCantidadInicial(l.getCantidadInicial());
        rs.setCantidadDisponible(l.getCantidadDisponible());
        if (l.estaVencido()) {
            rs.setEstado("VENCIDO");
        } else if (l.getCantidadDisponible() == 0) {
            rs.setEstado("AGOTADO");
        } else if (l.getFechaVence() != null && !l.getFechaVence().isAfter(LocalDate.now().plusDays(DIAS_POR_VENCER))) {
            rs.setEstado("POR_VENCER");
        } else {
            rs.setEstado("VIGENTE");
        }
        return rs;
    }

    /**
     * Convierte un movimiento para mostrar.
     */
    private MovimientoRs convertirMovimiento(MovimientoInventario mov, Medicamento m, Map<Long, String> lotes,
                                             Map<Long, String> usuarios) {
        MovimientoRs rs = new MovimientoRs();
        rs.setId(mov.getId());
        rs.setFecha(mov.getFecha());
        rs.setTipo(mov.getTipo());
        rs.setMedicamentoId(m.getId());
        rs.setMedicamentoCodigo(m.getCodigo());
        rs.setMedicamentoNombre(m.getNombre());
        rs.setNumeroLote(lotes.get(mov.getLoteId()));
        rs.setCantidad(mov.getCantidad());
        rs.setSaldoLote(mov.getSaldoLote());
        rs.setUsuario(usuarios.get(mov.getUsuarioId()));
        rs.setFormulaId(mov.getFormulaId());
        rs.setDespachoId(mov.getDespachoId());
        rs.setObservacion(mov.getObservacion());
        return rs;
    }

    /**
     * Mapa id -> nombre de usuario.
     */
    private Map<Long, String> nombresUsuarios() {
        return usuarioRepository.findAll().stream().collect(Collectors.toMap(Usuario::getId, Usuario::getUsername));
    }

    /**
     * Busca un medicamento o lanza 404.
     */
    private Medicamento buscarMedicamento(Long id) {
        if (id == null) {
            throw new BadRequestException("El id del producto es obligatorio");
        }
        return medicamentoRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Producto no encontrado",
                        org.springframework.http.HttpStatus.NOT_FOUND));
    }

    /**
     * Respuesta de exito.
     */
    private MiRespuestaRS exito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
