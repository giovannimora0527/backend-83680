package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.DetalleFactura;
import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Factura;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.models.DetalleServicioRq;
import com.uniminuto.clinica.models.FacturaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.models.PagoFacturaRq;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.FacturaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FacturaServiceImplTest {

    @Mock
    private FacturaRepository facturaRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private FormulaMedicaRepository formulaMedicaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private MascotaRepository mascotaRepository;

    private FacturaServiceImpl facturaService;
    private Cliente cliente;
    private Mascota mascota;
    private Medico medico;
    private Cita citaAtendida;

    @BeforeEach
    void setUp() {
        facturaService = new FacturaServiceImpl(
                facturaRepository, citaRepository, formulaMedicaRepository, clienteRepository, mascotaRepository);

        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombres("Pedro");
        cliente.setApellidos("Suárez");
        cliente.setTipoDocumento("CC");
        cliente.setNumeroDocumento("8090100");
        cliente.setActivo(true);

        mascota = new Mascota();
        mascota.setMascotaId(10);
        mascota.setNombreMascota("Toby");
        mascota.setCliente(cliente);

        Especializacion esp = new Especializacion();
        esp.setId(1L);
        esp.setNombre("General");

        medico = new Medico();
        medico.setId(5L);
        medico.setNombres("Camila");
        medico.setApellidos("Rojas");
        medico.setEspecializacion(esp);

        citaAtendida = new Cita();
        citaAtendida.setId(30L);
        citaAtendida.setCliente(cliente);
        citaAtendida.setMascota(mascota);
        citaAtendida.setMedico(medico);
        citaAtendida.setFechaHora(LocalDateTime.now().minusHours(2));
        citaAtendida.setEstado("ATENDIDA");
        citaAtendida.setMotivo("Consulta general");
    }

    @Test
    void crearFactura_incluyeSoloMedicamentosDispensadosYCongelaPreciosHistoricos() {
        Medicamento medDispensado = new Medicamento();
        medDispensado.setId(101L);
        medDispensado.setNombre("Meloxicam Gotas");
        medDispensado.setPresentacion("Frasco 10ml");
        medDispensado.setPrecioVenta(new BigDecimal("20000.00"));

        // Fórmula 1: Solicitó 3, pero efectivamente se dispensaron 2 unidades -> solo se facturan 2 ($40,000)
        FormulaMedica formulaDispensadaParcial = new FormulaMedica();
        formulaDispensadaParcial.setId(1L);
        formulaDispensadaParcial.setCita(citaAtendida);
        formulaDispensadaParcial.setMedicamento(medDispensado);
        formulaDispensadaParcial.setCantidadSolicitada(3);
        formulaDispensadaParcial.setCantidadEntregada(2);
        formulaDispensadaParcial.setEstado("PARCIAL");
        formulaDispensadaParcial.setActivo(true);

        // Fórmula 2: Solicitó 5, pero NO se ha dispensado (0 entregadas) -> NO se debe facturar
        Medicamento medNoDispensado = new Medicamento();
        medNoDispensado.setId(102L);
        medNoDispensado.setNombre("Cefalexina 500mg");
        medNoDispensado.setPrecioVenta(new BigDecimal("15000.00"));

        FormulaMedica formulaSinDispensar = new FormulaMedica();
        formulaSinDispensar.setId(2L);
        formulaSinDispensar.setCita(citaAtendida);
        formulaSinDispensar.setMedicamento(medNoDispensado);
        formulaSinDispensar.setCantidadSolicitada(5);
        formulaSinDispensar.setCantidadEntregada(0);
        formulaSinDispensar.setEstado("FORMULADA");
        formulaSinDispensar.setActivo(true);

        when(citaRepository.findById(30L)).thenReturn(Optional.of(citaAtendida));
        when(facturaRepository.existsByCita_IdAndEstadoNot(30L, "ANULADA")).thenReturn(false);
        when(formulaMedicaRepository.findByCita_IdOrderByFechaCreacionRegistroDesc(30L))
                .thenReturn(List.of(formulaDispensadaParcial, formulaSinDispensar));
        when(facturaRepository.count()).thenReturn(0L);

        DetalleServicioRq servicioConsulta = new DetalleServicioRq();
        servicioConsulta.setCodigoServicio("CONSULTA_GENERAL");
        servicioConsulta.setConcepto("Consulta Médica Veterinaria");
        servicioConsulta.setCantidad(1);
        servicioConsulta.setPrecioUnitario(new BigDecimal("60000.00"));

        FacturaRq rq = new FacturaRq();
        rq.setCitaId(30L);
        rq.setServicios(List.of(servicioConsulta));
        rq.setPorcentajeDescuento(new BigDecimal("10.00")); // 10% sobre $100,000 = $10,000
        rq.setPorcentajeImpuesto(new BigDecimal("19.00"));  // 19% sobre $90,000 = $17,100 -> Total: $107,100
        rq.setUsuarioRegistro("Caja 1");

        MiRespuestaRS res = facturaService.crearFactura(rq);

        assertEquals(201, res.getStatus());
        ArgumentCaptor<Factura> captor = ArgumentCaptor.forClass(Factura.class);
        verify(facturaRepository).save(captor.capture());

        Factura guardada = captor.getValue();
        assertEquals("PENDIENTE", guardada.getEstado());
        assertEquals(new BigDecimal("60000.00"), guardada.getSubtotalServicios());
        assertEquals(new BigDecimal("40000.00"), guardada.getSubtotalMedicamentos());
        assertEquals(new BigDecimal("100000.00"), guardada.getSubtotal());
        assertEquals(new BigDecimal("10000.00"), guardada.getValorDescuento());
        assertEquals(new BigDecimal("17100.00"), guardada.getValorImpuesto());
        assertEquals(new BigDecimal("107100.00"), guardada.getTotal());
        assertEquals(new BigDecimal("107100.00"), guardada.getSaldoPendiente());

        // Solo 2 detalles: 1 servicio + 1 medicamento dispensado (la fórmula sin dispensar no se cobra)
        assertEquals(2, guardada.getDetalles().size());
        DetalleFactura detMed = guardada.getDetalles().stream()
                .filter(d -> "MEDICAMENTO".equals(d.getTipoItem()))
                .findFirst()
                .orElseThrow();
        assertEquals(2, detMed.getCantidad());
        assertEquals(new BigDecimal("20000.00"), detMed.getPrecioUnitarioHistorico());
    }

    @Test
    void crearFactura_rechazaCitaNoAtendidaODobleCobroDeMismoServicio() {
        Cita citaPendiente = new Cita();
        citaPendiente.setId(31L);
        citaPendiente.setEstado("PENDIENTE");
        when(citaRepository.findById(31L)).thenReturn(Optional.of(citaPendiente));

        FacturaRq rqNoAtendida = new FacturaRq();
        rqNoAtendida.setCitaId(31L);

        assertThrows(BadRequestException.class, () -> facturaService.crearFactura(rqNoAtendida));

        // Cita ya facturada previamente (estado != ANULADA)
        when(citaRepository.findById(30L)).thenReturn(Optional.of(citaAtendida));
        when(facturaRepository.existsByCita_IdAndEstadoNot(30L, "ANULADA")).thenReturn(true);

        FacturaRq rqDobleCita = new FacturaRq();
        rqDobleCita.setCitaId(30L);
        assertThrows(ConflictException.class, () -> facturaService.crearFactura(rqDobleCita));
    }

    @Test
    void registrarPago_y_anularFacturaSinEliminarHistorial() {
        Factura factura = new Factura();
        factura.setId(88L);
        factura.setNumeroFactura("FAC-2026-C30-0001");
        factura.setCita(citaAtendida);
        factura.setCliente(cliente);
        factura.setMascota(mascota);
        factura.setMedico(medico);
        factura.setTotal(new BigDecimal("100000.00"));
        factura.setTotalPagado(BigDecimal.ZERO.setScale(2));
        factura.setSaldoPendiente(new BigDecimal("100000.00"));
        factura.setEstado("PENDIENTE");

        when(facturaRepository.findById(88L)).thenReturn(Optional.of(factura));

        // 1. Abono parcial de $40,000 -> Estado PARCIAL, Saldo $60,000
        PagoFacturaRq abono1 = new PagoFacturaRq();
        abono1.setMonto(new BigDecimal("40000.00"));
        abono1.setMetodoPago("EFECTIVO");
        abono1.setUsuarioRegistro("Caja 1");

        MiRespuestaRS resAbono = facturaService.registrarPago(88L, abono1);
        assertEquals(200, resAbono.getStatus());
        assertEquals("PARCIAL", factura.getEstado());
        assertEquals(new BigDecimal("40000.00"), factura.getTotalPagado());
        assertEquals(new BigDecimal("60000.00"), factura.getSaldoPendiente());
        assertEquals(1, factura.getPagos().size());

        // 2. Pago restante de $60,000 -> Estado PAGADA, Saldo $0
        PagoFacturaRq abono2 = new PagoFacturaRq();
        abono2.setMonto(new BigDecimal("60000.00"));
        abono2.setMetodoPago("TRANSFERENCIA");
        abono2.setUsuarioRegistro("Caja 1");

        MiRespuestaRS resTotal = facturaService.registrarPago(88L, abono2);
        assertEquals(200, resTotal.getStatus());
        assertEquals("PAGADA", factura.getEstado());
        assertEquals(new BigDecimal("0.00"), factura.getSaldoPendiente());

        // 3. Anulación controlada sin eliminar historial
        MiRespuestaRS resAnulacion = facturaService.anularFactura(88L, "Error en tarifa aplicada", "Admin");
        assertEquals(200, resAnulacion.getStatus());
        assertEquals("ANULADA", factura.getEstado());
        assertEquals("Error en tarifa aplicada", factura.getMotivoAnulacion());
        assertNotNull(factura.getFechaAnulacion());
        assertTrue(factura.getHistorialOperaciones().contains("ANULADA"));
        verify(facturaRepository, never()).delete(any());
    }
}
