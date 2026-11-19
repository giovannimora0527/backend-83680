package com.uniminuto.clinica.utils;

import java.util.List;

/** Valores fijos que se usan en las reglas de negocio de la clínica. */
public final class Constantes {

    /** Estado de una cita que aún no ha sido confirmada. */
    public static final String CITA_PENDIENTE = "PENDIENTE";
    /** Estado de una cita agendada. */
    public static final String CITA_PROGRAMADA = "PROGRAMADA";
    /** Estado de una cita confirmada por el cliente. */
    public static final String CITA_CONFIRMADA = "CONFIRMADA";
    /** Estado de una cita en la que la mascota ya fue atendida. */
    public static final String CITA_ATENDIDA = "ATENDIDA";
    /** Alias de estado finalizado (se normaliza o trata junto con ATENDIDA). */
    public static final String CITA_FINALIZADA = "FINALIZADA";
    /** Estado de una cita cancelada. */
    public static final String CITA_CANCELADA = "CANCELADA";

    /** Todos los estados válidos de una cita. */
    public static final List<String> ESTADOS_CITA = List.of(
            CITA_PENDIENTE, CITA_PROGRAMADA, CITA_CONFIRMADA, CITA_ATENDIDA, CITA_CANCELADA);

    /** Estados con los que se puede registrar una cita nueva. */
    public static final List<String> ESTADOS_INICIALES_CITA = List.of(
            CITA_PENDIENTE, CITA_PROGRAMADA, CITA_CONFIRMADA);

    /** Minutos antes y después que se bloquean alrededor de una cita. */
    public static final int MINUTOS_MARGEN_CITA = 15;

    /** Duración por defecto de una cita médica en minutos cuando no se indica hora fin. */
    public static final int DURACION_DEFECTO_CITA_MINUTOS = 30;

    /** Estado de una historia clínica que aún recibe anotaciones. */
    public static final String HISTORIA_ABIERTA = "ABIERTA";
    /** Estado de una historia clínica cerrada, ya no se puede modificar. */
    public static final String HISTORIA_CERRADA = "CERRADA";

    /** Todos los estados válidos de una historia clínica. */
    public static final List<String> ESTADOS_HISTORIA = List.of(HISTORIA_ABIERTA, HISTORIA_CERRADA);

    /** Duración máxima permitida para un tratamiento, en días. */
    public static final int MAXIMO_DIAS_TRATAMIENTO = 365;

    /** Estado inicial de una fórmula médica emitida (pendiente de dispensación). */
    public static final String FORMULA_FORMULADA = "FORMULADA";
    /** Estado de una fórmula con entrega parcial de medicamentos. */
    public static final String FORMULA_PARCIAL = "PARCIAL";
    /** Estado de una fórmula cuyos medicamentos fueron dispensados en su totalidad. */
    public static final String FORMULA_ENTREGADA = "ENTREGADA";
    /** Estado de una fórmula anulada o cancelada. */
    public static final String FORMULA_CANCELADA = "CANCELADA";

    /** Estado de factura pendiente de pago. */
    public static final String FACTURA_PENDIENTE = "PENDIENTE";
    /** Estado de factura con abono parcial. */
    public static final String FACTURA_PARCIAL = "PARCIAL";
    /** Estado de factura pagada en su totalidad. */
    public static final String FACTURA_PAGADA = "PAGADA";
    /** Estado de factura anulada (no se elimina físicamente). */
    public static final String FACTURA_ANULADA = "ANULADA";

    /** Estados válidos para una factura. */
    public static final List<String> ESTADOS_FACTURA = List.of(
            FACTURA_PENDIENTE, FACTURA_PARCIAL, FACTURA_PAGADA, FACTURA_ANULADA);

    /** Tipo de línea de factura correspondiente a un servicio médico. */
    public static final String ITEM_SERVICIO = "SERVICIO";
    /** Tipo de línea de factura correspondiente a un medicamento dispensado. */
    public static final String ITEM_MEDICAMENTO = "MEDICAMENTO";

    /** Métodos de pago aceptados para registrar abonos o pagos de factura. */
    public static final List<String> METODOS_PAGO = List.of(
            "EFECTIVO", "TARJETA", "TRANSFERENCIA", "OTRO");

    /** Constructor privado, la clase solo contiene constantes. */
    private Constantes() {
    }
}
