package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FacturaDetalle;
import com.uniminuto.clinica.entity.HistoriaMedica;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MedicamentoLote;
import com.uniminuto.clinica.entity.MovimientoInventario;
import com.uniminuto.clinica.entity.Pago;
import com.uniminuto.clinica.entity.Usuario;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MedicamentoRs;
import com.uniminuto.clinica.models.ReporteRs;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.HistoriaMedicaRepository;
import com.uniminuto.clinica.repository.MedicamentoLoteRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoInventarioRepository;
import com.uniminuto.clinica.repository.UsuarioRepository;
import com.uniminuto.clinica.service.InventarioService;
import com.uniminuto.clinica.service.ReporteService;
import com.uniminuto.clinica.util.ExcelUtil;
import com.uniminuto.clinica.util.PdfUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementacion de los reportes. Todos salen de las facturas, pagos, movimientos
 * y citas guardados, para que sean coherentes con la operacion.
 */
@Service
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FH = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private CitaRepository citaRepository;
    @Autowired
    private HistoriaMedicaRepository historiaRepository;
    @Autowired
    private MedicamentoRepository medicamentoRepository;
    @Autowired
    private MedicamentoLoteRepository loteRepository;
    @Autowired
    private MovimientoInventarioRepository movimientoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private InventarioService inventarioService;

    @Override
    public List<Map<String, String>> tipos() {
        List<Map<String, String>> tipos = new ArrayList<>();
        String[][] datos = {
                {"ingresos-periodo", "Ingresos por período (día, mes o año)", "Financiero"},
                {"ingresos-consultas", "Ingresos por consultas y servicios", "Financiero"},
                {"ingresos-especialidad", "Ingresos por especialidad", "Financiero"},
                {"ingresos-veterinario", "Ingresos por veterinario", "Financiero"},
                {"ingresos-medicamentos", "Ingresos por venta de medicamentos", "Financiero"},
                {"facturas-estado", "Facturas generadas, pagadas, pendientes y parciales", "Financiero"},
                {"impuestos-descuentos", "Impuestos y descuentos por factura", "Financiero"},
                {"inventario-existencias", "Existencias y valor del inventario (costo y venta)", "Inventario"},
                {"inventario-alertas", "Agotados, stock mínimo y próximos a vencer", "Inventario"},
                {"inventario-vencimientos", "Lotes vencidos y próximos a vencer", "Inventario"},
                {"inventario-movimientos", "Entradas y salidas de inventario", "Inventario"},
                {"citas-estado", "Citas por estado", "Gestión médica"},
                {"citas-detalle", "Detalle de citas del período", "Gestión médica"},
                {"consultas-veterinario", "Consultas realizadas por veterinario", "Gestión médica"},
                {"consultas-especialidad", "Consultas realizadas por especialidad", "Gestión médica"},
                {"mascotas-atendidas", "Mascotas atendidas en el período", "Gestión médica"}};
        for (String[] d : datos) {
            Map<String, String> m = new LinkedHashMap<>();
            m.put("codigo", d[0]);
            m.put("nombre", d[1]);
            m.put("categoria", d[2]);
            tipos.add(m);
        }
        return tipos;
    }

    @Override
    public ReporteRs generar(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        LocalDate inicio = desde == null ? LocalDate.now().withDayOfMonth(1) : desde;
        LocalDate fin = hasta == null ? LocalDate.now() : hasta;
        if (inicio.isAfter(fin)) {
            throw new BadRequestException("La fecha inicial no puede ser mayor que la final");
        }
        ReporteRs rs = new ReporteRs();
        rs.setTipo(tipo);
        rs.setTitulo(this.tipos().stream().filter(t -> t.get("codigo").equals(tipo)).findFirst()
                .map(t -> t.get("nombre")).orElseThrow(() -> new BadRequestException("Tipo de reporte no válido")));
        rs.setSubtitulo("Desde " + inicio + " hasta " + fin);
        LocalDateTime ini = inicio.atStartOfDay();
        LocalDateTime finDia = fin.atTime(23, 59, 59);
        switch (tipo) {
            case "ingresos-periodo" -> this.ingresosPeriodo(rs, ini, finDia, agrupacion);
            case "ingresos-consultas" -> this.ingresosPorDetalle(rs, ini, finDia, FacturaDetalle.SERVICIO, "Servicio");
            case "ingresos-medicamentos" -> this.ingresosPorDetalle(rs, ini, finDia, FacturaDetalle.MEDICAMENTO, "Medicamento");
            case "ingresos-especialidad" -> this.ingresosAgrupados(rs, ini, finDia, "Especialidad",
                    f -> f.getEspecialidad() == null ? "Sin especialidad" : f.getEspecialidad());
            case "ingresos-veterinario" -> this.ingresosAgrupados(rs, ini, finDia, "Veterinario",
                    f -> f.getMedico().getNombres() + " " + f.getMedico().getApellidos());
            case "facturas-estado" -> this.facturasPorEstado(rs, ini, finDia);
            case "impuestos-descuentos" -> this.impuestosDescuentos(rs, ini, finDia);
            case "inventario-existencias" -> this.existencias(rs);
            case "inventario-alertas" -> this.alertas(rs, filtro);
            case "inventario-vencimientos" -> this.vencimientos(rs);
            case "inventario-movimientos" -> this.movimientos(rs, ini, finDia, filtro);
            case "citas-estado" -> this.citasPorEstado(rs, ini, finDia);
            case "citas-detalle" -> this.citasDetalle(rs, ini, finDia, filtro);
            case "consultas-veterinario" -> this.consultasAgrupadas(rs, ini, finDia, "Veterinario",
                    h -> h.getMedico() == null ? "Sin médico" : h.getMedico().getNombres() + " " + h.getMedico().getApellidos());
            case "consultas-especialidad" -> this.consultasAgrupadas(rs, ini, finDia, "Especialidad",
                    h -> h.getMedico() == null || h.getMedico().getEspecializacion() == null ? "Sin especialidad"
                            : h.getMedico().getEspecializacion().getNombre());
            case "mascotas-atendidas" -> this.mascotasAtendidas(rs, ini, finDia);
            default -> throw new BadRequestException("Tipo de reporte no válido");
        }
        if (tipo.startsWith("inventario-") && !tipo.equals("inventario-movimientos")) {
            rs.setSubtitulo("Corte al " + LocalDate.now());
        }
        if (agrupacion != null && tipo.equals("ingresos-periodo")) {
            rs.setSubtitulo(rs.getSubtitulo() + "  ·  Agrupado por " + agrupacion.toLowerCase());
        }
        if (filtro != null && !filtro.isBlank()) {
            rs.setSubtitulo(rs.getSubtitulo() + "  ·  Filtro: " + filtro);
        }
        return rs;
    }

    @Override
    public byte[] excel(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        ReporteRs rs = this.generar(tipo, desde, hasta, agrupacion, filtro);
        return ExcelUtil.tabla(rs.getTitulo(), rs.getSubtitulo(), rs.getColumnas(), rs.getFilas(),
                rs.getTotales().isEmpty() ? null : rs.getTotales());
    }

    @Override
    public byte[] pdf(String tipo, LocalDate desde, LocalDate hasta, String agrupacion, String filtro) {
        ReporteRs rs = this.generar(tipo, desde, hasta, agrupacion, filtro);
        List<List<String>> filas = rs.getFilas().stream().map(f -> f.stream().map(this::texto).toList()).toList();
        PdfUtil.Documento doc = new PdfUtil.Documento(rs.getTitulo());
        if (rs.getColumnas().size() > 6) {
            doc.horizontal();
        }
        doc.texto(null, rs.getSubtitulo()).tabla(null, rs.getColumnas(), filas);
        if (!rs.getTotales().isEmpty()) {
            List<String> totales = rs.getTotales().stream().map(this::texto).toList();
            doc.tabla("Totales", rs.getColumnas().subList(0, totales.size()), List.of(totales));
        }
        return doc.generar();
    }

    // ------------------------------------------------------------------ financieros

    private List<Factura> facturasValidas(LocalDateTime ini, LocalDateTime fin) {
        return facturaRepository.findByFechaBetweenOrderByFechaAsc(ini, fin).stream()
                .filter(f -> !Factura.ANULADA.equals(f.getEstado())).toList();
    }

    private void ingresosPeriodo(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String agrupacion) {
        String formato = switch (agrupacion == null ? "DIA" : agrupacion.toUpperCase()) {
            case "MES" -> "yyyy-MM";
            case "ANIO" -> "yyyy";
            default -> "yyyy-MM-dd";
        };
        DateTimeFormatter f = DateTimeFormatter.ofPattern(formato);
        Map<String, BigDecimal[]> grupos = new TreeMap<>();
        for (Factura fa : this.facturasValidas(ini, fin)) {
            BigDecimal[] g = grupos.computeIfAbsent(fa.getFecha().format(f), k -> this.ceros(3));
            g[0] = g[0].add(BigDecimal.ONE);
            g[1] = g[1].add(fa.getTotal());
        }
        for (Pago p : facturaRepository.findPagosEntre(ini, fin)) {
            grupos.computeIfAbsent(p.getFecha().format(f), k -> this.ceros(3))[2] =
                    grupos.get(p.getFecha().format(f))[2].add(p.getValor());
        }
        rs.setColumnas(List.of("Período", "Facturas generadas", "Total facturado", "Pagos recibidos (ingresos)"));
        BigDecimal[] t = this.ceros(3);
        grupos.forEach((periodo, g) -> {
            rs.getFilas().add(List.of(periodo, g[0].intValue(), g[1], g[2]));
            this.sumar(t, g);
        });
        rs.setTotales(List.of("TOTAL", t[0].intValue(), t[1], t[2]));
    }

    private void ingresosPorDetalle(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String tipoLinea, String etiqueta) {
        List<Long> ids = this.facturasValidas(ini, fin).stream().map(Factura::getId).toList();
        Map<String, BigDecimal[]> grupos = new TreeMap<>();
        if (!ids.isEmpty()) {
            for (FacturaDetalle d : facturaRepository.findDetallesDe(ids)) {
                if (!tipoLinea.equals(d.getTipo())) {
                    continue;
                }
                BigDecimal[] g = grupos.computeIfAbsent(d.getDescripcion(), k -> this.ceros(4));
                this.sumar(g, new BigDecimal[]{BigDecimal.valueOf(d.getCantidad()), d.getSubtotal(), d.getIvaValor(), d.getTotal()});
            }
        }
        rs.setColumnas(List.of(etiqueta, "Cantidad", "Subtotal", "IVA", "Total"));
        BigDecimal[] t = this.ceros(4);
        grupos.forEach((nombre, g) -> {
            rs.getFilas().add(List.of(nombre, g[0].intValue(), g[1], g[2], g[3]));
            this.sumar(t, g);
        });
        rs.setTotales(List.of("TOTAL", t[0].intValue(), t[1], t[2], t[3]));
    }

    private void ingresosAgrupados(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String etiqueta,
                                   Function<Factura, String> clave) {
        Map<String, BigDecimal[]> grupos = new TreeMap<>();
        for (Factura f : this.facturasValidas(ini, fin)) {
            this.sumar(grupos.computeIfAbsent(clave.apply(f), k -> this.ceros(6)), new BigDecimal[]{BigDecimal.ONE,
                    f.getSubtotal(), f.getIva(), f.getDescuento(), f.getTotal(), f.getTotalPagado()});
        }
        rs.setColumnas(List.of(etiqueta, "Facturas", "Subtotal", "IVA", "Descuentos", "Total facturado", "Pagado"));
        BigDecimal[] t = this.ceros(6);
        grupos.forEach((nombre, g) -> {
            rs.getFilas().add(List.of(nombre, g[0].intValue(), g[1], g[2], g[3], g[4], g[5]));
            this.sumar(t, g);
        });
        rs.setTotales(List.of("TOTAL", t[0].intValue(), t[1], t[2], t[3], t[4], t[5]));
    }

    private void facturasPorEstado(ReporteRs rs, LocalDateTime ini, LocalDateTime fin) {
        Map<String, BigDecimal[]> grupos = new LinkedHashMap<>();
        List.of(Factura.PENDIENTE, Factura.PAGO_PARCIAL, Factura.PAGADA, Factura.ANULADA).forEach(e -> grupos.put(e, this.ceros(4)));
        for (Factura f : facturaRepository.findByFechaBetweenOrderByFechaAsc(ini, fin)) {
            BigDecimal saldo = Factura.ANULADA.equals(f.getEstado()) ? BigDecimal.ZERO : f.getTotal().subtract(f.getTotalPagado());
            this.sumar(grupos.get(f.getEstado()), new BigDecimal[]{BigDecimal.ONE, f.getTotal(), f.getTotalPagado(), saldo});
        }
        rs.setColumnas(List.of("Estado", "Cantidad", "Total", "Pagado", "Saldo pendiente"));
        BigDecimal[] t = this.ceros(4);
        grupos.forEach((estado, g) -> {
            rs.getFilas().add(List.of(estado, g[0].intValue(), g[1], g[2], g[3]));
            this.sumar(t, g);
        });
        rs.setTotales(List.of("TOTAL FACTURAS GENERADAS", t[0].intValue(), t[1], t[2], t[3]));
    }

    private void impuestosDescuentos(ReporteRs rs, LocalDateTime ini, LocalDateTime fin) {
        rs.setColumnas(List.of("Número", "Fecha", "Propietario", "Subtotal", "IVA", "Descuento", "Total", "Estado"));
        BigDecimal[] t = this.ceros(4);
        for (Factura f : this.facturasValidas(ini, fin)) {
            rs.getFilas().add(List.of(f.getNumero(), f.getFecha().format(FH),
                    f.getCliente().getNombres() + " " + f.getCliente().getApellidos(),
                    f.getSubtotal(), f.getIva(), f.getDescuento(), f.getTotal(), f.getEstado()));
            this.sumar(t, new BigDecimal[]{f.getSubtotal(), f.getIva(), f.getDescuento(), f.getTotal()});
        }
        rs.setTotales(List.of("TOTAL", "", "", t[0], t[1], t[2], t[3]));
    }

    // ------------------------------------------------------------------ inventario

    private void existencias(ReporteRs rs) {
        Map<Long, List<MedicamentoLote>> lotes = loteRepository.findAll().stream()
                .collect(Collectors.groupingBy(l -> l.getMedicamento().getId()));
        rs.setColumnas(List.of("Código", "Producto", "Tipo", "Existencias", "Stock mínimo",
                "Valor a costo", "Precio venta", "Valor potencial de venta"));
        BigDecimal[] t = this.ceros(3);
        for (Medicamento m : medicamentoRepository.findAllByOrderByNombreAsc()) {
            List<MedicamentoLote> lm = lotes.getOrDefault(m.getId(), List.of());
            int stock = lm.stream().filter(l -> !l.estaVencido()).mapToInt(MedicamentoLote::getCantidadDisponible).sum();
            BigDecimal costo = lm.stream().filter(l -> !l.estaVencido())
                    .map(l -> l.getPrecioCompra().multiply(BigDecimal.valueOf(l.getCantidadDisponible())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal venta = m.getPrecioVenta().multiply(BigDecimal.valueOf(stock));
            rs.getFilas().add(List.of(m.getCodigo(), m.getNombre(), m.getTipo(), stock, m.getStockMinimo(), costo,
                    m.getPrecioVenta(), venta));
            this.sumar(t, new BigDecimal[]{BigDecimal.valueOf(stock), costo, venta});
        }
        rs.setTotales(List.of("TOTAL", "", "", t[0].intValue(), "", t[1], "", t[2]));
    }

    private void alertas(ReporteRs rs, String filtro) {
        rs.setColumnas(List.of("Código", "Producto", "Alerta", "Disponible", "Stock mínimo", "Próximo vencimiento", "Próximo pedido"));
        for (MedicamentoRs m : inventarioService.listarInventario()) {
            for (String alerta : m.getAlertas()) {
                if (filtro == null || filtro.isBlank() || filtro.equalsIgnoreCase(alerta)) {
                    rs.getFilas().add(List.of(m.getCodigo(), m.getNombre(), alerta, m.getStockDisponible(), m.getStockMinimo(),
                            m.getProximoVencimiento() == null ? "" : m.getProximoVencimiento().toString(),
                            m.getFechaProximoPedido() == null ? "" : m.getFechaProximoPedido().toString()));
                }
            }
        }
        rs.setTotales(List.of("TOTAL ALERTAS", rs.getFilas().size()));
    }

    private void vencimientos(ReporteRs rs) {
        LocalDate limite = LocalDate.now().plusDays(InventarioServiceImpl.DIAS_POR_VENCER);
        rs.setColumnas(List.of("Producto", "Lote", "Vence", "Unidades", "Estado"));
        int unidades = 0;
        for (MedicamentoLote l : loteRepository.findAll()) {
            if (l.getFechaVence() == null || l.getCantidadDisponible() == 0 || l.getFechaVence().isAfter(limite)) {
                continue;
            }
            rs.getFilas().add(List.of(l.getMedicamento().getNombre(), l.getNumeroLote(), l.getFechaVence().toString(),
                    l.getCantidadDisponible(), l.estaVencido() ? "VENCIDO" : "PRÓXIMO A VENCER"));
            unidades += l.getCantidadDisponible();
        }
        rs.setTotales(List.of("TOTAL", "", "", unidades));
    }

    private void movimientos(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String filtro) {
        Map<Long, String> usuarios = usuarioRepository.findAll().stream().collect(Collectors.toMap(Usuario::getId, Usuario::getUsername));
        Map<Long, Medicamento> meds = medicamentoRepository.findAll().stream().collect(Collectors.toMap(Medicamento::getId, m -> m));
        Map<Long, String> lotes = loteRepository.findAll().stream().collect(Collectors.toMap(MedicamentoLote::getId, MedicamentoLote::getNumeroLote));
        rs.setColumnas(List.of("Fecha", "Tipo", "Código", "Producto", "Lote", "Cantidad", "Saldo lote", "Usuario", "Fórmula", "Despacho"));
        int entradas = 0;
        int salidas = 0;
        for (MovimientoInventario m : movimientoRepository.findByFechaBetweenOrderByFechaDesc(ini, fin)) {
            if (filtro != null && !filtro.isBlank() && !filtro.equalsIgnoreCase(m.getTipo())) {
                continue;
            }
            Medicamento med = meds.get(m.getMedicamentoId());
            rs.getFilas().add(List.of(m.getFecha().format(FH), m.getTipo(), med.getCodigo(), med.getNombre(),
                    lotes.getOrDefault(m.getLoteId(), ""), m.getCantidad(), m.getSaldoLote(),
                    usuarios.getOrDefault(m.getUsuarioId(), ""), m.getFormulaId() == null ? "" : "#" + m.getFormulaId(),
                    m.getDespachoId() == null ? "" : "#" + m.getDespachoId()));
            if (MovimientoInventario.SALIDA.equals(m.getTipo())) {
                salidas += m.getCantidad();
            } else {
                entradas += m.getCantidad();
            }
        }
        rs.setTotales(List.of("Entradas: " + entradas, "Salidas: " + salidas));
    }

    // ------------------------------------------------------------------ gestion medica

    private List<Cita> citasEntre(LocalDateTime ini, LocalDateTime fin) {
        return citaRepository.findByFechaHoraBetweenOrderByFechaHoraDesc(ini, fin);
    }

    private void citasPorEstado(ReporteRs rs, LocalDateTime ini, LocalDateTime fin) {
        Map<String, Integer> conteo = new LinkedHashMap<>();
        List.of("pendiente", "confirmada", "programada", "atendida", "cancelada").forEach(e -> conteo.put(e, 0));
        this.citasEntre(ini, fin).forEach(c -> conteo.merge(c.getEstado(), 1, Integer::sum));
        rs.setColumnas(List.of("Estado", "Citas"));
        conteo.forEach((estado, n) -> rs.getFilas().add(List.of(estado.equals("atendida") ? "atendida / finalizada" : estado, n)));
        rs.setTotales(List.of("TOTAL CITAS", conteo.values().stream().mapToInt(Integer::intValue).sum()));
    }

    private void citasDetalle(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String filtro) {
        rs.setColumnas(List.of("Fecha", "Mascota", "Propietario", "Veterinario", "Motivo", "Estado"));
        for (Cita c : this.citasEntre(ini, fin)) {
            if (filtro != null && !filtro.isBlank() && !filtro.equalsIgnoreCase(c.getEstado())) {
                continue;
            }
            rs.getFilas().add(List.of(c.getFechaHora().format(FH), c.getMascota().getNombreMascota(),
                    c.getCliente().getNombres() + " " + c.getCliente().getApellidos(),
                    c.getMedico().getNombres() + " " + c.getMedico().getApellidos(),
                    c.getMotivo() == null ? "" : c.getMotivo(), c.getEstado()));
        }
        rs.setTotales(List.of("TOTAL", rs.getFilas().size()));
    }

    private List<HistoriaMedica> consultasEntre(LocalDateTime ini, LocalDateTime fin) {
        return historiaRepository.findAllByOrderByFechaHoraDesc().stream()
                .filter(h -> h.getFechaHora() != null && !h.getFechaHora().isBefore(ini) && !h.getFechaHora().isAfter(fin)).toList();
    }

    private void consultasAgrupadas(ReporteRs rs, LocalDateTime ini, LocalDateTime fin, String etiqueta,
                                    Function<HistoriaMedica, String> clave) {
        Map<String, Integer> conteo = new TreeMap<>();
        this.consultasEntre(ini, fin).forEach(h -> conteo.merge(clave.apply(h), 1, Integer::sum));
        rs.setColumnas(List.of(etiqueta, "Consultas realizadas"));
        conteo.forEach((k, n) -> rs.getFilas().add(List.of(k, n)));
        rs.setTotales(List.of("TOTAL", conteo.values().stream().mapToInt(Integer::intValue).sum()));
    }

    private void mascotasAtendidas(ReporteRs rs, LocalDateTime ini, LocalDateTime fin) {
        Map<Integer, List<HistoriaMedica>> porMascota = this.consultasEntre(ini, fin).stream()
                .collect(Collectors.groupingBy(h -> h.getMascota().getMascotaId()));
        rs.setColumnas(List.of("Mascota", "Especie", "Propietario", "Consultas", "Última atención"));
        porMascota.values().forEach(lista -> {
            HistoriaMedica h = lista.get(0);
            rs.getFilas().add(List.of(h.getMascota().getNombreMascota(),
                    h.getMascota().getRaza() == null ? "" : h.getMascota().getRaza().getEspecie(),
                    h.getMascota().getCliente().getNombres() + " " + h.getMascota().getCliente().getApellidos(),
                    lista.size(), h.getFechaHora().format(FH)));
        });
        rs.setTotales(List.of("MASCOTAS ATENDIDAS", porMascota.size()));
    }

    // ------------------------------------------------------------------ ayudas

    private BigDecimal[] ceros(int n) {
        BigDecimal[] arr = new BigDecimal[n];
        java.util.Arrays.fill(arr, BigDecimal.ZERO);
        return arr;
    }

    private void sumar(BigDecimal[] acumulado, BigDecimal[] valores) {
        for (int i = 0; i < acumulado.length; i++) {
            acumulado[i] = acumulado[i].add(valores[i]);
        }
    }

    private String texto(Object valor) {
        if (valor instanceof BigDecimal b) {
            return PdfUtil.pesos(b);
        }
        return valor == null ? "" : valor.toString();
    }
}
