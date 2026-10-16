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

    /** Estado de una historia clínica que aún recibe anotaciones. */
    public static final String HISTORIA_ABIERTA = "ABIERTA";
    /** Estado de una historia clínica cerrada, ya no se puede modificar. */
    public static final String HISTORIA_CERRADA = "CERRADA";

    /** Todos los estados válidos de una historia clínica. */
    public static final List<String> ESTADOS_HISTORIA = List.of(HISTORIA_ABIERTA, HISTORIA_CERRADA);

    /** Duración máxima permitida para un tratamiento, en días. */
    public static final int MAXIMO_DIAS_TRATAMIENTO = 365;

    /** Constructor privado, la clase solo contiene constantes. */
    private Constantes() {
    }
}
