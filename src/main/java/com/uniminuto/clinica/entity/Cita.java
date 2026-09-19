package com.uniminuto.clinica.entity;

import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Clase que mapea la tabla cita en la base de datos.
 */
@Data
@Entity
@Table(name = "cita")
public class Cita implements Serializable {

    /**
     * Id serializable.
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    /**
    * Identificador del cliente asociado a la cita.
    */
    @Column(name = "cliente_id")
    private Integer clienteId;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    @Column(name = "estado")
    private String estado;

    @Column(name = "motivo")
    private String motivo;

   @Column(name = "mascota_id")
   private Integer mascotaId;

    @ManyToOne
    @JoinColumn(name = "medico_id", referencedColumnName = "id")
    private Medico medico;
}
