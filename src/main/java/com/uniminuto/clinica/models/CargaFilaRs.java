package com.uniminuto.clinica.models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Una fila del archivo de carga masiva con su resultado de validacion.
 */
@Data
public class CargaFilaRs {
    /** Numero de la fila en el archivo (la fila 1 son los encabezados). */
    private Integer fila;
    private String codigo;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String presentacion;
    private String registroInvima;
    private String precioCompra;
    private String precioVenta;
    private String ivaPorcentaje;
    private String stockMinimo;
    private String numeroLote;
    private String cantidad;
    private String fechaCompra;
    private String fechaVence;
    /** Si la fila se puede importar. */
    private Boolean valida;
    /** Si crea un producto nuevo (true) o agrega un lote a uno existente (false). */
    private Boolean productoNuevo;
    /** Motivos de rechazo. */
    private List<String> errores = new ArrayList<>();
}
