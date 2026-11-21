package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.LoteMedicamento;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.entity.MovimientoMedicamento;
import com.uniminuto.clinica.repository.LoteMedicamentoRepository;
import com.uniminuto.clinica.repository.CargaMasivaMedicamentoRepository;
import com.uniminuto.clinica.repository.MedicamentoRepository;
import com.uniminuto.clinica.repository.MovimientoMedicamentoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentoServiceImplTest {
    @Mock
    private MedicamentoRepository medicamentoRepository;
    @Mock
    private MovimientoMedicamentoRepository movimientoMedicamentoRepository;
    @Mock
    private LoteMedicamentoRepository loteMedicamentoRepository;
    @Mock
    private CargaMasivaMedicamentoRepository cargaMasivaMedicamentoRepository;
    @InjectMocks
    private MedicamentoServiceImpl medicamentoService;

    @Test
    void registrarSalidaInventarioConsumeLotesPorVencimientoYRegistraTrazabilidad() {
        Medicamento medicamento = new Medicamento();
        medicamento.setId(8L);
        medicamento.setCantidadDisponible(10);
        medicamento.setActivo(true);

        LoteMedicamento venceAntes = lote("LOTE-A", 3, LocalDate.now().plusDays(5));
        LoteMedicamento venceDespues = lote("LOTE-B", 7, LocalDate.now().plusDays(20));
        when(medicamentoRepository.findByIdForUpdate(8L)).thenReturn(Optional.of(medicamento));
        when(loteMedicamentoRepository.existsByMedicamentoId(8L)).thenReturn(true);
        when(loteMedicamentoRepository.buscarDisponiblesPorVencer(8L, LocalDate.now()))
                .thenReturn(List.of(venceAntes, venceDespues));

        medicamentoService.registrarSalidaInventario(8L, 5, "Dispensación", 22L, "FORMULA", "farmacia");

        assertEquals(0, venceAntes.getCantidadDisponible());
        assertEquals(5, venceDespues.getCantidadDisponible());
        assertEquals(5, medicamento.getCantidadDisponible());
        verify(loteMedicamentoRepository).saveAll(anyList());

        ArgumentCaptor<List<MovimientoMedicamento>> captor = ArgumentCaptor.forClass(List.class);
        verify(movimientoMedicamentoRepository).saveAll(captor.capture());
        assertEquals(List.of("LOTE-A", "LOTE-B"),
                captor.getValue().stream().map(MovimientoMedicamento::getCodigoLote).toList());
        assertEquals(List.of(3, 2),
                captor.getValue().stream().map(MovimientoMedicamento::getCantidad).toList());
    }

    @Test
    void importarMasivoPrevisualizaValidosYErroresSinPersistir() {
        String contenido = """
                nombre,descripcion,presentacion,codigo,lote,registroInvima,requiereRegistroInvima,precioCompra,precioVenta,cantidadDisponible,stockMinimo,fechaCompra,fechaIngresoInventario,fechaVence,fechaProximoPedido,activo
                Analgesico,Producto,Tableta,ANALG001,L-01,,false,100,150,10,2,2026-01-01,2026-01-02,2030-01-01,,true
                Analgesico duplicado,Producto,Tableta,ANALG001,L-02,,false,100,150,5,1,2026-01-01,2026-01-02,2030-01-01,,true
                """;
        MockMultipartFile file = new MockMultipartFile(
                "archivo", "inventario.csv", "text/csv", contenido.getBytes(StandardCharsets.UTF_8));

        var preview = medicamentoService.importarMasivo(file, false, "admin");

        assertEquals(2, preview.getTotalRegistros());
        assertEquals(1, preview.getRegistrosValidos());
        assertEquals(1, preview.getRegistrosInvalidos());
        assertEquals(1, preview.getImportados().size());
        assertEquals(3, preview.getErrores().getFirst().getFila());
        verify(medicamentoRepository, org.mockito.Mockito.never()).saveAll(anyList());
        verify(cargaMasivaMedicamentoRepository, org.mockito.Mockito.never()).save(any());
    }

    private LoteMedicamento lote(String codigo, int cantidad, LocalDate vencimiento) {
        LoteMedicamento lote = new LoteMedicamento();
        lote.setCodigoLote(codigo);
        lote.setCantidadDisponible(cantidad);
        lote.setFechaVencimiento(vencimiento);
        lote.setActivo(true);
        return lote;
    }
}
