package com.uniminuto.clinica.serviceimpl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteFinancieroRS.ItemFinancieroAgrupado;
import com.uniminuto.clinica.models.ReporteFinancieroRS.ResumenPeriodoFinanciero;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS.ItemGestionMedicaAgrupado;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS.ItemMascotaAtendida;
import com.uniminuto.clinica.models.ReporteInventarioRS;
import com.uniminuto.clinica.models.ReporteInventarioRS.ItemExistenciaValorizada;
import com.uniminuto.clinica.models.ReporteInventarioRS.ItemMovimientoKardex;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import com.uniminuto.clinica.service.ReporteService;
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
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
/** Implementa la lógica analítica y de exportación PDF/Excel para los reportes financieros (4.1), de inventario (4.2) y de gestión médica (4.3 y 4.4). */
public class ReporteServiceImpl implements ReporteService {

    private static final String ENCABEZADO_INSTITUCIONAL = "CLÍNICA VETERINARIA UNIMINUTO — NIT 900.123.456-7";
    private static final DateTimeFormatter FMT_FECHA_HORA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter FMT_DIA = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter FMT_MES = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter FMT_ANIO = DateTimeFormatter.ofPattern("yyyy");
    private static final BigDecimal CIEN = new BigDecimal("100");

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;

    @Autowired
    private CitaRepository citaRepository;

    public ReporteServiceImpl() {
    }

    public ReporteServiceImpl(FacturaRepository facturaRepository,
                              MedicamentoRepository medicamentoRepository,
                              MovimientoMedicamentoRepository movimientoMedicamentoRepository,
                              CitaRepository citaRepository) {
        this.facturaRepository = facturaRepository;
        this.medicamentoRepository = medicamentoRepository;
        this.movimientoMedicamentoRepository = movimientoMedicamentoRepository;
        this.citaRepository = citaRepository;
    }

    // =========================================================================
    // 4.1 REPORTES FINANCIEROS
    // =========================================================================

    @Override
    public ReporteFinancieroRS generarReporteFinanciero(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId) {
        validarRangoFechas(fechaInicial, fechaFinal);

        List<Factura> todas = facturaRepository.findAllByOrderByFechaGeneracionDesc();
        List<Factura> filtradas = todas.stream()
                .filter(f -> estaEnRango(f.getFechaGeneracion(), fechaInicial, fechaFinal))
                .filter(f -> medicoId == null || (f.getMedico() != null && medicoId.equals(f.getMedico().getId())))
                .filter(f -> especializacionId == null || (f.getMedico() != null
                        && f.getMedico().getEspecializacion() != null
                        && especializacionId.equals(f.getMedico().getEspecializacion().getId())))
                .collect(Collectors.toList());

        ReporteFinancieroRS rs = new ReporteFinancieroRS();
        rs.setFechaGeneracion(LocalDateTime.now());
        rs.setRangoFechas(describirRango(fechaInicial, fechaFinal));
        rs.setTotalFacturasGeneradas(filtradas.size());

        BigDecimal ingresosTotales = BigDecimal.ZERO;
        BigDecimal ingresosRecaudados = BigDecimal.ZERO;
        BigDecimal saldoPendiente = BigDecimal.ZERO;
        BigDecimal ingresosServicios = BigDecimal.ZERO;
        BigDecimal ingresosMedicamentos = BigDecimal.ZERO;
        BigDecimal impuestosGenerados = BigDecimal.ZERO;
        BigDecimal impuestosRecaudados = BigDecimal.ZERO;
        BigDecimal descuentosAplicados = BigDecimal.ZERO;

        int pagadasCount = 0;
        BigDecimal montoPagadas = BigDecimal.ZERO;
        int pendientesCount = 0;
        BigDecimal montoPendientes = BigDecimal.ZERO;
        int parcialesCount = 0;
        BigDecimal montoParciales = BigDecimal.ZERO;
        int anuladasCount = 0;
        BigDecimal montoAnuladas = BigDecimal.ZERO;

        Map<String, ItemFinancieroAgrupado> mapEspecialidad = new LinkedHashMap<>();
        Map<Long, ItemFinancieroAgrupado> mapVeterinario = new LinkedHashMap<>();
        Map<String, ResumenPeriodoFinanciero> mapDia = new TreeMap<>(Comparator.reverseOrder());
        Map<String, ResumenPeriodoFinanciero> mapMes = new TreeMap<>(Comparator.reverseOrder());
        Map<String, ResumenPeriodoFinanciero> mapAnio = new TreeMap<>(Comparator.reverseOrder());

        for (Factura fac : filtradas) {
            String estado = fac.getEstado() != null ? fac.getEstado().toUpperCase() : Constantes.FACTURA_PENDIENTE;
            BigDecimal total = nvl(fac.getTotal());
            BigDecimal pagado = nvl(fac.getTotalPagado());
            BigDecimal saldo = nvl(fac.getSaldoPendiente());
            BigDecimal srv = nvl(fac.getSubtotalServicios());
            BigDecimal med = nvl(fac.getSubtotalMedicamentos());
            BigDecimal imp = nvl(fac.getValorImpuesto());
            BigDecimal desc = nvl(fac.getValorDescuento());

            if (Constantes.FACTURA_ANULADA.equals(estado)) {
                anuladasCount++;
                montoAnuladas = montoAnuladas.add(total);
                continue;
            }

            switch (estado) {
                case Constantes.FACTURA_PAGADA -> {
                    pagadasCount++;
                    montoPagadas = montoPagadas.add(total);
                }
                case Constantes.FACTURA_PARCIAL -> {
                    parcialesCount++;
                    montoParciales = montoParciales.add(pagado);
                }
                default -> {
                    pendientesCount++;
                    montoPendientes = montoPendientes.add(saldo);
                }
            }

            ingresosTotales = ingresosTotales.add(total);
            ingresosRecaudados = ingresosRecaudados.add(pagado);
            saldoPendiente = saldoPendiente.add(saldo);
            ingresosServicios = ingresosServicios.add(srv);
            ingresosMedicamentos = ingresosMedicamentos.add(med);
            impuestosGenerados = impuestosGenerados.add(imp);
            descuentosAplicados = descuentosAplicados.add(desc);

            // Impuesto efectivamente recaudado proporcional a lo pagado sobre el total de la factura
            if (Constantes.FACTURA_PAGADA.equals(estado)) {
                impuestosRecaudados = impuestosRecaudados.add(imp);
            } else if (total.compareTo(BigDecimal.ZERO) > 0 && pagado.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal proporcionImpuesto = imp.multiply(pagado).divide(total, 2, RoundingMode.HALF_UP);
                impuestosRecaudados = impuestosRecaudados.add(proporcionImpuesto);
            }

            // 1. Agrupación por Especialidad
            String nombreEsp = obtenerEspecialidad(fac.getMedico());
            ItemFinancieroAgrupado itemEsp = mapEspecialidad.computeIfAbsent(nombreEsp, k -> {
                ItemFinancieroAgrupado i = new ItemFinancieroAgrupado();
                i.setCategoria(k);
                return i;
            });
            acumularItemFinanciero(itemEsp, srv, med, desc, imp, total, pagado);

            // 2. Agrupación por Veterinario
            Long idMed = fac.getMedico() != null && fac.getMedico().getId() != null ? fac.getMedico().getId() : 0L;
            String nombreMed = nombreMedico(fac.getMedico());
            ItemFinancieroAgrupado itemVet = mapVeterinario.computeIfAbsent(idMed, k -> {
                ItemFinancieroAgrupado i = new ItemFinancieroAgrupado();
                i.setId(k);
                i.setCategoria(nombreMed);
                i.setSubcategoria(nombreEsp);
                return i;
            });
            acumularItemFinanciero(itemVet, srv, med, desc, imp, total, pagado);

            // 3. Resumen por Día, Mes y Año
            if (fac.getFechaGeneracion() != null) {
                acumularResumenPeriodo(mapDia, fac.getFechaGeneracion().format(FMT_DIA), srv, med, desc, imp, total, pagado);
                acumularResumenPeriodo(mapMes, fac.getFechaGeneracion().format(FMT_MES), srv, med, desc, imp, total, pagado);
                acumularResumenPeriodo(mapAnio, fac.getFechaGeneracion().format(FMT_ANIO), srv, med, desc, imp, total, pagado);
            }
        }

        rs.setIngresosTotalesFacturados(scale(ingresosTotales));
        rs.setIngresosRecaudados(scale(ingresosRecaudados));
        rs.setSaldoPendienteTotal(scale(saldoPendiente));
        rs.setIngresosConsultasServicios(scale(ingresosServicios));
        rs.setIngresosVentaMedicamentos(scale(ingresosMedicamentos));
        rs.setImpuestosGenerados(scale(impuestosGenerados));
        rs.setImpuestosRecaudados(scale(impuestosRecaudados));
        rs.setDescuentosAplicados(scale(descuentosAplicados));

        rs.setFacturasPagadas(pagadasCount);
        rs.setMontoFacturasPagadas(scale(montoPagadas));
        rs.setFacturasPendientes(pendientesCount);
        rs.setMontoFacturasPendientes(scale(montoPendientes));
        rs.setPagosParciales(parcialesCount);
        rs.setMontoPagosParciales(scale(montoParciales));
        rs.setFacturasAnuladas(anuladasCount);
        rs.setMontoFacturasAnuladas(scale(montoAnuladas));

        rs.setIngresosPorEspecialidad(new ArrayList<>(mapEspecialidad.values()));
        rs.setIngresosPorVeterinario(new ArrayList<>(mapVeterinario.values()));
        rs.setResumenPorDia(new ArrayList<>(mapDia.values()));
        rs.setResumenPorMes(new ArrayList<>(mapMes.values()));
        rs.setResumenPorAnio(new ArrayList<>(mapAnio.values()));
        return rs;
    }

    private void acumularItemFinanciero(ItemFinancieroAgrupado item, BigDecimal srv, BigDecimal med,
                                        BigDecimal desc, BigDecimal imp, BigDecimal total, BigDecimal pagado) {
        item.setCantidadFacturas(item.getCantidadFacturas() + 1);
        item.setIngresosServicios(scale(item.getIngresosServicios().add(srv)));
        item.setIngresosMedicamentos(scale(item.getIngresosMedicamentos().add(med)));
        item.setDescuentos(scale(item.getDescuentos().add(desc)));
        item.setImpuestos(scale(item.getImpuestos().add(imp)));
        item.setTotalFacturado(scale(item.getTotalFacturado().add(total)));
        item.setTotalRecaudado(scale(item.getTotalRecaudado().add(pagado)));
    }

    private void acumularResumenPeriodo(Map<String, ResumenPeriodoFinanciero> map, String clavePeriodo,
                                        BigDecimal srv, BigDecimal med, BigDecimal desc, BigDecimal imp,
                                        BigDecimal total, BigDecimal pagado) {
        ResumenPeriodoFinanciero res = map.computeIfAbsent(clavePeriodo, k -> {
            ResumenPeriodoFinanciero r = new ResumenPeriodoFinanciero();
            r.setPeriodo(k);
            return r;
        });
        res.setCantidadFacturas(res.getCantidadFacturas() + 1);
        res.setIngresosServicios(scale(res.getIngresosServicios().add(srv)));
        res.setIngresosMedicamentos(scale(res.getIngresosMedicamentos().add(med)));
        res.setDescuentos(scale(res.getDescuentos().add(desc)));
        res.setImpuestos(scale(res.getImpuestos().add(imp)));
        res.setTotalFacturado(scale(res.getTotalFacturado().add(total)));
        res.setTotalRecaudado(scale(res.getTotalRecaudado().add(pagado)));
    }

