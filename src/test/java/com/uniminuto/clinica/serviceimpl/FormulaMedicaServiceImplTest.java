package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.FormulaMedicaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.FormulaMedicaRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import com.uniminuto.clinica.service.MedicamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FormulaMedicaServiceImplTest {

    @Mock
    private FormulaMedicaRepository formulaMedicaRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private CitaRepository citaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @Mock
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;
    @Mock
    private MedicamentoService medicamentoService;

    private FormulaMedicaServiceImpl formulaMedicaService;
    private Mascota mascota;
    private Cita cita;
    private Medico medico;
    private Medicamento medicamento;

    @BeforeEach
    void setUp() {
        formulaMedicaService = new FormulaMedicaServiceImpl(
                formulaMedicaRepository, mascotaRepository, citaRepository,
                medicoRepository, medicamentoRepository, movimientoMedicamentoRepository,
                medicamentoService);

        Cliente cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombres("Ana");
        cliente.setApellidos("Pérez");
        cliente.setActivo(true);

        mascota = new Mascota();
        mascota.setMascotaId(10);
        mascota.setNombreMascota("Luna");
        mascota.setCliente(cliente);

        medico = new Medico();
        medico.setId(5L);
        medico.setNombres("Sergio");
        medico.setApellidos("Gómez");

        cita = new Cita();
        cita.setId(20L);
        cita.setMascota(mascota);
        cita.setCliente(cliente);
        cita.setMedico(medico);
        cita.setFechaHora(LocalDateTime.now().minusHours(1));
        cita.setEstado("ATENDIDA");

        medicamento = new Medicamento();
        medicamento.setId(100L);
        medicamento.setNombre("Amoxicilina Vet 500mg");
        medicamento.setCodigo("AMOX5001");
        medicamento.setCantidadDisponible(30);
        medicamento.setStockMinimo(5);
        medicamento.setFechaVence(LocalDate.now().plusMonths(6));
        medicamento.setActivo(true);
    }

    @Test
    void crearFormulaMedica_validaInventarioPeroNoDescuentaStockAlFormular() {
        FormulaMedicaRq rq = new FormulaMedicaRq();
        rq.setMascotaId(10);
        rq.setCitaId(20L);
        rq.setMedicoId(5L);
        rq.setMedicamentoId(100L);
        rq.setFechaEmision(LocalDate.now());
        rq.setCantidadSolicitada(14);
        rq.setDosis("1 tableta (500 mg)");
        rq.setFrecuencia("Cada 12 horas");
        rq.setDuracionDias(7);
        rq.setIndicaciones("Administrar con alimento cada 12 horas por 7 días");

        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascota));
        when(citaRepository.findById(20L)).thenReturn(Optional.of(cita));
        when(formulaMedicaRepository.existsByCita_IdAndMedicamento_IdAndActivoTrue(20L, 100L)).thenReturn(false);
        when(medicoRepository.findById(5L)).thenReturn(Optional.of(medico));
        when(medicamentoRepository.findById(100L)).thenReturn(Optional.of(medicamento));

        MiRespuestaRS res = formulaMedicaService.crearFormulaMedica(rq);

        assertEquals(201, res.getStatus());
        ArgumentCaptor<FormulaMedica> captor = ArgumentCaptor.forClass(FormulaMedica.class);
        verify(formulaMedicaRepository).save(captor.capture());

        FormulaMedica guardada = captor.getValue();
        assertEquals("FORMULADA", guardada.getEstado());
        assertEquals(14, guardada.getCantidadSolicitada());
        assertEquals(0, guardada.getCantidadEntregada());
        // El stock del medicamento NO se descuenta al formular
        assertEquals(30, medicamento.getCantidadDisponible());
        verify(medicamentoService, never()).registrarSalidaInventario(any(), any(), any(), any(), any(), any());
    }

    @Test
    void crearFormulaMedica_rechazaCuandoCantidadSuperaStockDisponible() {
        FormulaMedicaRq rq = new FormulaMedicaRq();
        rq.setMascotaId(10);
        rq.setCitaId(20L);
        rq.setMedicoId(5L);
        rq.setMedicamentoId(100L);
        rq.setFechaEmision(LocalDate.now());
        rq.setCantidadSolicitada(50); // Disponible es 30
        rq.setDosis("1 tableta");
        rq.setFrecuencia("Cada 8 horas");
        rq.setDuracionDias(15);
        rq.setIndicaciones("Vía oral");

        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascota));
        when(citaRepository.findById(20L)).thenReturn(Optional.of(cita));
        when(formulaMedicaRepository.existsByCita_IdAndMedicamento_IdAndActivoTrue(20L, 100L)).thenReturn(false);
        when(medicoRepository.findById(5L)).thenReturn(Optional.of(medico));
        when(medicamentoRepository.findById(100L)).thenReturn(Optional.of(medicamento));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> formulaMedicaService.crearFormulaMedica(rq));
        assertTrue(ex.getMessage().contains("Inventario insuficiente"));
        verify(formulaMedicaRepository, never()).save(any());
    }

    @Test
    void dispensarFormula_registraSalidaInventarioYCambiaEstadoAParcialYLuegoEntregada() {
        FormulaMedica formula = new FormulaMedica();
        formula.setId(55L);
        formula.setCita(cita);
        formula.setMascota(mascota);
        formula.setMedico(medico);
        formula.setMedicamento(medicamento);
        formula.setCantidadSolicitada(10);
        formula.setCantidadEntregada(0);
        formula.setEstado("FORMULADA");
        formula.setActivo(true);

        when(formulaMedicaRepository.findById(55L)).thenReturn(Optional.of(formula));

        // 1. Dispensación parcial de 4 unidades
        MiRespuestaRS resParcial = formulaMedicaService.dispensarFormula(55L, 4, "Primera entrega", "Farmacia Turno 1");
        assertEquals(200, resParcial.getStatus());
        assertEquals(4, formula.getCantidadEntregada());
        assertEquals("PARCIAL", formula.getEstado());
        verify(medicamentoService).registrarSalidaInventario(
                eq(100L), eq(4), contains("Primera entrega"), eq(55L), eq("FORMULA"), eq("Farmacia Turno 1"));

        // 2. Dispensación del saldo restante (6 unidades)
        MiRespuestaRS resTotal = formulaMedicaService.dispensarFormula(55L, 6, "Entrega final", "Farmacia Turno 1");
        assertEquals(200, resTotal.getStatus());
        assertEquals(10, formula.getCantidadEntregada());
        assertEquals("ENTREGADA", formula.getEstado());
        assertNotNull(formula.getFechaDispenacion());
    }
}
