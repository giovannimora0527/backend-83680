
package com.uniminuto.clinica.apicontroller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mascota")
@CrossOrigin(origins = "*")
public class MascotaApiController {

    private final JdbcTemplate jdbcTemplate;

    public MascotaApiController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Listar todas las mascotas registradas
    @GetMapping("/listar")
    public List<Map<String, Object>> listarMascotas() {
        String sql = """
            SELECT
                m.mascota_id,
                m.nombre_mascota,
                m.edad,
                m.fecha_registro,
                r.raza_id,
                r.nombre AS nombre_raza,
                r.especie,
                c.cliente_id,
                c.nombres,
                c.apellidos
            FROM mascota m
            INNER JOIN raza r ON m.raza_id = r.raza_id
            INNER JOIN cliente c ON m.cliente_id = c.cliente_id
            ORDER BY m.mascota_id
            """;

        List<Map<String, Object>> filas =
                jdbcTemplate.queryForList(sql);

        List<Map<String, Object>> resultado =
                new ArrayList<>();

        for (Map<String, Object> fila : filas) {
            Map<String, Object> raza = new LinkedHashMap<>();
            raza.put("id", fila.get("raza_id"));
            raza.put("nombre", fila.get("nombre_raza"));
            raza.put("especie", fila.get("especie"));

            Map<String, Object> cliente = new LinkedHashMap<>();
            cliente.put("id", fila.get("cliente_id"));
            cliente.put("nombres", fila.get("nombres"));
            cliente.put("apellidos", fila.get("apellidos"));

            Map<String, Object> mascota = new LinkedHashMap<>();
            mascota.put("mascotaId", fila.get("mascota_id"));
            mascota.put("nombreMascota", fila.get("nombre_mascota"));
            mascota.put("edad", fila.get("edad"));
            mascota.put("fechaRegistro", fila.get("fecha_registro"));
            mascota.put("raza", raza);
            mascota.put("cliente", cliente);

            resultado.add(mascota);
        }

        return resultado;
    }

    // Listar razas para el formulario de mascotas
    @GetMapping("/razas")
    public List<Map<String, Object>> listarRazas() {
        String sql = """
            SELECT
                raza_id AS id,
                nombre,
                especie
            FROM raza
            ORDER BY nombre
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Listar clientes activos para el formulario de mascotas
    @GetMapping("/clientes")
    public List<Map<String, Object>> listarClientes() {
        String sql = """
            SELECT
                cliente_id AS id,
                nombres,
                apellidos
            FROM cliente
            WHERE activo = 1
            ORDER BY nombres, apellidos
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // Registrar una nueva mascota
    @PostMapping("/guardar")
    public ResponseEntity<Map<String, Object>> guardarMascota(
            @RequestBody Map<String, Object> datos) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        try {
            String nombre = String.valueOf(
                    datos.get("nombreMascota")
            ).trim();

            int edad = Integer.parseInt(
                    String.valueOf(datos.get("edad"))
            );

            int razaId = Integer.parseInt(
                    String.valueOf(datos.get("razaId"))
            );

            long clienteId = Long.parseLong(
                    String.valueOf(datos.get("clienteId"))
            );

            if (nombre.isBlank() || edad < 0) {
                respuesta.put(
                        "mensaje",
                        "Nombre o edad no válidos"
                );

                return ResponseEntity.badRequest().body(respuesta);
            }

            String sql = """
                INSERT INTO mascota (
                    nombre_mascota,
                    edad,
                    raza_id,
                    cliente_id,
                    fecha_registro,
                    fecha_modificacion
                )
                VALUES (?, ?, ?, ?, NOW(), NULL)
                """;

            jdbcTemplate.update(
                    sql,
                    nombre,
                    edad,
                    razaId,
                    clienteId
            );

            respuesta.put(
                    "mensaje",
                    "Mascota registrada correctamente"
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(respuesta);

        } catch (DataAccessException e) {
            respuesta.put(
                    "mensaje",
                    "Error de base de datos al guardar la mascota. Verifica que la raza y el cliente existan."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);

        } catch (RuntimeException e) {
            respuesta.put(
                    "mensaje",
                    "Datos inválidos. Revisa el nombre, la edad, la raza y el cliente."
            );

            return ResponseEntity
                    .badRequest()
                    .body(respuesta);
        }
    }

    // Actualizar una mascota existente
    @PutMapping("/actualizar")
    public ResponseEntity<Map<String, Object>> actualizarMascota(
            @RequestBody Map<String, Object> datos) {

        Map<String, Object> respuesta = new LinkedHashMap<>();

        try {
            int mascotaId = Integer.parseInt(
                    String.valueOf(datos.get("mascotaId"))
            );

            String nombre = String.valueOf(
                    datos.get("nombreMascota")
            ).trim();

            int edad = Integer.parseInt(
                    String.valueOf(datos.get("edad"))
            );

            int razaId = Integer.parseInt(
                    String.valueOf(datos.get("razaId"))
            );

            long clienteId = Long.parseLong(
                    String.valueOf(datos.get("clienteId"))
            );

            if (nombre.isBlank() || edad < 0) {
                respuesta.put(
                        "mensaje",
                        "Nombre o edad no válidos"
                );

                return ResponseEntity.badRequest().body(respuesta);
            }

            String sql = """
                UPDATE mascota
                SET
                    nombre_mascota = ?,
                    edad = ?,
                    raza_id = ?,
                    cliente_id = ?,
                    fecha_modificacion = NOW()
                WHERE mascota_id = ?
                """;

            int filas = jdbcTemplate.update(
                    sql,
                    nombre,
                    edad,
                    razaId,
                    clienteId,
                    mascotaId
            );

            if (filas == 0) {
                respuesta.put(
                        "mensaje",
                        "No se encontró la mascota que deseas editar"
                );

                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(respuesta);
            }

            respuesta.put(
                    "mensaje",
                    "Mascota actualizada correctamente"
            );

            return ResponseEntity.ok(respuesta);

        } catch (DataAccessException e) {
            respuesta.put(
                    "mensaje",
                    "Error de base de datos al actualizar la mascota. Verifica que la raza y el cliente existan."
            );

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(respuesta);

        } catch (RuntimeException e) {
            respuesta.put(
                    "mensaje",
                    "Datos inválidos. Revisa el ID, el nombre, la edad, la raza y el cliente."
            );

            return ResponseEntity
                    .badRequest()
                    .body(respuesta);
        }
    }
}
