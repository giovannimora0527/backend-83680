package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.models.ReporteFinancieroRS;
import com.uniminuto.clinica.models.ReporteGestionMedicaRS;
import com.uniminuto.clinica.models.ReporteInventarioRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {

    @Mock
    private FacturaRepository facturaRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @Mock
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;
    @Mock
    private CitaRepository citaRepository;

    private ReporteServiceImpl reporteService;
    private Medico medicoCirugia;
    private Cliente cliente;
    private Mascota mascota;

    @BeforeEach
    void setUp() {
        reporteService = new ReporteServiceImpl(
                facturaRepository, medicamentoRepository, movimientoMedicamentoRepository, citaRepository);

        Especializacion esp = new Especializacion();
        esp.setId(1L);
        esp.setNombre("Cirugía");

        medicoCirugia = new Medico();
        medicoCirugia.setId(5L);
        medicoCirugia.setNombres("Andrés");
        medicoCirugia.setApellidos("Pardo");
        medicoCirugia.setEspecializacion(esp);

        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombres("María");
        cliente.setApellidos("López");
        cliente.setTipoDocumento("CC");
        cliente.setNumeroDocumento("52100200");
        cliente.setTelefono("3105556677");

        Raza raza = new Raza();
        raza.setRazaId(1);
        raza.setNombre("Beagle");

        mascota = new Mascota();
        mascota.setMascotaId(10);
        mascota.setNombreMascota("Bruno");
        mascota.setEdad(4);
        mascota.setRaza(raza);
        mascota.setCliente(cliente);
    }

    @Test
    void generarReporteFinanciero_calculaIngresosPorCategoriaEstadoYPeriodos() {
        Factura fPagada = new Factura();
        fPagada.setId(1L);
        fPagada.setNumeroFactura("FAC-001");
        fPagada.setMedico(medicoCirugia);
        fPagada.setFechaGeneracion(LocalDateTime.of(2026, 11, 15, 10, 0));
        fPagada.setSubtotalServicios(new BigDecimal("85000.00"));
        fPagada.setSubtotalMedicamentos(new BigDecimal("15000.00"));
        fPagada.setSubtotal(new BigDecimal("100000.00"));
        fPagada.setValorDescuento(new BigDecimal("10000.00"));
        fPagada.setValorImpuesto(new BigDecimal("17100.00"));
        fPagada.setTotal(new BigDecimal("107100.00"));
        fPagada.setTotalPagado(new BigDecimal("107100.00"));
        fPagada.setSaldoPendiente(new BigDecimal("0.00"));
        fPagada.setEstado("PAGADA");

        Factura fParcial = new Factura();
        fParcial.setId(2L);
        fParcial.setNumeroFactura("FAC-002");
        fParcial.setMedico(medicoCirugia);
        fParcial.setFechaGeneracion(LocalDateTime.of(2026, 11, 18, 14, 30));
        fParcial.setSubtotalServicios(new BigDecimal("60000.00"));
        fParcial.setSubtotalMedicamentos(new BigDecimal("0.00"));
        fParcial.setSubtotal(new BigDecimal("60000.00"));
        fParcial.setValorDescuento(new BigDecimal("0.00"));
        fParcial.setValorImpuesto(new BigDecimal("0.00"));
        fParcial.setTotal(new BigDecimal("60000.00"));
        fParcial.setTotalPagado(new BigDecimal("20000.00"));
        fParcial.setSaldoPendiente(new BigDecimal("40000.00"));
        fParcial.setEstado("PARCIAL");

        when(facturaRepository.findAllByOrderByFechaGeneracionDesc()).thenReturn(List.of(fParcial, fPagada));

        ReporteFinancieroRS rs = reporteService.generarReporteFinanciero(
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), null, null);

        assertEquals(2, rs.getTotalFacturasGeneradas());
        assertEquals(1, rs.getFacturasPagadas());
        assertEquals(1, rs.getPagosParciales());
        assertEquals(new BigDecimal("145000.00"), rs.getIngresosConsultasServicios());
        assertEquals(new BigDecimal("15000.00"), rs.getIngresosVentaMedicamentos());
        assertEquals(new BigDecimal("167100.00"), rs.getIngresosTotalesFacturados());
        assertEquals(new BigDecimal("127100.00"), rs.getIngresosRecaudados());
        assertEquals(new BigDecimal("17100.00"), rs.getImpuestosRecaudados());
        assertEquals(new BigDecimal("10000.00"), rs.getDescuentosAplicados());
        assertEquals(1, rs.getIngresosPorEspecialidad().size());
        assertEquals(1, rs.getIngresosPorVeterinario().size());
        assertEquals(2, rs.getResumenPorDia().size());
        assertEquals(1, rs.getResumenPorMes().size());
        assertEquals(1, rs.getResumenPorAnio().size());
    }

    @Test
    void generarReporteInventario_calculaValoracionCostoVsVentaAlertasYKardex() {
        LocalDate hoy = LocalDate.now();

        Medicamento medNormal = new Medicamento();
        medNormal.setId(1L);
        medNormal.setCodigo("MED00001");
        medNormal.setNombre("Vacuna Antirrábica");
        medNormal.setCantidadDisponible(20);
        medNormal.setStockMinimo(5);
        medNormal.setPrecioCompra(new BigDecimal("10000.00"));
        medNormal.setPrecioVenta(new BigDecimal("18000.00"));
        medNormal.setFechaVence(hoy.plusMonths(6));
        medNormal.setActivo(true);

        Medicamento medAgotado = new Medicamento();
        medAgotado.setId(2L);
        medAgotado.setCodigo("MED00002");
        medAgotado.setNombre("Antibiótico Agotado");
        medAgotado.setCantidadDisponible(0);
        medAgotado.setStockMinimo(3);
        medAgotado.setPrecioCompra(new BigDecimal("5000.00"));
        medAgotado.setPrecioVenta(new BigDecimal("9000.00"));
        medAgotado.setFechaVence(hoy.plusMonths(4));
        medAgotado.setActivo(true);

        Medicamento medVencido = new Medicamento();
        medVencido.setId(3L);
        medVencido.setCodigo("MED00003");
        medVencido.setNombre("Colirio Vencido");
        medVencido.setCantidadDisponible(4);
        medVencido.setStockMinimo(2);
        medVencido.setPrecioCompra(new BigDecimal("8000.00"));
        medVencido.setPrecioVenta(new BigDecimal("12000.00"));
        medVencido.setFechaVence(hoy.minusDays(5));
        medVencido.setActivo(true);

        MovimientoMedicamento movSalida = new MovimientoMedicamento();
        movSalida.setId(10L);
        movSalida.setMedicamentoId(1L);
        movSalida.setTipo("SALIDA");
        movSalida.setCantidad(5);
        movSalida.setMotivo("DISPENSACION FORMULA #1");
        movSalida.setReferenciaTipo("FORMULA");
        movSalida.setReferenciaId(1L);
        movSalida.setUsuarioRegistro("Dr. Pardo");
        movSalida.setFechaRegistro(LocalDateTime.now());

        when(medicamentoRepository.findAllByOrderByFechaVenceAsc())
                .thenReturn(List.of(medVencido, medAgotado, medNormal));
        when(movimientoMedicamentoRepository.findAll()).thenReturn(List.of(movSalida));

        ReporteInventarioRS rs = reporteService.generarReporteInventario(null, null, null, 30);

        assertEquals(3, rs.getTotalProductos());
        assertEquals(24, rs.getTotalUnidadesExistencia());
        // Costo: (20 * 10,000) + (0 * 5,000) + (4 * 8,000) = 232,000
        assertEquals(new BigDecimal("232000.00"), rs.getValorInventarioCostoAdquisicion());
        // Venta: (20 * 18,000) + (0 * 9,000) + (4 * 12,000) = 408,000
        assertEquals(new BigDecimal("408000.00"), rs.getValorPotencialVenta());
        assertEquals(1, rs.getCantidadAgotados());
        assertEquals(1, rs.getCantidadVencidos());
        assertTrue(rs.getTotalUnidadesSalidaPeriodo() >= 5);
        assertTrue(rs.getTotalUnidadesEntradaPeriodo() > 0);
    }

    @Test
    void generarReporteGestionMedica_agrupaCitasPorEstadoVeterinarioEspecialidadYMascotas() {
        Cita cAtendida = new Cita();
        cAtendida.setId(1L);
        cAtendida.setCliente(cliente);
        cAtendida.setMascota(mascota);
        cAtendida.setMedico(medicoCirugia);
        cAtendida.setFechaHora(LocalDateTime.of(2026, 11, 10, 9, 0));
        cAtendida.setEstado("ATENDIDA");
        cAtendida.setMotivo("Control quirúrgico");

        Cita cPendiente = new Cita();
        cPendiente.setId(2L);
        cPendiente.setCliente(cliente);
        cPendiente.setMascota(mascota);
        cPendiente.setMedico(medicoCirugia);
        cPendiente.setFechaHora(LocalDateTime.of(2026, 11, 20, 11, 0));
        cPendiente.setEstado("CONFIRMADA");
        cPendiente.setMotivo("Retiro de puntos");

        Cita cCancelada = new Cita();
        cCancelada.setId(3L);
        cCancelada.setCliente(cliente);
        cCancelada.setMascota(mascota);
        cCancelada.setMedico(medicoCirugia);
        cCancelada.setFechaHora(LocalDateTime.of(2026, 11, 12, 15, 0));
        cCancelada.setEstado("CANCELADA");
        cCancelada.setMotivo("No pudo asistir");

        when(citaRepository.findAllByOrderByFechaHoraDesc()).thenReturn(List.of(cPendiente, cCancelada, cAtendida));

        ReporteGestionMedicaRS rs = reporteService.generarReporteGestionMedica(
                LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 30), null, null);

        assertEquals(3, rs.getTotalCitasPeriodo());
        assertEquals(1, rs.getCitasAtendidas());
        assertEquals(1, rs.getCitasPendientesTotal());
        assertEquals(1, rs.getCitasCanceladas());
        assertEquals(1, rs.getTotalMascotasAtendidas());
        assertEquals(1, rs.getConsultasPorVeterinario().size());
        assertEquals(1, rs.getConsultasPorEspecialidad().size());
        assertEquals("Bruno", rs.getMascotasAtendidasPeriodo().get(0).getNombreMascota());
    }
}