    // =========================================================================
    // 4.2 REPORTES DE INVENTARIO
    // =========================================================================

    @Override
    public ReporteInventarioRS generarReporteInventario(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicamentoId, Integer diasProximoVencer) {
        validarRangoFechas(fechaInicial, fechaFinal);
        int umbralDias = (diasProximoVencer != null && diasProximoVencer > 0) ? diasProximoVencer : 30;
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimiteProximo = hoy.plusDays(umbralDias);

        List<Medicamento> medicamentos = medicamentoRepository.findAllByOrderByFechaVenceAsc();
        Map<Long, Medicamento> mapMedicamentos = new HashMap<>();
        for (Medicamento m : medicamentos) {
            if (m.getId() != null) {
                mapMedicamentos.put(m.getId(), m);
            }
        }

        ReporteInventarioRS rs = new ReporteInventarioRS();
        rs.setFechaGeneracion(LocalDateTime.now());
        rs.setRangoFechas(describirRango(fechaInicial, fechaFinal));
        rs.setDiasUmbralProximoVencer(umbralDias);
        rs.setTotalProductos(medicamentos.size());

        int totalUnidades = 0;
        BigDecimal valorCostoTotal = BigDecimal.ZERO;
        BigDecimal valorVentaTotal = BigDecimal.ZERO;

        for (Medicamento med : medicamentos) {
            int stock = med.getCantidadDisponible() != null ? med.getCantidadDisponible() : 0;
            int minimo = med.getStockMinimo() != null ? med.getStockMinimo() : 0;
            BigDecimal costoUnit = nvl(med.getPrecioCompra());
            BigDecimal ventaUnit = nvl(med.getPrecioVenta());

            BigDecimal costoItem = costoUnit.multiply(BigDecimal.valueOf(stock)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal ventaItem = ventaUnit.multiply(BigDecimal.valueOf(stock)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal margenItem = ventaItem.subtract(costoItem).setScale(2, RoundingMode.HALF_UP);

            totalUnidades += stock;
            valorCostoTotal = valorCostoTotal.add(costoItem);
            valorVentaTotal = valorVentaTotal.add(ventaItem);

            boolean estaVencido = med.getFechaVence() != null && med.getFechaVence().isBefore(hoy);
            boolean proximoVencer = med.getFechaVence() != null
                    && !med.getFechaVence().isBefore(hoy)
                    && !med.getFechaVence().isAfter(fechaLimiteProximo);
            boolean estaAgotado = stock <= 0;
            boolean enStockMinimo = stock > 0 && stock <= minimo;

            if (estaVencido) {
                rs.getMedicamentosVencidos().add(med);
            } else if (proximoVencer) {
                rs.getMedicamentosProximosVencer().add(med);
            }

            if (estaAgotado) {
                rs.getMedicamentosAgotados().add(med);
            } else if (enStockMinimo) {
                rs.getMedicamentosStockMinimo().add(med);
            }

            String estadoAlerta = "NORMAL";
            if (estaVencido) {
                estadoAlerta = "VENCIDO";
            } else if (estaAgotado) {
                estadoAlerta = "AGOTADO";
            } else if (enStockMinimo) {
                estadoAlerta = "STOCK_MINIMO";
            } else if (proximoVencer) {
                estadoAlerta = "PROXIMO_VENCER";
            }

            long diasRestantes = med.getFechaVence() != null
                    ? ChronoUnit.DAYS.between(hoy, med.getFechaVence())
                    : 0;

            ItemExistenciaValorizada item = new ItemExistenciaValorizada();
            item.setMedicamentoId(med.getId());
            item.setCodigo(med.getCodigo());
            item.setNombre(med.getNombre());
            item.setPresentacion(med.getPresentacion());
            item.setLote(med.getLote());
            item.setRegistroInvima(med.getRegistroInvima());
            item.setRequiereRegistroInvima(med.getRequiereRegistroInvima());
            item.setFechaCompra(med.getFechaCompra());
            item.setFechaIngresoInventario(med.getFechaIngresoInventario());
            item.setFechaVence(med.getFechaVence());
            item.setDiasParaVencer(diasRestantes);
            item.setCantidadDisponible(stock);
            item.setStockMinimo(minimo);
            item.setPrecioCompra(scale(costoUnit));
            item.setPrecioVenta(scale(ventaUnit));
            item.setValorCostoTotal(costoItem);
            item.setValorVentaTotal(ventaItem);
            item.setMargenPotencial(margenItem);
            item.setEstadoAlerta(estadoAlerta);
            item.setActivo(med.getActivo());
            rs.getExistenciasActuales().add(item);
        }

        rs.setTotalUnidadesExistencia(totalUnidades);
        rs.setValorInventarioCostoAdquisicion(scale(valorCostoTotal));
        rs.setValorPotencialVenta(scale(valorVentaTotal));
        rs.setMargenPotencialEstimado(scale(valorVentaTotal.subtract(valorCostoTotal)));

        rs.setCantidadAgotados(rs.getMedicamentosAgotados().size());
        rs.setCantidadStockMinimo(rs.getMedicamentosStockMinimo().size());
        rs.setCantidadProximosVencer(rs.getMedicamentosProximosVencer().size());
        rs.setCantidadVencidos(rs.getMedicamentosVencidos().size());

        // Construcción del Kardex de Entradas y Salidas de Inventario
        List<MovimientoMedicamento> movimientosDB = movimientoMedicamentoRepository != null
                ? movimientoMedicamentoRepository.findAll()
                : new ArrayList<>();

        Map<Long, Boolean> tieneEntradaRegistrada = new HashMap<>();
        Map<Long, Integer> salidasPorMedicamento = new HashMap<>();
        for (MovimientoMedicamento m : movimientosDB) {
            if ("ENTRADA".equalsIgnoreCase(m.getTipo())) {
                tieneEntradaRegistrada.put(m.getMedicamentoId(), true);
            } else if ("SALIDA".equalsIgnoreCase(m.getTipo())) {
                salidasPorMedicamento.merge(m.getMedicamentoId(), m.getCantidad() != null ? m.getCantidad() : 0, Integer::sum);
            }
        }

        List<ItemMovimientoKardex> todosMovimientos = new ArrayList<>();
        for (MovimientoMedicamento mov : movimientosDB) {
            Medicamento med = mapMedicamentos.get(mov.getMedicamentoId());
            ItemMovimientoKardex k = new ItemMovimientoKardex();
            k.setId(mov.getId());
            k.setFechaRegistro(mov.getFechaRegistro());
            k.setMedicamentoId(mov.getMedicamentoId());
            k.setCodigoMedicamento(med != null ? med.getCodigo() : "-");
            k.setNombreMedicamento(med != null ? med.getNombre() : "Medicamento #" + mov.getMedicamentoId());
            k.setPresentacion(med != null ? med.getPresentacion() : "-");
            k.setLote(med != null ? med.getLote() : "-");
            k.setTipo(mov.getTipo() != null ? mov.getTipo().toUpperCase() : "SALIDA");
            k.setCantidad(mov.getCantidad() != null ? mov.getCantidad() : 0);
            k.setMotivo(mov.getMotivo());
            k.setReferenciaTipo(mov.getReferenciaTipo());
            k.setReferenciaId(mov.getReferenciaId());
            k.setUsuarioRegistro(mov.getUsuarioRegistro() != null ? mov.getUsuarioRegistro() : "INVENTARIO");
            todosMovimientos.add(k);
        }

        // Sintetizar movimiento de ENTRADA inicial para productos previos que aún no tenían registro explícito de entrada
        for (Medicamento med : medicamentos) {
            if (med.getId() != null && !Boolean.TRUE.equals(tieneEntradaRegistrada.get(med.getId()))) {
                int stockActual = med.getCantidadDisponible() != null ? med.getCantidadDisponible() : 0;
                int salidas = salidasPorMedicamento.getOrDefault(med.getId(), 0);
                int cantidadInicial = stockActual + salidas;
                if (cantidadInicial > 0) {
                    LocalDateTime fechaIngreso = med.getFechaIngresoInventario() != null
                            ? med.getFechaIngresoInventario().atTime(8, 0)
                            : (med.getFechaCreacionRegistro() != null ? med.getFechaCreacionRegistro() : LocalDateTime.now());
                    ItemMovimientoKardex entradaInicial = new ItemMovimientoKardex();
                    entradaInicial.setId(0L);
                    entradaInicial.setFechaRegistro(fechaIngreso);
                    entradaInicial.setMedicamentoId(med.getId());
                    entradaInicial.setCodigoMedicamento(med.getCodigo());
                    entradaInicial.setNombreMedicamento(med.getNombre());
                    entradaInicial.setPresentacion(med.getPresentacion());
                    entradaInicial.setLote(med.getLote());
                    entradaInicial.setTipo("ENTRADA");
                    entradaInicial.setCantidad(cantidadInicial);
                    entradaInicial.setMotivo("INGRESO INICIAL AL INVENTARIO (Lote: " + (med.getLote() != null ? med.getLote() : "N/A") + ")");
                    entradaInicial.setReferenciaTipo("INGRESO_INICIAL");
                    entradaInicial.setReferenciaId(med.getId());
                    entradaInicial.setUsuarioRegistro("INVENTARIO");
                    todosMovimientos.add(entradaInicial);
                }
            }
        }

        List<ItemMovimientoKardex> movimientosFiltrados = todosMovimientos.stream()
                .filter(k -> medicamentoId == null || medicamentoId.equals(k.getMedicamentoId()))
                .filter(k -> estaEnRango(k.getFechaRegistro(), fechaInicial, fechaFinal))
                .sorted((a, b) -> {
                    LocalDateTime fa = a.getFechaRegistro() != null ? a.getFechaRegistro() : LocalDateTime.MIN;
                    LocalDateTime fb = b.getFechaRegistro() != null ? b.getFechaRegistro() : LocalDateTime.MIN;
                    return fb.compareTo(fa);
                })
                .collect(Collectors.toList());

        int totalEntradas = 0;
        int totalSalidas = 0;
        for (ItemMovimientoKardex k : movimientosFiltrados) {
            if ("ENTRADA".equalsIgnoreCase(k.getTipo())) {
                totalEntradas += (k.getCantidad() != null ? k.getCantidad() : 0);
            } else {
                totalSalidas += (k.getCantidad() != null ? k.getCantidad() : 0);
            }
        }

        rs.setTotalMovimientosPeriodo(movimientosFiltrados.size());
        rs.setTotalUnidadesEntradaPeriodo(totalEntradas);
        rs.setTotalUnidadesSalidaPeriodo(totalSalidas);
        rs.setMovimientosInventario(movimientosFiltrados);
        return rs;
    }

    // =========================================================================
    // 4.3 REPORTES DE GESTIÓN MÉDICA
    // =========================================================================

    @Override
    public ReporteGestionMedicaRS generarReporteGestionMedica(
            LocalDate fechaInicial, LocalDate fechaFinal, Long medicoId, Long especializacionId) {
        validarRangoFechas(fechaInicial, fechaFinal);

        List<Cita> todasCitas = citaRepository.findAllByOrderByFechaHoraDesc();
        List<Cita> filtradas = todasCitas.stream()
                .filter(c -> estaEnRango(c.getFechaHora(), fechaInicial, fechaFinal))
                .filter(c -> medicoId == null || (c.getMedico() != null && medicoId.equals(c.getMedico().getId())))
                .filter(c -> especializacionId == null || (c.getMedico() != null
                        && c.getMedico().getEspecializacion() != null
                        && especializacionId.equals(c.getMedico().getEspecializacion().getId())))
                .collect(Collectors.toList());

        ReporteGestionMedicaRS rs = new ReporteGestionMedicaRS();
        rs.setFechaGeneracion(LocalDateTime.now());
        rs.setRangoFechas(describirRango(fechaInicial, fechaFinal));
        rs.setTotalCitasPeriodo(filtradas.size());

        int atendidas = 0;
        int canceladas = 0;
        int estPendiente = 0;
        int estProgramada = 0;
        int estConfirmada = 0;

        Map<Long, ItemGestionMedicaAgrupado> mapVeterinario = new LinkedHashMap<>();
        Map<String, ItemGestionMedicaAgrupado> mapEspecialidad = new LinkedHashMap<>();
        Map<Integer, ItemMascotaAtendida> mapMascotasAtendidas = new LinkedHashMap<>();

        for (Cita cita : filtradas) {
            String estado = cita.getEstado() != null ? cita.getEstado().toUpperCase() : Constantes.CITA_PENDIENTE;
            boolean esAtendida = Constantes.CITA_ATENDIDA.equals(estado) || Constantes.CITA_FINALIZADA.equals(estado);
            boolean esCancelada = Constantes.CITA_CANCELADA.equals(estado);
            boolean esPendienteActiva = !esAtendida && !esCancelada;

            if (esAtendida) {
                atendidas++;
            } else if (esCancelada) {
                canceladas++;
            } else {
                if (Constantes.CITA_PROGRAMADA.equals(estado)) {
                    estProgramada++;
                } else if (Constantes.CITA_CONFIRMADA.equals(estado)) {
                    estConfirmada++;
                } else {
                    estPendiente++;
                }
            }

            // 1. Agrupación por Veterinario
            Long idMed = cita.getMedico() != null && cita.getMedico().getId() != null ? cita.getMedico().getId() : 0L;
            String nomMed = nombreMedico(cita.getMedico());
            String nomEsp = obtenerEspecialidad(cita.getMedico());

            ItemGestionMedicaAgrupado itemVet = mapVeterinario.computeIfAbsent(idMed, k -> {
                ItemGestionMedicaAgrupado i = new ItemGestionMedicaAgrupado();
                i.setId(k);
                i.setNombre(nomMed);
                i.setSubcategoria(nomEsp);
                return i;
            });
            acumularItemMedico(itemVet, esAtendida, esPendienteActiva, esCancelada);

            // 2. Agrupación por Especialidad
            ItemGestionMedicaAgrupado itemEsp = mapEspecialidad.computeIfAbsent(nomEsp, k -> {
                ItemGestionMedicaAgrupado i = new ItemGestionMedicaAgrupado();
                i.setNombre(k);
                return i;
            });
            acumularItemMedico(itemEsp, esAtendida, esPendienteActiva, esCancelada);

            // 3. Mascotas atendidas en el período
            if (esAtendida && cita.getMascota() != null && cita.getMascota().getMascotaId() != null) {
                Mascota mas = cita.getMascota();
                Cliente cli = cita.getCliente() != null ? cita.getCliente() : mas.getCliente();
                ItemMascotaAtendida itemMas = mapMascotasAtendidas.computeIfAbsent(mas.getMascotaId(), k -> {
                    ItemMascotaAtendida im = new ItemMascotaAtendida();
                    im.setMascotaId(mas.getMascotaId());
                    im.setNombreMascota(mas.getNombreMascota());
                    im.setRaza(mas.getRaza() != null ? mas.getRaza().getNombre() : "-");
                    im.setEdad(mas.getEdad());
                    im.setPropietario(cli != null ? (cli.getNombres() + " " + cli.getApellidos()).trim() : "-");
                    im.setDocumentoPropietario(cli != null ? (cli.getTipoDocumento() + " " + cli.getNumeroDocumento()).trim() : "-");
                    im.setTelefonoPropietario(cli != null ? cli.getTelefono() : "-");
                    im.setUltimaFechaAtencion(cita.getFechaHora());
                    im.setUltimoVeterinario(nomMed);
                    im.setUltimoMotivo(cita.getMotivo());
                    return im;
                });
                itemMas.setConsultasAtendidas(itemMas.getConsultasAtendidas() + 1);
                if (cita.getFechaHora() != null && (itemMas.getUltimaFechaAtencion() == null
                        || cita.getFechaHora().isAfter(itemMas.getUltimaFechaAtencion()))) {
                    itemMas.setUltimaFechaAtencion(cita.getFechaHora());
                    itemMas.setUltimoVeterinario(nomMed);
                    itemMas.setUltimoMotivo(cita.getMotivo());
                }
            }
        }

        // Calcular porcentajes de cumplimiento
        for (ItemGestionMedicaAgrupado iv : mapVeterinario.values()) {
            if (iv.getTotalCitas() > 0) {
                iv.setPorcentajeCumplimiento(BigDecimal.valueOf(iv.getCitasAtendidas())
                        .multiply(CIEN)
                        .divide(BigDecimal.valueOf(iv.getTotalCitas()), 1, RoundingMode.HALF_UP));
            }
        }
        for (ItemGestionMedicaAgrupado ie : mapEspecialidad.values()) {
            if (ie.getTotalCitas() > 0) {
                ie.setPorcentajeCumplimiento(BigDecimal.valueOf(ie.getCitasAtendidas())
                        .multiply(CIEN)
                        .divide(BigDecimal.valueOf(ie.getTotalCitas()), 1, RoundingMode.HALF_UP));
            }
        }

        rs.setCitasAtendidas(atendidas);
        rs.setCitasCanceladas(canceladas);
        rs.setCitasEstadoPendiente(estPendiente);
        rs.setCitasEstadoProgramada(estProgramada);
        rs.setCitasEstadoConfirmada(estConfirmada);
        rs.setCitasPendientesTotal(estPendiente + estProgramada + estConfirmada);
        rs.setTotalMascotasAtendidas(mapMascotasAtendidas.size());

        rs.setConsultasPorVeterinario(new ArrayList<>(mapVeterinario.values()));
        rs.setConsultasPorEspecialidad(new ArrayList<>(mapEspecialidad.values()));
        rs.setMascotasAtendidasPeriodo(new ArrayList<>(mapMascotasAtendidas.values()));
        return rs;
    }

    private void acumularItemMedico(ItemGestionMedicaAgrupado item, boolean atendida, boolean pendiente, boolean cancelada) {
        item.setTotalCitas(item.getTotalCitas() + 1);
        if (atendida) {
            item.setCitasAtendidas(item.getCitasAtendidas() + 1);
        } else if (cancelada) {
            item.setCitasCanceladas(item.getCitasCanceladas() + 1);
        } else if (pendiente) {
            item.setCitasPendientes(item.getCitasPendientes() + 1);
        }
    }

    // =========================================================================
    // UTILIDADES COMUNES
    // =========================================================================

    private void validarRangoFechas(LocalDate fechaInicial, LocalDate fechaFinal) {
        if (fechaInicial != null && fechaFinal != null && fechaInicial.isAfter(fechaFinal)) {
            throw new BadRequestException("La fecha inicial del reporte no puede ser posterior a la fecha final");
        }
    }

    private boolean estaEnRango(LocalDateTime fechaHora, LocalDate inicio, LocalDate fin) {
        if (fechaHora == null) {
            return inicio == null && fin == null;
        }
        LocalDate fecha = fechaHora.toLocalDate();
        if (inicio != null && fecha.isBefore(inicio)) {
            return false;
        }
        if (fin != null && fecha.isAfter(fin)) {
            return false;
        }
        return true;
    }

    private String describirRango(LocalDate inicio, LocalDate fin) {
        if (inicio != null && fin != null) {
            return inicio.format(FMT_DIA) + " a " + fin.format(FMT_DIA);
        }
        if (inicio != null) {
            return "Desde " + inicio.format(FMT_DIA);
        }
        if (fin != null) {
            return "Hasta " + fin.format(FMT_DIA);
        }
        return "Histórico completo";
    }

    private String nombreMedico(Medico medico) {
        if (medico == null) {
            return "Sin veterinario";
        }
        return ("Dr(a). " + (medico.getNombres() != null ? medico.getNombres() : "") + " "
                + (medico.getApellidos() != null ? medico.getApellidos() : "")).trim();
    }

    private String obtenerEspecialidad(Medico medico) {
        if (medico != null && medico.getEspecializacion() != null
                && medico.getEspecializacion().getNombre() != null) {
            return medico.getEspecializacion().getNombre().trim();
        }
        return "Medicina General";
    }

    private BigDecimal nvl(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private BigDecimal scale(BigDecimal val) {
        return nvl(val).setScale(2, RoundingMode.HALF_UP);
    }

    // =========================================================================
    // 4.4 CONTROL DE PERMISOS POR ROL Y EXPORTACIÓN EN EXCEL Y PDF
    // =========================================================================

    @Override
    public void validarPermisoReporte(String rol, String tipoReporte) {
        String tipoNorm = normalizarTipoReporte(tipoReporte);
        String rolNorm = (rol == null || rol.trim().isEmpty())
                ? Constantes.ROL_ADMINISTRADOR
                : rol.trim().toUpperCase();

        boolean permitido = switch (rolNorm) {
            case Constantes.ROL_ADMINISTRADOR -> true;
            case Constantes.ROL_SECRETARIA -> Constantes.REPORTE_FINANCIERO.equals(tipoNorm)
                    || Constantes.REPORTE_MEDICO.equals(tipoNorm);
            case Constantes.ROL_VETERINARIO -> Constantes.REPORTE_MEDICO.equals(tipoNorm);
            case Constantes.ROL_DESPACHO_MEDICO -> Constantes.REPORTE_INVENTARIO.equals(tipoNorm);
            default -> false;
        };

        if (!permitido) {
            throw new BadRequestException(
                    "Acceso restringido: El rol '" + rolNorm + "' no tiene permisos para consultar o exportar el reporte '" + tipoNorm + "'");
        }
    }

    private String normalizarTipoReporte(String tipoReporte) {
        if (tipoReporte == null || tipoReporte.trim().isEmpty()) {
            throw new BadRequestException("El tipo de reporte es obligatorio (FINANCIERO, INVENTARIO o MEDICO)");
        }
        String t = tipoReporte.trim().toUpperCase();
        if (Constantes.REPORTE_FINANCIERO.equals(t)
                || Constantes.REPORTE_INVENTARIO.equals(t)
                || Constantes.REPORTE_MEDICO.equals(t)) {
            return t;
        }
        throw new BadRequestException("Tipo de reporte no válido: " + tipoReporte + ". Valores permitidos: FINANCIERO, INVENTARIO, MEDICO");
    }

    private String rolEfectivo(String rol) {
        return (rol == null || rol.trim().isEmpty()) ? Constantes.ROL_ADMINISTRADOR : rol.trim().toUpperCase();
    }

    private String fmtMoneda(BigDecimal valor) {
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        return "$" + nf.format(nvl(valor));
    }

    // -------------------------------------------------------------------------
    // EXPORTACIÓN A EXCEL (.XLSX) CON APACHE POI
    // -------------------------------------------------------------------------

    @Override
    public byte[] exportarReporteExcel(
            String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
            Long medicoId, Long especializacionId, Long medicamentoId,
            Integer diasProximoVencer, String agrupacion, String rol) {
        String tipoNorm = normalizarTipoReporte(tipoReporte);
        validarPermisoReporte(rol, tipoNorm);
        String rolUsuario = rolEfectivo(rol);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            CellStyle estiloInst = crearEstiloTituloExcel(workbook, IndexedColors.DARK_BLUE.getIndex(), (short) 13);
            CellStyle estiloSubtitulo = crearEstiloTituloExcel(workbook, IndexedColors.ROYAL_BLUE.getIndex(), (short) 11);
            CellStyle estiloMetaLabel = crearEstiloNegritaExcel(workbook, false);
            CellStyle estiloHeaderTabla = crearEstiloEncabezadoTablaExcel(workbook);
            CellStyle estiloCelda = crearEstiloCeldaExcel(workbook, HorizontalAlignment.LEFT);
            CellStyle estiloCeldaNum = crearEstiloCeldaExcel(workbook, HorizontalAlignment.RIGHT);
            CellStyle estiloCeldaCentro = crearEstiloCeldaExcel(workbook, HorizontalAlignment.CENTER);
            CellStyle estiloTotalTexto = crearEstiloTotalExcel(workbook, HorizontalAlignment.LEFT);
            CellStyle estiloTotalNum = crearEstiloTotalExcel(workbook, HorizontalAlignment.RIGHT);
            CellStyle estiloTotalCentro = crearEstiloTotalExcel(workbook, HorizontalAlignment.CENTER);

            switch (tipoNorm) {
                case Constantes.REPORTE_FINANCIERO -> {
                    ReporteFinancieroRS fin = generarReporteFinanciero(fechaInicial, fechaFinal, medicoId, especializacionId);
                    construirExcelFinanciero(workbook, fin, agrupacion, rolUsuario,
                            estiloInst, estiloSubtitulo, estiloMetaLabel, estiloHeaderTabla,
                            estiloCelda, estiloCeldaNum, estiloCeldaCentro,
                            estiloTotalTexto, estiloTotalNum, estiloTotalCentro);
                }
                case Constantes.REPORTE_INVENTARIO -> {
                    ReporteInventarioRS inv = generarReporteInventario(fechaInicial, fechaFinal, medicamentoId, diasProximoVencer);
                    construirExcelInventario(workbook, inv, rolUsuario,
                            estiloInst, estiloSubtitulo, estiloMetaLabel, estiloHeaderTabla,
                            estiloCelda, estiloCeldaNum, estiloCeldaCentro,
                            estiloTotalTexto, estiloTotalNum, estiloTotalCentro);
                }
                case Constantes.REPORTE_MEDICO -> {
                    ReporteGestionMedicaRS med = generarReporteGestionMedica(fechaInicial, fechaFinal, medicoId, especializacionId);
                    construirExcelGestionMedica(workbook, med, rolUsuario,
                            estiloInst, estiloSubtitulo, estiloMetaLabel, estiloHeaderTabla,
                            estiloCelda, estiloCeldaNum, estiloCeldaCentro,
                            estiloTotalTexto, estiloTotalNum, estiloTotalCentro);
                }
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el archivo Excel del reporte: " + e.getMessage());
        }
    }

    private void construirExcelFinanciero(
            Workbook workbook, ReporteFinancieroRS fin, String agrupacion, String rolUsuario,
            CellStyle estiloInst, CellStyle estiloSubtitulo, CellStyle estiloMetaLabel, CellStyle estiloHeaderTabla,
            CellStyle estiloCelda, CellStyle estiloCeldaNum, CellStyle estiloCeldaCentro,
            CellStyle estiloTotalTexto, CellStyle estiloTotalNum, CellStyle estiloTotalCentro) {
        Sheet sheet = workbook.createSheet("4.1 Reporte Financiero");
        String agr = (agrupacion != null && !agrupacion.isBlank()) ? agrupacion.trim().toUpperCase() : "MES";

        int rowIdx = crearEncabezadoInstitucionalExcel(
                sheet, "4.1 REPORTE FINANCIERO Y CONTABLE",
                fin.getRangoFechas(), fin.getFechaGeneracion(), rolUsuario,
                "Agrupación temporal: " + agr, 7, estiloInst, estiloSubtitulo, estiloMetaLabel);

        // 1. Resumen global de KPIs e indicadores de estado de facturas
        rowIdx++;
        Row rSec1 = sheet.createRow(rowIdx++);
        Cell cSec1 = rSec1.createCell(0);
        cSec1.setCellValue("1. RESUMEN GENERAL DE INGRESOS Y ESTADO DE FACTURAS");
        cSec1.setCellStyle(estiloMetaLabel);

        String[] headersKpi = {"Total Facturas", "Facturas Pagadas", "Pagos Parciales", "Facturas Pendientes",
                "Facturas Anuladas", "Ingresos Consultas", "Ingresos Medicamentos", "Impuestos Recaudados"};
        Row rKpiH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersKpi.length; i++) {
            Cell c = rKpiH.createCell(i);
            c.setCellValue(headersKpi[i]);
            c.setCellStyle(estiloHeaderTabla);
        }
        Row rKpiV = sheet.createRow(rowIdx++);
        crearCelda(rKpiV, 0, String.valueOf(fin.getTotalFacturasGeneradas()), estiloCeldaCentro);
        crearCelda(rKpiV, 1, fin.getFacturasPagadas() + " (" + fmtMoneda(fin.getMontoFacturasPagadas()) + ")", estiloCeldaCentro);
        crearCelda(rKpiV, 2, fin.getPagosParciales() + " (" + fmtMoneda(fin.getMontoPagosParciales()) + ")", estiloCeldaCentro);
        crearCelda(rKpiV, 3, fin.getFacturasPendientes() + " (" + fmtMoneda(fin.getMontoFacturasPendientes()) + ")", estiloCeldaCentro);
        crearCelda(rKpiV, 4, fin.getFacturasAnuladas() + " (" + fmtMoneda(fin.getMontoFacturasAnuladas()) + ")", estiloCeldaCentro);
        crearCelda(rKpiV, 5, fmtMoneda(fin.getIngresosConsultasServicios()), estiloCeldaNum);
        crearCelda(rKpiV, 6, fmtMoneda(fin.getIngresosVentaMedicamentos()), estiloCeldaNum);
        crearCelda(rKpiV, 7, fmtMoneda(fin.getImpuestosRecaudados()), estiloCeldaNum);

        Row rKpiTot = sheet.createRow(rowIdx++);
        crearCelda(rKpiTot, 0, "TOTALES GLOBALES DEL PERÍODO", estiloTotalTexto);
        crearCelda(rKpiTot, 1, "Descuentos: " + fmtMoneda(fin.getDescuentosAplicados()), estiloTotalCentro);
        crearCelda(rKpiTot, 2, "Impuestos Gen.: " + fmtMoneda(fin.getImpuestosGenerados()), estiloTotalCentro);
        crearCelda(rKpiTot, 3, "Saldo Pendiente:", estiloTotalTexto);
        crearCelda(rKpiTot, 4, fmtMoneda(fin.getSaldoPendienteTotal()), estiloTotalNum);
        crearCelda(rKpiTot, 5, "Total Facturado:", estiloTotalTexto);
        crearCelda(rKpiTot, 6, fmtMoneda(fin.getIngresosTotalesFacturados()), estiloTotalNum);
        crearCelda(rKpiTot, 7, "Recaudado: " + fmtMoneda(fin.getIngresosRecaudados()), estiloTotalNum);

        // 2. Tabla de Resumen por Período (Día, Mes o Año)
        rowIdx++;
        Row rSec2 = sheet.createRow(rowIdx++);
        Cell cSec2 = rSec2.createCell(0);
        cSec2.setCellValue("2. RESUMEN CONTABLE POR PERÍODO (" + agr + ")");
        cSec2.setCellStyle(estiloMetaLabel);

        String[] headersPer = {"Período (" + agr + ")", "Facturas Activas", "Ingresos Consultas", "Ingresos Medicamentos",
                "Descuentos", "Impuestos", "Total Facturado", "Total Recaudado"};
        Row rPerH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersPer.length; i++) {
            Cell c = rPerH.createCell(i);
            c.setCellValue(headersPer[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        List<ResumenPeriodoFinanciero> listaPeriodo = switch (agr) {
            case "DIA" -> fin.getResumenPorDia();
            case "ANIO" -> fin.getResumenPorAnio();
            default -> fin.getResumenPorMes();
        };

        int totFactPer = 0;
        BigDecimal totSrvPer = BigDecimal.ZERO;
        BigDecimal totMedPer = BigDecimal.ZERO;
        BigDecimal totDescPer = BigDecimal.ZERO;
        BigDecimal totImpPer = BigDecimal.ZERO;
        BigDecimal totFacPer = BigDecimal.ZERO;
        BigDecimal totRecPer = BigDecimal.ZERO;

        for (ResumenPeriodoFinanciero r : listaPeriodo) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, r.getPeriodo(), estiloCelda);
            crearCelda(row, 1, String.valueOf(r.getCantidadFacturas()), estiloCeldaCentro);
            crearCelda(row, 2, fmtMoneda(r.getIngresosServicios()), estiloCeldaNum);
            crearCelda(row, 3, fmtMoneda(r.getIngresosMedicamentos()), estiloCeldaNum);
            crearCelda(row, 4, fmtMoneda(r.getDescuentos()), estiloCeldaNum);
            crearCelda(row, 5, fmtMoneda(r.getImpuestos()), estiloCeldaNum);
            crearCelda(row, 6, fmtMoneda(r.getTotalFacturado()), estiloCeldaNum);
            crearCelda(row, 7, fmtMoneda(r.getTotalRecaudado()), estiloCeldaNum);

            totFactPer += r.getCantidadFacturas();
            totSrvPer = totSrvPer.add(nvl(r.getIngresosServicios()));
            totMedPer = totMedPer.add(nvl(r.getIngresosMedicamentos()));
            totDescPer = totDescPer.add(nvl(r.getDescuentos()));
            totImpPer = totImpPer.add(nvl(r.getImpuestos()));
            totFacPer = totFacPer.add(nvl(r.getTotalFacturado()));
            totRecPer = totRecPer.add(nvl(r.getTotalRecaudado()));
        }

        Row rPerTot = sheet.createRow(rowIdx++);
        crearCelda(rPerTot, 0, "TOTALES CALCULADOS", estiloTotalTexto);
        crearCelda(rPerTot, 1, String.valueOf(totFactPer), estiloTotalCentro);
        crearCelda(rPerTot, 2, fmtMoneda(totSrvPer), estiloTotalNum);
        crearCelda(rPerTot, 3, fmtMoneda(totMedPer), estiloTotalNum);
        crearCelda(rPerTot, 4, fmtMoneda(totDescPer), estiloTotalNum);
        crearCelda(rPerTot, 5, fmtMoneda(totImpPer), estiloTotalNum);
        crearCelda(rPerTot, 6, fmtMoneda(totFacPer), estiloTotalNum);
        crearCelda(rPerTot, 7, fmtMoneda(totRecPer), estiloTotalNum);

        // 3. Tabla de Ingresos por Veterinario y Especialidad
        rowIdx++;
        Row rSec3 = sheet.createRow(rowIdx++);
        Cell cSec3 = rSec3.createCell(0);
        cSec3.setCellValue("3. INGRESOS POR VETERINARIO Y ESPECIALIDAD");
        cSec3.setCellStyle(estiloMetaLabel);

        String[] headersVet = {"Veterinario", "Especialidad", "Facturas", "Ingresos Consultas",
                "Ingresos Medicamentos", "Descuentos", "Total Facturado", "Total Recaudado"};
        Row rVetH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersVet.length; i++) {
            Cell c = rVetH.createCell(i);
            c.setCellValue(headersVet[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        int totFactVet = 0;
        BigDecimal totSrvVet = BigDecimal.ZERO;
        BigDecimal totMedVet = BigDecimal.ZERO;
        BigDecimal totDescVet = BigDecimal.ZERO;
        BigDecimal totFacVet = BigDecimal.ZERO;
        BigDecimal totRecVet = BigDecimal.ZERO;

        for (ItemFinancieroAgrupado iv : fin.getIngresosPorVeterinario()) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, iv.getCategoria(), estiloCelda);
            crearCelda(row, 1, iv.getSubcategoria() != null ? iv.getSubcategoria() : "General", estiloCelda);
            crearCelda(row, 2, String.valueOf(iv.getCantidadFacturas()), estiloCeldaCentro);
            crearCelda(row, 3, fmtMoneda(iv.getIngresosServicios()), estiloCeldaNum);
            crearCelda(row, 4, fmtMoneda(iv.getIngresosMedicamentos()), estiloCeldaNum);
            crearCelda(row, 5, fmtMoneda(iv.getDescuentos()), estiloCeldaNum);
            crearCelda(row, 6, fmtMoneda(iv.getTotalFacturado()), estiloCeldaNum);
            crearCelda(row, 7, fmtMoneda(iv.getTotalRecaudado()), estiloCeldaNum);

            totFactVet += iv.getCantidadFacturas();
            totSrvVet = totSrvVet.add(nvl(iv.getIngresosServicios()));
            totMedVet = totMedVet.add(nvl(iv.getIngresosMedicamentos()));
            totDescVet = totDescVet.add(nvl(iv.getDescuentos()));
            totFacVet = totFacVet.add(nvl(iv.getTotalFacturado()));
            totRecVet = totRecVet.add(nvl(iv.getTotalRecaudado()));
        }

        Row rVetTot = sheet.createRow(rowIdx);
        crearCelda(rVetTot, 0, "TOTALES CALCULADOS", estiloTotalTexto);
        crearCelda(rVetTot, 1, "-", estiloTotalCentro);
        crearCelda(rVetTot, 2, String.valueOf(totFactVet), estiloTotalCentro);
        crearCelda(rVetTot, 3, fmtMoneda(totSrvVet), estiloTotalNum);
        crearCelda(rVetTot, 4, fmtMoneda(totMedVet), estiloTotalNum);
        crearCelda(rVetTot, 5, fmtMoneda(totDescVet), estiloTotalNum);
        crearCelda(rVetTot, 6, fmtMoneda(totFacVet), estiloTotalNum);
        crearCelda(rVetTot, 7, fmtMoneda(totRecVet), estiloTotalNum);

        autoAjustarColumnas(sheet, 8);
    }

    private void construirExcelInventario(
            Workbook workbook, ReporteInventarioRS inv, String rolUsuario,
            CellStyle estiloInst, CellStyle estiloSubtitulo, CellStyle estiloMetaLabel, CellStyle estiloHeaderTabla,
            CellStyle estiloCelda, CellStyle estiloCeldaNum, CellStyle estiloCeldaCentro,
            CellStyle estiloTotalTexto, CellStyle estiloTotalNum, CellStyle estiloTotalCentro) {
        Sheet sheet = workbook.createSheet("4.2 Inventario y Kardex");

        int rowIdx = crearEncabezadoInstitucionalExcel(
                sheet, "4.2 REPORTE DE VALORACIÓN DE INVENTARIO, ALERTAS Y KARDEX",
                inv.getRangoFechas(), inv.getFechaGeneracion(), rolUsuario,
                "Umbral próximo a vencer: " + inv.getDiasUmbralProximoVencer() + " días", 9,
                estiloInst, estiloSubtitulo, estiloMetaLabel);

        // 1. Tabla de Existencias Actuales y Valoración Costo vs Venta
        rowIdx++;
        Row rSec1 = sheet.createRow(rowIdx++);
        Cell cSec1 = rSec1.createCell(0);
        cSec1.setCellValue("1. EXISTENCIAS ACTUALES Y VALORACIÓN (COSTO DE ADQUISICIÓN VS. VENTA POTENCIAL)");
        cSec1.setCellStyle(estiloMetaLabel);

        String[] headersEx = {"Código", "Medicamento", "Presentación", "Lote", "Vencimiento",
                "Stock Actual", "Stock Mín.", "Costo Unit.", "Venta Unit.", "Valor Costo Total", "Valor Venta Potencial", "Estado Alerta"};
        Row rExH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersEx.length; i++) {
            Cell c = rExH.createCell(i);
            c.setCellValue(headersEx[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        int totalUnidades = 0;
        BigDecimal totalCosto = BigDecimal.ZERO;
        BigDecimal totalVenta = BigDecimal.ZERO;

        for (ItemExistenciaValorizada item : inv.getExistenciasActuales()) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, item.getCodigo(), estiloCelda);
            crearCelda(row, 1, item.getNombre(), estiloCelda);
            crearCelda(row, 2, item.getPresentacion() != null ? item.getPresentacion() : "-", estiloCelda);
            crearCelda(row, 3, item.getLote() != null ? item.getLote() : "-", estiloCeldaCentro);
            crearCelda(row, 4, item.getFechaVence() != null ? item.getFechaVence().toString() : "-", estiloCeldaCentro);
            crearCelda(row, 5, String.valueOf(item.getCantidadDisponible()), estiloCeldaCentro);
            crearCelda(row, 6, String.valueOf(item.getStockMinimo()), estiloCeldaCentro);
            crearCelda(row, 7, fmtMoneda(item.getPrecioCompra()), estiloCeldaNum);
            crearCelda(row, 8, fmtMoneda(item.getPrecioVenta()), estiloCeldaNum);
            crearCelda(row, 9, fmtMoneda(item.getValorCostoTotal()), estiloCeldaNum);
            crearCelda(row, 10, fmtMoneda(item.getValorVentaTotal()), estiloCeldaNum);
            crearCelda(row, 11, item.getEstadoAlerta(), estiloCeldaCentro);

            totalUnidades += (item.getCantidadDisponible() != null ? item.getCantidadDisponible() : 0);
            totalCosto = totalCosto.add(nvl(item.getValorCostoTotal()));
            totalVenta = totalVenta.add(nvl(item.getValorVentaTotal()));
        }

        Row rExTot = sheet.createRow(rowIdx++);
        crearCelda(rExTot, 0, "TOTALES CALCULADOS", estiloTotalTexto);
        crearCelda(rExTot, 1, inv.getTotalProductos() + " producto(s)", estiloTotalTexto);
        crearCelda(rExTot, 2, "Agotados: " + inv.getCantidadAgotados(), estiloTotalCentro);
        crearCelda(rExTot, 3, "Stock Mín: " + inv.getCantidadStockMinimo(), estiloTotalCentro);
        crearCelda(rExTot, 4, "Vencidos: " + inv.getCantidadVencidos(), estiloTotalCentro);
        crearCelda(rExTot, 5, totalUnidades + " und.", estiloTotalCentro);
        crearCelda(rExTot, 6, "-", estiloTotalCentro);
        crearCelda(rExTot, 7, "-", estiloTotalCentro);
        crearCelda(rExTot, 8, "TOTALES:", estiloTotalTexto);
        crearCelda(rExTot, 9, fmtMoneda(totalCosto), estiloTotalNum);
        crearCelda(rExTot, 10, fmtMoneda(totalVenta), estiloTotalNum);
        crearCelda(rExTot, 11, "Margen: " + fmtMoneda(totalVenta.subtract(totalCosto)), estiloTotalNum);

        // 2. Tabla de Kardex de Movimientos
        rowIdx++;
        Row rSec2 = sheet.createRow(rowIdx++);
        Cell cSec2 = rSec2.createCell(0);
        cSec2.setCellValue("2. HISTORIAL DE MOVIMIENTOS DE INVENTARIO (KARDEX ENTRADAS Y SALIDAS)");
        cSec2.setCellStyle(estiloMetaLabel);

        String[] headersKar = {"Fecha y Hora", "Tipo Movimiento", "Código", "Medicamento", "Lote", "Cantidad", "Motivo / Operación", "Usuario"};
        Row rKarH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersKar.length; i++) {
            Cell c = rKarH.createCell(i);
            c.setCellValue(headersKar[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        for (ItemMovimientoKardex mov : inv.getMovimientosInventario()) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, mov.getFechaRegistro() != null ? mov.getFechaRegistro().format(FMT_FECHA_HORA) : "-", estiloCeldaCentro);
            crearCelda(row, 1, mov.getTipo(), estiloCeldaCentro);
            crearCelda(row, 2, mov.getCodigoMedicamento(), estiloCelda);
            crearCelda(row, 3, mov.getNombreMedicamento(), estiloCelda);
            crearCelda(row, 4, mov.getLote() != null ? mov.getLote() : "-", estiloCeldaCentro);
            crearCelda(row, 5, ("ENTRADA".equalsIgnoreCase(mov.getTipo()) ? "+" : "-") + mov.getCantidad(), estiloCeldaCentro);
            crearCelda(row, 6, mov.getMotivo() != null ? mov.getMotivo() : "-", estiloCelda);
            crearCelda(row, 7, mov.getUsuarioRegistro(), estiloCeldaCentro);
        }

        Row rKarTot = sheet.createRow(rowIdx);
        crearCelda(rKarTot, 0, "TOTALES KARDEX", estiloTotalTexto);
        crearCelda(rKarTot, 1, inv.getTotalMovimientosPeriodo() + " mov(s)", estiloTotalCentro);
        crearCelda(rKarTot, 2, "Total Entradas:", estiloTotalTexto);
        crearCelda(rKarTot, 3, "+" + inv.getTotalUnidadesEntradaPeriodo() + " und.", estiloTotalCentro);
        crearCelda(rKarTot, 4, "Total Salidas:", estiloTotalTexto);
        crearCelda(rKarTot, 5, "-" + inv.getTotalUnidadesSalidaPeriodo() + " und.", estiloTotalCentro);
        crearCelda(rKarTot, 6, "Balance Neto Período:", estiloTotalTexto);
        crearCelda(rKarTot, 7, (inv.getTotalUnidadesEntradaPeriodo() - inv.getTotalUnidadesSalidaPeriodo()) + " und.", estiloTotalCentro);

        autoAjustarColumnas(sheet, 12);
    }

    private void construirExcelGestionMedica(
            Workbook workbook, ReporteGestionMedicaRS med, String rolUsuario,
            CellStyle estiloInst, CellStyle estiloSubtitulo, CellStyle estiloMetaLabel, CellStyle estiloHeaderTabla,
            CellStyle estiloCelda, CellStyle estiloCeldaNum, CellStyle estiloCeldaCentro,
            CellStyle estiloTotalTexto, CellStyle estiloTotalNum, CellStyle estiloTotalCentro) {
        Sheet sheet = workbook.createSheet("4.3 Gestión Médica");

        int rowIdx = crearEncabezadoInstitucionalExcel(
                sheet, "4.3 REPORTE DE GESTIÓN MÉDICA Y ATENCIÓN DE PACIENTES",
                med.getRangoFechas(), med.getFechaGeneracion(), rolUsuario,
                "Indicadores de citas, cumplimiento por veterinario/especialidad y mascotas atendidas", 6,
                estiloInst, estiloSubtitulo, estiloMetaLabel);

        // 1. Consultas realizadas por Veterinario
        rowIdx++;
        Row rSec1 = sheet.createRow(rowIdx++);
        Cell cSec1 = rSec1.createCell(0);
        cSec1.setCellValue("1. CONSULTAS REALIZADAS POR VETERINARIO");
        cSec1.setCellStyle(estiloMetaLabel);

        String[] headersVet = {"Veterinario", "Especialidad", "Total Citas", "Citas Atendidas", "Citas Pendientes", "Citas Canceladas", "% Cumplimiento"};
        Row rVetH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersVet.length; i++) {
            Cell c = rVetH.createCell(i);
            c.setCellValue(headersVet[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        for (ItemGestionMedicaAgrupado iv : med.getConsultasPorVeterinario()) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, iv.getNombre(), estiloCelda);
            crearCelda(row, 1, iv.getSubcategoria() != null ? iv.getSubcategoria() : "General", estiloCelda);
            crearCelda(row, 2, String.valueOf(iv.getTotalCitas()), estiloCeldaCentro);
            crearCelda(row, 3, String.valueOf(iv.getCitasAtendidas()), estiloCeldaCentro);
            crearCelda(row, 4, String.valueOf(iv.getCitasPendientes()), estiloCeldaCentro);
            crearCelda(row, 5, String.valueOf(iv.getCitasCanceladas()), estiloCeldaCentro);
            crearCelda(row, 6, iv.getPorcentajeCumplimiento() + "%", estiloCeldaCentro);
        }

        BigDecimal pctGlobal = med.getTotalCitasPeriodo() > 0
                ? BigDecimal.valueOf(med.getCitasAtendidas()).multiply(CIEN)
                        .divide(BigDecimal.valueOf(med.getTotalCitasPeriodo()), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        Row rVetTot = sheet.createRow(rowIdx++);
        crearCelda(rVetTot, 0, "TOTALES CALCULADOS", estiloTotalTexto);
        crearCelda(rVetTot, 1, med.getConsultasPorVeterinario().size() + " veterinario(s)", estiloTotalCentro);
        crearCelda(rVetTot, 2, String.valueOf(med.getTotalCitasPeriodo()), estiloTotalCentro);
        crearCelda(rVetTot, 3, String.valueOf(med.getCitasAtendidas()), estiloTotalCentro);
        crearCelda(rVetTot, 4, String.valueOf(med.getCitasPendientesTotal()), estiloTotalCentro);
        crearCelda(rVetTot, 5, String.valueOf(med.getCitasCanceladas()), estiloTotalCentro);
        crearCelda(rVetTot, 6, pctGlobal + "%", estiloTotalCentro);

        // 2. Mascotas Atendidas en el Período
        rowIdx++;
        Row rSec2 = sheet.createRow(rowIdx++);
        Cell cSec2 = rSec2.createCell(0);
        cSec2.setCellValue("2. MASCOTAS ATENDIDAS EN EL PERÍODO");
        cSec2.setCellStyle(estiloMetaLabel);

        String[] headersMas = {"Mascota", "Raza / Edad", "Propietario", "Documento / Teléfono", "Consultas Atendidas", "Última Atención", "Último Veterinario"};
        Row rMasH = sheet.createRow(rowIdx++);
        for (int i = 0; i < headersMas.length; i++) {
            Cell c = rMasH.createCell(i);
            c.setCellValue(headersMas[i]);
            c.setCellStyle(estiloHeaderTabla);
        }

        int totalAtencionesMascotas = 0;
        for (ItemMascotaAtendida ma : med.getMascotasAtendidasPeriodo()) {
            Row row = sheet.createRow(rowIdx++);
            crearCelda(row, 0, ma.getNombreMascota(), estiloCelda);
            crearCelda(row, 1, ma.getRaza() + " (" + (ma.getEdad() != null ? ma.getEdad() : 0) + " años)", estiloCelda);
            crearCelda(row, 2, ma.getPropietario(), estiloCelda);
            crearCelda(row, 3, ma.getDocumentoPropietario() + " | " + ma.getTelefonoPropietario(), estiloCelda);
            crearCelda(row, 4, String.valueOf(ma.getConsultasAtendidas()), estiloCeldaCentro);
            crearCelda(row, 5, ma.getUltimaFechaAtencion() != null ? ma.getUltimaFechaAtencion().format(FMT_FECHA_HORA) : "-", estiloCeldaCentro);
            crearCelda(row, 6, ma.getUltimoVeterinario(), estiloCelda);
            totalAtencionesMascotas += ma.getConsultasAtendidas();
        }

        Row rMasTot = sheet.createRow(rowIdx);
        crearCelda(rMasTot, 0, "TOTALES PACIENTES", estiloTotalTexto);
        crearCelda(rMasTot, 1, med.getTotalMascotasAtendidas() + " mascota(s) única(s)", estiloTotalCentro);
        crearCelda(rMasTot, 2, "-", estiloTotalCentro);
        crearCelda(rMasTot, 3, "Total Consultas Efectivas:", estiloTotalTexto);
        crearCelda(rMasTot, 4, String.valueOf(totalAtencionesMascotas), estiloTotalCentro);
        crearCelda(rMasTot, 5, "-", estiloTotalCentro);
        crearCelda(rMasTot, 6, "-", estiloTotalCentro);

        autoAjustarColumnas(sheet, 7);
    }

    private int crearEncabezadoInstitucionalExcel(
            Sheet sheet, String tituloReporte, String rangoFechas, LocalDateTime fechaGen,
            String rolUsuario, String detalleFiltros, int ultimaColumna,
            CellStyle estiloInst, CellStyle estiloSubtitulo, CellStyle estiloMetaLabel) {
        Row r0 = sheet.createRow(0);
        Cell c0 = r0.createCell(0);
        c0.setCellValue(ENCABEZADO_INSTITUCIONAL);
        c0.setCellStyle(estiloInst);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, ultimaColumna));

        Row r1 = sheet.createRow(1);
        Cell c1 = r1.createCell(0);
        c1.setCellValue(tituloReporte);
        c1.setCellStyle(estiloSubtitulo);
        sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, ultimaColumna));

