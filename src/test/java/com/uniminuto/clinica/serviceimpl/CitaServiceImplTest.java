package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.BloqueoHorario;
import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.entity.Raza;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.exception.ConflictException;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.BloqueoHorarioRepository;
import com.uniminuto.clinica.repository.CitaRepository;
import com.uniminuto.clinica.repository.ClienteRepository;
import com.uniminuto.clinica.repository.MascotaRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CitaServiceImplTest {

    @Mock
    private CitaRepository citaRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private MascotaRepository mascotaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private BloqueoHorarioRepository bloqueoHorarioRepository;

    private CitaServiceImpl citaService;
    private Cliente cliente;
    private Mascota mascota;
    private Medico medico;

    @BeforeEach
    void setUp() {
        citaService = new CitaServiceImpl(
                citaRepository, clienteRepository, mascotaRepository, medicoRepository, bloqueoHorarioRepository);

        cliente = new Cliente();
        cliente.setClienteId(1L);
        cliente.setNombres("Carlos");
        cliente.setApellidos("Gómez");
        cliente.setTipoDocumento("CC");
        cliente.setNumeroDocumento("10203040");
        cliente.setTelefono("3001234567");

        Raza raza = new Raza();
        raza.setRazaId(1);
        raza.setNombre("Golden Retriever");

        mascota = new Mascota();
        mascota.setMascotaId(10);
        mascota.setNombreMascota("Max");
        mascota.setEdad(3);
        mascota.setRaza(raza);
        mascota.setCliente(cliente);

        Especializacion esp = new Especializacion();
        esp.setId(1L);
        esp.setNombre("Cirugía");

        medico = new Medico();
        medico.setId(5L);
        medico.setNombres("Laura");
        medico.setApellidos("Martínez");
        medico.setEspecializacion(esp);
    }

    @Test
    void crearCita_asignaFechaHoraFinPorDefectoYRegistraHistorial() throws BadRequestException {
        LocalDateTime manana10am = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        CitaRq rq = new CitaRq();
        rq.setClienteId(1L);
        rq.setMascotaId(10);
        rq.setMedicoId(5L);
        rq.setFechaHora(manana10am);
        rq.setEstado("PENDIENTE");
        rq.setMotivo("Control general");
        rq.setUsuarioCambio("Perfil: SECRETARIA");

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascota));
        when(medicoRepository.findById(5L)).thenReturn(Optional.of(medico));
        when(bloqueoHorarioRepository.findByMedico_IdAndActivoTrueAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                eq(5L), any(), any())).thenReturn(Collections.emptyList());
        when(citaRepository.findByMedico_IdAndFechaHoraBetween(eq(5L), any(), any()))
                .thenReturn(Collections.emptyList());
        when(citaRepository.findByMascota_MascotaIdAndFechaHoraBetween(eq(10), any(), any()))
                .thenReturn(Collections.emptyList());

        MiRespuestaRS res = citaService.crearCita(rq);

        assertEquals(201, res.getStatus());
        ArgumentCaptor<Cita> captor = ArgumentCaptor.forClass(Cita.class);
        verify(citaRepository).save(captor.capture());

        Cita guardada = captor.getValue();
        assertEquals(manana10am.plusMinutes(30), guardada.getFechaHoraFin());
        assertNotNull(guardada.getHistorialCambios());
        assertTrue(guardada.getHistorialCambios().contains("Cita registrada en estado PENDIENTE"));
    }

    @Test
    void crearCita_rechazaCuandoExisteBloqueoHorarioDelVeterinario() {
        LocalDateTime manana11am = LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0);
        CitaRq rq = new CitaRq();
        rq.setClienteId(1L);
        rq.setMascotaId(10);
        rq.setMedicoId(5L);
        rq.setFechaHora(manana11am);
        rq.setEstado("CONFIRMADA");
        rq.setMotivo("Vacunación");

        BloqueoHorario bloqueo = new BloqueoHorario();
        bloqueo.setId(99L);
        bloqueo.setMedico(medico);
        bloqueo.setFechaHoraInicio(manana11am.minusMinutes(30));
        bloqueo.setFechaHoraFin(manana11am.plusMinutes(60));
        bloqueo.setMotivo("Cirugía programada");
        bloqueo.setActivo(true);

        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));
        when(mascotaRepository.findById(10)).thenReturn(Optional.of(mascota));
        when(medicoRepository.findById(5L)).thenReturn(Optional.of(medico));
        when(bloqueoHorarioRepository.findByMedico_IdAndActivoTrueAndFechaHoraInicioLessThanAndFechaHoraFinGreaterThan(
                eq(5L), any(), any())).thenReturn(List.of(bloqueo));

        ConflictException ex = assertThrows(ConflictException.class, () -> citaService.crearCita(rq));
        assertTrue(ex.getMessage().contains("Cirugía programada"));
        verify(citaRepository, never()).save(any());
    }

    @Test
    void marcarCitaAtendida_cambiaEstadoYRegistraTrazabilidad() throws BadRequestException {
        Cita cita = new Cita();
        cita.setId(12L);
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(LocalDateTime.now().plusHours(1));
        cita.setEstado("CONFIRMADA");
        cita.setMotivo("Chequeo");

        when(citaRepository.findById(12L)).thenReturn(Optional.of(cita));

        MiRespuestaRS res = citaService.marcarCitaAtendida(12L, "Veterinario: Dr(a). Laura Martínez");

        assertEquals(200, res.getStatus());
        assertEquals("ATENDIDA", cita.getEstado());
        assertNotNull(cita.getHistorialCambios());
        assertTrue(cita.getHistorialCambios().contains("ATENDIDA"));
        verify(citaRepository).save(cita);
    }

    @Test
    void exportarCitasExcel_generaArchivoXlsxValido() throws BadRequestException {
        Cita cita = new Cita();
        cita.setId(1L);
        cita.setCliente(cliente);
        cita.setMascota(mascota);
        cita.setMedico(medico);
        cita.setFechaHora(LocalDateTime.of(2026, 11, 20, 9, 0));
        cita.setFechaHoraFin(LocalDateTime.of(2026, 11, 20, 9, 30));
        cita.setEstado("ATENDIDA");
        cita.setMotivo("Control postoperatorio");

        when(medicoRepository.findById(5L)).thenReturn(Optional.of(medico));
        when(citaRepository.findByMedico_IdOrderByFechaHoraDesc(5L)).thenReturn(List.of(cita));

        byte[] bytes = citaService.exportarCitasExcel(null, null, 5L, null);

        assertNotNull(bytes);
        assertTrue(bytes.length > 100);
        // Cabecera ZIP/XLSX 'PK'
        assertEquals('P', (char) bytes[0]);
        assertEquals('K', (char) bytes[1]);
    }
}
