package com.uniminuto.clinica.models;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
/** Respuesta consolidada del reporte de gestión médica y atención de mascotas (Sección 4.3). */
public class ReporteGestionMedicaRS {

    private LocalDateTime fechaGeneracion;
    private String rangoFechas;

    // Indicadores de citas del período
    private int totalCitasPeriodo;
    private int citasAtendidas;
    private int citasCanceladas;
    private int citasPendientesTotal;
    private int citasEstadoPendiente;
    private int citasEstadoProgramada;
    private int citasEstadoConfirmada;
    private int totalMascotasAtendidas;

    // Desgloses de gestión médica
    private List<ItemGestionMedicaAgrupado> consultasPorVeterinario = new ArrayList<>();
    private List<ItemGestionMedicaAgrupado> consultasPorEspecialidad = new ArrayList<>();
    private List<ItemMascotaAtendida> mascotasAtendidasPeriodo = new ArrayList<>();

    @Data
    public static class ItemGestionMedicaAgrupado {
        private Long id;
        private String nombre;
        private String subcategoria;
        private int totalCitas;
        private int citasAtendidas;
        private int citasPendientes;
        private int citasCanceladas;
        private BigDecimal porcentajeCumplimiento = BigDecimal.ZERO;
    }

    @Data
    public static class ItemMascotaAtendida {
        private Integer mascotaId;
        private String nombreMascota;
        private String raza;
        private Integer edad;
        private String propietario;
        private String documentoPropietario;
        private String telefonoPropietario;
        private int consultasAtendidas;
        private LocalDateTime ultimaFechaAtencion;
        private String ultimoVeterinario;
        private String ultimoMotivo;
    }
}