        Row r2 = sheet.createRow(2);
        Cell c2L = r2.createCell(0);
        c2L.setCellValue("Fecha de generación:");
        c2L.setCellStyle(estiloMetaLabel);
        r2.createCell(1).setCellValue((fechaGen != null ? fechaGen : LocalDateTime.now()).format(FMT_FECHA_HORA));

        Cell c2R = r2.createCell(3);
        c2R.setCellValue("Rango de fechas:");
        c2R.setCellStyle(estiloMetaLabel);
        r2.createCell(4).setCellValue(rangoFechas);

        Row r3 = sheet.createRow(3);
        Cell c3L = r3.createCell(0);
        c3L.setCellValue("Generado por rol:");
        c3L.setCellStyle(estiloMetaLabel);
        r3.createCell(1).setCellValue(rolUsuario);

        Cell c3R = r3.createCell(3);
        c3R.setCellValue("Parámetros / Filtros:");
        c3R.setCellStyle(estiloMetaLabel);
        r3.createCell(4).setCellValue(detalleFiltros);

        return 4;
    }

    private CellStyle crearEstiloTituloExcel(Workbook wb, short colorFondo, short tamFuente) {
        CellStyle st = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setFontHeightInPoints(tamFuente);
        font.setColor(IndexedColors.WHITE.getIndex());
        st.setFont(font);
        st.setFillForegroundColor(colorFondo);
        st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        st.setAlignment(HorizontalAlignment.CENTER);
        return st;
    }

    private CellStyle crearEstiloNegritaExcel(Workbook wb, boolean centrar) {
        CellStyle st = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        st.setFont(font);
        if (centrar) {
            st.setAlignment(HorizontalAlignment.CENTER);
        }
        return st;
    }

    private CellStyle crearEstiloEncabezadoTablaExcel(Workbook wb) {
        CellStyle st = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        st.setFont(font);
        st.setFillForegroundColor(IndexedColors.SEA_GREEN.getIndex());
        st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        st.setAlignment(HorizontalAlignment.CENTER);
        aplicarBordesExcel(st);
        return st;
    }

    private CellStyle crearEstiloCeldaExcel(Workbook wb, HorizontalAlignment align) {
        CellStyle st = wb.createCellStyle();
        st.setAlignment(align);
        aplicarBordesExcel(st);
        return st;
    }

    private CellStyle crearEstiloTotalExcel(Workbook wb, HorizontalAlignment align) {
        CellStyle st = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        st.setFont(font);
        st.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        st.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        st.setAlignment(align);
        aplicarBordesExcel(st);
        return st;
    }

    private void aplicarBordesExcel(CellStyle st) {
        st.setBorderTop(BorderStyle.THIN);
        st.setBorderBottom(BorderStyle.THIN);
        st.setBorderLeft(BorderStyle.THIN);
        st.setBorderRight(BorderStyle.THIN);
    }

    private void crearCelda(Row row, int col, String val, CellStyle st) {
        Cell c = row.createCell(col);
        c.setCellValue(val != null ? val : "");
        c.setCellStyle(st);
    }

    private void autoAjustarColumnas(Sheet sheet, int totalCols) {
        for (int i = 0; i < totalCols; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // -------------------------------------------------------------------------
    // EXPORTACIÓN A PDF (.PDF) CON OPENPDF
    // -------------------------------------------------------------------------

    @Override
    public byte[] exportarReportePdf(
            String tipoReporte, LocalDate fechaInicial, LocalDate fechaFinal,
            Long medicoId, Long especializacionId, Long medicamentoId,
            Integer diasProximoVencer, String agrupacion, String rol) {
        String tipoNorm = normalizarTipoReporte(tipoReporte);
        validarPermisoReporte(rol, tipoNorm);
        String rolUsuario = rolEfectivo(rol);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 28, 28, 28, 28);
            PdfWriter.getInstance(doc, out);
            doc.open();

            switch (tipoNorm) {
                case Constantes.REPORTE_FINANCIERO -> {
                    ReporteFinancieroRS fin = generarReporteFinanciero(fechaInicial, fechaFinal, medicoId, especializacionId);
                    construirPdfFinanciero(doc, fin, agrupacion, rolUsuario);
                }
                case Constantes.REPORTE_INVENTARIO -> {
                    ReporteInventarioRS inv = generarReporteInventario(fechaInicial, fechaFinal, medicamentoId, diasProximoVencer);
                    construirPdfInventario(doc, inv, rolUsuario);
                }
                case Constantes.REPORTE_MEDICO -> {
                    ReporteGestionMedicaRS med = generarReporteGestionMedica(fechaInicial, fechaFinal, medicoId, especializacionId);
                    construirPdfGestionMedica(doc, med, rolUsuario);
                }
            }

            doc.close();
            return out.toByteArray();
        } catch (BadRequestException e) {
            throw e;
        } catch (Exception e) {
            throw new BadRequestException("No fue posible generar el documento PDF del reporte: " + e.getMessage());
        }
    }

    private void construirPdfFinanciero(Document doc, ReporteFinancieroRS fin, String agrupacion, String rolUsuario) throws Exception {
        String agr = (agrupacion != null && !agrupacion.isBlank()) ? agrupacion.trim().toUpperCase() : "MES";
        agregarEncabezadoInstitucionalPdf(
                doc, "4.1 REPORTE FINANCIERO Y CONTABLE",
                fin.getRangoFechas(), fin.getFechaGeneracion(), rolUsuario,
                "Agrupación contable: " + agr);

        // 1. Resumen Ejecutivo de Ingresos y Estado de Facturas
        agregarSubtituloSeccionPdf(doc, "1. RESUMEN GENERAL DE INGRESOS Y ESTADO DE FACTURAS");
        PdfPTable tblKpi = new PdfPTable(6);
        tblKpi.setWidthPercentage(100);
        tblKpi.setSpacingAfter(10f);
        String[] hKpi = {"Total Facturas", "Facturas Pagadas", "Pagos Parciales", "Facturas Pendientes", "Ingresos Consultas", "Ingresos Medicamentos"};
        for (String h : hKpi) {
            tblKpi.addCell(celdaHeaderPdf(h));
        }
        tblKpi.addCell(celdaDatoPdf(String.valueOf(fin.getTotalFacturasGeneradas()), Element.ALIGN_CENTER));
        tblKpi.addCell(celdaDatoPdf(fin.getFacturasPagadas() + " (" + fmtMoneda(fin.getMontoFacturasPagadas()) + ")", Element.ALIGN_CENTER));
        tblKpi.addCell(celdaDatoPdf(fin.getPagosParciales() + " (" + fmtMoneda(fin.getMontoPagosParciales()) + ")", Element.ALIGN_CENTER));
        tblKpi.addCell(celdaDatoPdf(fin.getFacturasPendientes() + " (" + fmtMoneda(fin.getMontoFacturasPendientes()) + ")", Element.ALIGN_CENTER));
        tblKpi.addCell(celdaDatoPdf(fmtMoneda(fin.getIngresosConsultasServicios()), Element.ALIGN_RIGHT));
        tblKpi.addCell(celdaDatoPdf(fmtMoneda(fin.getIngresosVentaMedicamentos()), Element.ALIGN_RIGHT));

        tblKpi.addCell(celdaTotalPdf("TOTALES DEL PERÍODO", Element.ALIGN_LEFT));
        tblKpi.addCell(celdaTotalPdf("Descuentos: " + fmtMoneda(fin.getDescuentosAplicados()), Element.ALIGN_CENTER));
        tblKpi.addCell(celdaTotalPdf("Impuestos Rec.: " + fmtMoneda(fin.getImpuestosRecaudados()), Element.ALIGN_CENTER));
        tblKpi.addCell(celdaTotalPdf("Saldo Pend.: " + fmtMoneda(fin.getSaldoPendienteTotal()), Element.ALIGN_CENTER));
        tblKpi.addCell(celdaTotalPdf("Total Facturado: " + fmtMoneda(fin.getIngresosTotalesFacturados()), Element.ALIGN_RIGHT));
        tblKpi.addCell(celdaTotalPdf("Total Recaudado: " + fmtMoneda(fin.getIngresosRecaudados()), Element.ALIGN_RIGHT));
        doc.add(tblKpi);

        // 2. Tabla de Resumen por Período
        agregarSubtituloSeccionPdf(doc, "2. RESUMEN CONTABLE POR PERÍODO (" + agr + ")");
        PdfPTable tblPer = new PdfPTable(new float[]{2f, 1.2f, 1.8f, 1.8f, 1.5f, 1.5f, 2f, 2f});
        tblPer.setWidthPercentage(100);
        tblPer.setSpacingAfter(10f);
        String[] hPer = {"Período (" + agr + ")", "Facturas", "Consultas", "Medicamentos", "Descuentos", "Impuestos", "Total Facturado", "Total Recaudado"};
        for (String h : hPer) {
            tblPer.addCell(celdaHeaderPdf(h));
        }

        List<ResumenPeriodoFinanciero> listaPeriodo = switch (agr) {
            case "DIA" -> fin.getResumenPorDia();
            case "ANIO" -> fin.getResumenPorAnio();
            default -> fin.getResumenPorMes();
        };

        int totFact = 0;
        BigDecimal totSrv = BigDecimal.ZERO;
        BigDecimal totMed = BigDecimal.ZERO;
        BigDecimal totDesc = BigDecimal.ZERO;
        BigDecimal totImp = BigDecimal.ZERO;
        BigDecimal totFac = BigDecimal.ZERO;
        BigDecimal totRec = BigDecimal.ZERO;

        for (ResumenPeriodoFinanciero r : listaPeriodo) {
            tblPer.addCell(celdaDatoPdf(r.getPeriodo(), Element.ALIGN_LEFT));
            tblPer.addCell(celdaDatoPdf(String.valueOf(r.getCantidadFacturas()), Element.ALIGN_CENTER));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getIngresosServicios()), Element.ALIGN_RIGHT));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getIngresosMedicamentos()), Element.ALIGN_RIGHT));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getDescuentos()), Element.ALIGN_RIGHT));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getImpuestos()), Element.ALIGN_RIGHT));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getTotalFacturado()), Element.ALIGN_RIGHT));
            tblPer.addCell(celdaDatoPdf(fmtMoneda(r.getTotalRecaudado()), Element.ALIGN_RIGHT));

            totFact += r.getCantidadFacturas();
            totSrv = totSrv.add(nvl(r.getIngresosServicios()));
            totMed = totMed.add(nvl(r.getIngresosMedicamentos()));
            totDesc = totDesc.add(nvl(r.getDescuentos()));
            totImp = totImp.add(nvl(r.getImpuestos()));
            totFac = totFac.add(nvl(r.getTotalFacturado()));
            totRec = totRec.add(nvl(r.getTotalRecaudado()));
        }

        tblPer.addCell(celdaTotalPdf("TOTALES CALCULADOS", Element.ALIGN_LEFT));
        tblPer.addCell(celdaTotalPdf(String.valueOf(totFact), Element.ALIGN_CENTER));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totSrv), Element.ALIGN_RIGHT));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totMed), Element.ALIGN_RIGHT));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totDesc), Element.ALIGN_RIGHT));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totImp), Element.ALIGN_RIGHT));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totFac), Element.ALIGN_RIGHT));
        tblPer.addCell(celdaTotalPdf(fmtMoneda(totRec), Element.ALIGN_RIGHT));
        doc.add(tblPer);

        // 3. Tabla de Ingresos por Veterinario y Especialidad
        agregarSubtituloSeccionPdf(doc, "3. INGRESOS POR VETERINARIO Y ESPECIALIDAD");
        PdfPTable tblVet = new PdfPTable(new float[]{2.5f, 2f, 1.1f, 1.8f, 1.8f, 2f, 2f});
        tblVet.setWidthPercentage(100);
        String[] hVet = {"Veterinario", "Especialidad", "Facturas", "Consultas", "Medicamentos", "Total Facturado", "Total Recaudado"};
        for (String h : hVet) {
            tblVet.addCell(celdaHeaderPdf(h));
        }

        for (ItemFinancieroAgrupado iv : fin.getIngresosPorVeterinario()) {
            tblVet.addCell(celdaDatoPdf(iv.getCategoria(), Element.ALIGN_LEFT));
            tblVet.addCell(celdaDatoPdf(iv.getSubcategoria() != null ? iv.getSubcategoria() : "General", Element.ALIGN_LEFT));
            tblVet.addCell(celdaDatoPdf(String.valueOf(iv.getCantidadFacturas()), Element.ALIGN_CENTER));
            tblVet.addCell(celdaDatoPdf(fmtMoneda(iv.getIngresosServicios()), Element.ALIGN_RIGHT));
            tblVet.addCell(celdaDatoPdf(fmtMoneda(iv.getIngresosMedicamentos()), Element.ALIGN_RIGHT));
            tblVet.addCell(celdaDatoPdf(fmtMoneda(iv.getTotalFacturado()), Element.ALIGN_RIGHT));
            tblVet.addCell(celdaDatoPdf(fmtMoneda(iv.getTotalRecaudado()), Element.ALIGN_RIGHT));
        }

        tblVet.addCell(celdaTotalPdf("TOTALES CALCULADOS", Element.ALIGN_LEFT));
        tblVet.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(String.valueOf(totFact), Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(fmtMoneda(totSrv), Element.ALIGN_RIGHT));
        tblVet.addCell(celdaTotalPdf(fmtMoneda(totMed), Element.ALIGN_RIGHT));
        tblVet.addCell(celdaTotalPdf(fmtMoneda(totFac), Element.ALIGN_RIGHT));
        tblVet.addCell(celdaTotalPdf(fmtMoneda(totRec), Element.ALIGN_RIGHT));
        doc.add(tblVet);
    }

    private void construirPdfInventario(Document doc, ReporteInventarioRS inv, String rolUsuario) throws Exception {
        agregarEncabezadoInstitucionalPdf(
                doc, "4.2 REPORTE DE VALORACIÓN DE INVENTARIO, ALERTAS Y KARDEX",
                inv.getRangoFechas(), inv.getFechaGeneracion(), rolUsuario,
                "Umbral próximo a vencer: " + inv.getDiasUmbralProximoVencer() + " días");

        // 1. Tabla de Existencias Valorizadas
        agregarSubtituloSeccionPdf(doc, "1. EXISTENCIAS ACTUALES Y VALORACIÓN (COSTO VS. VENTA)");
        PdfPTable tblEx = new PdfPTable(new float[]{1.2f, 2.6f, 1.4f, 1.4f, 1.1f, 1.5f, 1.5f, 1.7f, 1.7f, 1.5f});
        tblEx.setWidthPercentage(100);
        tblEx.setSpacingAfter(10f);
        String[] hEx = {"Código", "Medicamento", "Lote", "Vence", "Stock", "Costo Unit.", "Venta Unit.", "Costo Total", "Venta Potencial", "Alerta"};
        for (String h : hEx) {
            tblEx.addCell(celdaHeaderPdf(h));
        }

        for (ItemExistenciaValorizada item : inv.getExistenciasActuales()) {
            tblEx.addCell(celdaDatoPdf(item.getCodigo(), Element.ALIGN_LEFT));
            tblEx.addCell(celdaDatoPdf(item.getNombre(), Element.ALIGN_LEFT));
            tblEx.addCell(celdaDatoPdf(item.getLote() != null ? item.getLote() : "-", Element.ALIGN_CENTER));
            tblEx.addCell(celdaDatoPdf(item.getFechaVence() != null ? item.getFechaVence().toString() : "-", Element.ALIGN_CENTER));
            tblEx.addCell(celdaDatoPdf(item.getCantidadDisponible() + " / " + item.getStockMinimo(), Element.ALIGN_CENTER));
            tblEx.addCell(celdaDatoPdf(fmtMoneda(item.getPrecioCompra()), Element.ALIGN_RIGHT));
            tblEx.addCell(celdaDatoPdf(fmtMoneda(item.getPrecioVenta()), Element.ALIGN_RIGHT));
            tblEx.addCell(celdaDatoPdf(fmtMoneda(item.getValorCostoTotal()), Element.ALIGN_RIGHT));
            tblEx.addCell(celdaDatoPdf(fmtMoneda(item.getValorVentaTotal()), Element.ALIGN_RIGHT));
            tblEx.addCell(celdaDatoPdf(item.getEstadoAlerta(), Element.ALIGN_CENTER));
        }

        tblEx.addCell(celdaTotalPdf("TOTALES", Element.ALIGN_LEFT));
        tblEx.addCell(celdaTotalPdf(inv.getTotalProductos() + " producto(s)", Element.ALIGN_LEFT));
        tblEx.addCell(celdaTotalPdf("Agot: " + inv.getCantidadAgotados(), Element.ALIGN_CENTER));
        tblEx.addCell(celdaTotalPdf("Venc: " + inv.getCantidadVencidos(), Element.ALIGN_CENTER));
        tblEx.addCell(celdaTotalPdf(inv.getTotalUnidadesExistencia() + " und.", Element.ALIGN_CENTER));
        tblEx.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        tblEx.addCell(celdaTotalPdf("TOTAL:", Element.ALIGN_RIGHT));
        tblEx.addCell(celdaTotalPdf(fmtMoneda(inv.getValorInventarioCostoAdquisicion()), Element.ALIGN_RIGHT));
        tblEx.addCell(celdaTotalPdf(fmtMoneda(inv.getValorPotencialVenta()), Element.ALIGN_RIGHT));
        tblEx.addCell(celdaTotalPdf("Margen: " + fmtMoneda(inv.getMargenPotencialEstimado()), Element.ALIGN_RIGHT));
        doc.add(tblEx);

        // 2. Kardex de Movimientos
        agregarSubtituloSeccionPdf(doc, "2. HISTORIAL DE MOVIMIENTOS DE INVENTARIO (KARDEX)");
        PdfPTable tblKar = new PdfPTable(new float[]{1.8f, 1.1f, 2.5f, 1.2f, 1.1f, 3.2f, 1.4f});
        tblKar.setWidthPercentage(100);
        String[] hKar = {"Fecha y Hora", "Tipo", "Medicamento", "Lote", "Cantidad", "Motivo / Operación", "Usuario"};
        for (String h : hKar) {
            tblKar.addCell(celdaHeaderPdf(h));
        }

        for (ItemMovimientoKardex mov : inv.getMovimientosInventario()) {
            tblKar.addCell(celdaDatoPdf(mov.getFechaRegistro() != null ? mov.getFechaRegistro().format(FMT_FECHA_HORA) : "-", Element.ALIGN_CENTER));
            tblKar.addCell(celdaDatoPdf(mov.getTipo(), Element.ALIGN_CENTER));
            tblKar.addCell(celdaDatoPdf(mov.getCodigoMedicamento() + " - " + mov.getNombreMedicamento(), Element.ALIGN_LEFT));
            tblKar.addCell(celdaDatoPdf(mov.getLote() != null ? mov.getLote() : "-", Element.ALIGN_CENTER));
            tblKar.addCell(celdaDatoPdf(("ENTRADA".equalsIgnoreCase(mov.getTipo()) ? "+" : "-") + mov.getCantidad(), Element.ALIGN_CENTER));
            tblKar.addCell(celdaDatoPdf(mov.getMotivo() != null ? mov.getMotivo() : "-", Element.ALIGN_LEFT));
            tblKar.addCell(celdaDatoPdf(mov.getUsuarioRegistro(), Element.ALIGN_CENTER));
        }

        tblKar.addCell(celdaTotalPdf("TOTALES KARDEX", Element.ALIGN_LEFT));
        tblKar.addCell(celdaTotalPdf(inv.getTotalMovimientosPeriodo() + " mov(s)", Element.ALIGN_CENTER));
        tblKar.addCell(celdaTotalPdf("Total Entradas: +" + inv.getTotalUnidadesEntradaPeriodo() + " und.", Element.ALIGN_LEFT));
        tblKar.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        tblKar.addCell(celdaTotalPdf("-" + inv.getTotalUnidadesSalidaPeriodo() + " sal.", Element.ALIGN_CENTER));
        tblKar.addCell(celdaTotalPdf("Salidas Totales del Período: " + inv.getTotalUnidadesSalidaPeriodo() + " unidades", Element.ALIGN_LEFT));
        tblKar.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        doc.add(tblKar);
    }

    private void construirPdfGestionMedica(Document doc, ReporteGestionMedicaRS med, String rolUsuario) throws Exception {
        agregarEncabezadoInstitucionalPdf(
                doc, "4.3 REPORTE DE GESTIÓN MÉDICA Y ATENCIÓN DE PACIENTES",
                med.getRangoFechas(), med.getFechaGeneracion(), rolUsuario,
                "Indicadores de citas por estado, productividad médica y mascotas atendidas");

        // 1. Consultas por Veterinario
        agregarSubtituloSeccionPdf(doc, "1. CONSULTAS REALIZADAS POR VETERINARIO Y ESPECIALIDAD");
        PdfPTable tblVet = new PdfPTable(new float[]{2.8f, 2.2f, 1.3f, 1.3f, 1.3f, 1.3f, 1.4f});
        tblVet.setWidthPercentage(100);
        tblVet.setSpacingAfter(10f);
        String[] hVet = {"Veterinario", "Especialidad", "Total Citas", "Atendidas", "Pendientes", "Canceladas", "% Cumplimiento"};
        for (String h : hVet) {
            tblVet.addCell(celdaHeaderPdf(h));
        }

        for (ItemGestionMedicaAgrupado iv : med.getConsultasPorVeterinario()) {
            tblVet.addCell(celdaDatoPdf(iv.getNombre(), Element.ALIGN_LEFT));
            tblVet.addCell(celdaDatoPdf(iv.getSubcategoria() != null ? iv.getSubcategoria() : "General", Element.ALIGN_LEFT));
            tblVet.addCell(celdaDatoPdf(String.valueOf(iv.getTotalCitas()), Element.ALIGN_CENTER));
            tblVet.addCell(celdaDatoPdf(String.valueOf(iv.getCitasAtendidas()), Element.ALIGN_CENTER));
            tblVet.addCell(celdaDatoPdf(String.valueOf(iv.getCitasPendientes()), Element.ALIGN_CENTER));
            tblVet.addCell(celdaDatoPdf(String.valueOf(iv.getCitasCanceladas()), Element.ALIGN_CENTER));
            tblVet.addCell(celdaDatoPdf(iv.getPorcentajeCumplimiento() + "%", Element.ALIGN_CENTER));
        }

        BigDecimal pctGlobal = med.getTotalCitasPeriodo() > 0
                ? BigDecimal.valueOf(med.getCitasAtendidas()).multiply(CIEN)
                        .divide(BigDecimal.valueOf(med.getTotalCitasPeriodo()), 1, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        tblVet.addCell(celdaTotalPdf("TOTALES CALCULADOS", Element.ALIGN_LEFT));
        tblVet.addCell(celdaTotalPdf(med.getConsultasPorVeterinario().size() + " veterinario(s)", Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(String.valueOf(med.getTotalCitasPeriodo()), Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(String.valueOf(med.getCitasAtendidas()), Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(String.valueOf(med.getCitasPendientesTotal()), Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(String.valueOf(med.getCitasCanceladas()), Element.ALIGN_CENTER));
        tblVet.addCell(celdaTotalPdf(pctGlobal + "%", Element.ALIGN_CENTER));
        doc.add(tblVet);

        // 2. Mascotas Atendidas
        agregarSubtituloSeccionPdf(doc, "2. MASCOTAS ATENDIDAS EN EL PERÍODO");
        PdfPTable tblMas = new PdfPTable(new float[]{1.8f, 1.8f, 2.2f, 2.2f, 1.2f, 1.8f, 2.2f});
        tblMas.setWidthPercentage(100);
        String[] hMas = {"Mascota", "Raza / Edad", "Propietario", "Documento / Teléfono", "Consultas", "Última Atención", "Último Veterinario"};
        for (String h : hMas) {
            tblMas.addCell(celdaHeaderPdf(h));
        }

        int totalAtenciones = 0;
        for (ItemMascotaAtendida ma : med.getMascotasAtendidasPeriodo()) {
            tblMas.addCell(celdaDatoPdf(ma.getNombreMascota(), Element.ALIGN_LEFT));
            tblMas.addCell(celdaDatoPdf(ma.getRaza() + " (" + (ma.getEdad() != null ? ma.getEdad() : 0) + " a.)", Element.ALIGN_LEFT));
            tblMas.addCell(celdaDatoPdf(ma.getPropietario(), Element.ALIGN_LEFT));
            tblMas.addCell(celdaDatoPdf(ma.getDocumentoPropietario() + " | " + ma.getTelefonoPropietario(), Element.ALIGN_LEFT));
            tblMas.addCell(celdaDatoPdf(String.valueOf(ma.getConsultasAtendidas()), Element.ALIGN_CENTER));
            tblMas.addCell(celdaDatoPdf(ma.getUltimaFechaAtencion() != null ? ma.getUltimaFechaAtencion().format(FMT_FECHA_HORA) : "-", Element.ALIGN_CENTER));
            tblMas.addCell(celdaDatoPdf(ma.getUltimoVeterinario(), Element.ALIGN_LEFT));
            totalAtenciones += ma.getConsultasAtendidas();
        }

        tblMas.addCell(celdaTotalPdf("TOTALES PACIENTES", Element.ALIGN_LEFT));
        tblMas.addCell(celdaTotalPdf(med.getTotalMascotasAtendidas() + " mascota(s)", Element.ALIGN_CENTER));
        tblMas.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        tblMas.addCell(celdaTotalPdf("Total Consultas Atendidas:", Element.ALIGN_RIGHT));
        tblMas.addCell(celdaTotalPdf(String.valueOf(totalAtenciones), Element.ALIGN_CENTER));
        tblMas.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        tblMas.addCell(celdaTotalPdf("-", Element.ALIGN_CENTER));
        doc.add(tblMas);
    }

    private void agregarEncabezadoInstitucionalPdf(
            Document doc, String tituloReporte, String rangoFechas,
            LocalDateTime fechaGen, String rolUsuario, String detalleFiltros) throws Exception {
        com.lowagie.text.Font fInst = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 13, com.lowagie.text.Font.BOLD, Color.WHITE);
        com.lowagie.text.Font fSub = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 11, com.lowagie.text.Font.BOLD, Color.WHITE);

        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);

        PdfPCell cInst = new PdfPCell(new Phrase(ENCABEZADO_INSTITUCIONAL, fInst));
        cInst.setBackgroundColor(new Color(15, 44, 89));
        cInst.setHorizontalAlignment(Element.ALIGN_CENTER);
        cInst.setPadding(6f);
        cInst.setBorder(PdfPCell.NO_BORDER);
        banner.addCell(cInst);

        PdfPCell cSub = new PdfPCell(new Phrase(tituloReporte, fSub));
        cSub.setBackgroundColor(new Color(29, 111, 66));
        cSub.setHorizontalAlignment(Element.ALIGN_CENTER);
        cSub.setPadding(5f);
        cSub.setBorder(PdfPCell.NO_BORDER);
        banner.addCell(cSub);
        doc.add(banner);

        PdfPTable meta = new PdfPTable(4);
        meta.setWidthPercentage(100);
        meta.setSpacingBefore(4f);
        meta.setSpacingAfter(8f);
        String fechaStr = (fechaGen != null ? fechaGen : LocalDateTime.now()).format(FMT_FECHA_HORA);
        meta.addCell(celdaMetaPdf("Fecha de generación: " + fechaStr));
        meta.addCell(celdaMetaPdf("Período consultado: " + rangoFechas));
        meta.addCell(celdaMetaPdf("Rol / Perfil ejecutor: " + rolUsuario));
        meta.addCell(celdaMetaPdf(detalleFiltros));
        doc.add(meta);
    }

    private void agregarSubtituloSeccionPdf(Document doc, String texto) throws Exception {
        com.lowagie.text.Font f = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 9, com.lowagie.text.Font.BOLD, new Color(15, 44, 89));
        Paragraph p = new Paragraph(texto, f);
        p.setSpacingBefore(4f);
        p.setSpacingAfter(4f);
        doc.add(p);
    }

    private PdfPCell celdaMetaPdf(String texto) {
        com.lowagie.text.Font f = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.BOLD, Color.DARK_GRAY);
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", f));
        c.setBackgroundColor(new Color(241, 245, 249));
        c.setPadding(4f);
        c.setBorderColor(new Color(203, 213, 225));
        return c;
    }

    private PdfPCell celdaHeaderPdf(String texto) {
        com.lowagie.text.Font f = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.BOLD, Color.WHITE);
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", f));
        c.setBackgroundColor(new Color(30, 64, 175));
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        c.setPadding(4f);
        return c;
    }

    private PdfPCell celdaDatoPdf(String texto, int align) {
        com.lowagie.text.Font f = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.NORMAL, Color.BLACK);
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", f));
        c.setHorizontalAlignment(align);
        c.setPadding(3.5f);
        c.setBorderColor(new Color(203, 213, 225));
        return c;
    }

    private PdfPCell celdaTotalPdf(String texto, int align) {
        com.lowagie.text.Font f = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 8, com.lowagie.text.Font.BOLD, new Color(15, 23, 42));
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "", f));
        c.setBackgroundColor(new Color(226, 232, 240));
        c.setHorizontalAlignment(align);
        c.setPadding(4f);
        return c;
    }
}
