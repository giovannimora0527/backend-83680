package com.uniminuto.clinica.models;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.Cliente;
import com.uniminuto.clinica.entity.DetalleFactura;
import com.uniminuto.clinica.entity.FormulaMedica;
import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medico;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
/** Vista previa de liquidación de una cita atendida antes de confirmar la generación de la factura. */
public class PreliquidacionFacturaRS {
    /** Cita atendida a facturar. */
    private Cita cita;
    /** Propietario responsable. */
    private Cliente cliente;
    /** Paciente atendido. */
    private Mascota mascota;
    /** Veterinario que atendió la cita. */
    private Medico medico;
    /** Servicios médicos sugeridos para la atención. */
    private List<DetalleFactura> serviciosSugeridos = new ArrayList<>();
    /** Medicamentos efectivamente dispensados en la cita con su precio histórico del inventario. */
    private List<DetalleFactura> medicamentosDispensados = new ArrayList<>();
    /** Fórmulas de la cita que aún tienen unidades pendientes de dispensar (informativo: no se facturan). */
    private List<FormulaMedica> formulasPendientesDispensar = new ArrayList<>();
    /** Subtotal sugerido de servicios. */
    private BigDecimal subtotalServicios = BigDecimal.ZERO;
    /** Subtotal de medicamentos efectivamente dispensados. */
    private BigDecimal subtotalMedicamentos = BigDecimal.ZERO;
    /** Porcentaje de impuesto por defecto sugerido. */
    private BigDecimal porcentajeImpuestoSugerido = new BigDecimal("19.00");
}
