package com.uniminuto.clinica.serviceimpl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
/** Implementa la lógica analítica para los reportes financieros (4.1), de inventario (4.2) y de gestión médica (4.3). */
public class ReporteServiceImpl implements ReporteService {

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
}
