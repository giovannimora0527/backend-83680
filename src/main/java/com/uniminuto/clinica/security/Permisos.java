package com.uniminuto.clinica.security;

import java.util.List;
import java.util.Set;

import static com.uniminuto.clinica.entity.Perfil.ADMINISTRADOR;
import static com.uniminuto.clinica.entity.Perfil.DESPACHO_MEDICO;
import static com.uniminuto.clinica.entity.Perfil.MEDICO;
import static com.uniminuto.clinica.entity.Perfil.RECEPCION;

/**
 * Reglas de acceso por perfil. Se validan en el backend en cada peticion (AuthInterceptor),
 * no solo ocultando opciones en el front.
 *
 * Cada regla dice: para rutas que empiezan por "prefijo", con estos metodos HTTP,
 * que perfiles pueden entrar. Gana la primera regla que coincida.
 */
public final class Permisos {

    /** Metodos de solo lectura. */
    private static final Set<String> LECTURA = Set.of("GET");
    /** Cualquier metodo. */
    private static final Set<String> TODOS = Set.of("GET", "POST", "PUT", "PATCH", "DELETE");

    /**
     * Una regla de acceso.
     *
     * @param prefijo  inicio de la ruta (sin el context-path).
     * @param metodos  metodos HTTP a los que aplica.
     * @param perfiles perfiles permitidos.
     */
    private record Regla(String prefijo, Set<String> metodos, Set<Integer> perfiles) {
    }

    /** Lista de reglas, de la mas especifica a la mas general. */
    private static final List<Regla> REGLAS = List.of(
            // Sesion: cualquier usuario autenticado
            new Regla("/auth/", TODOS, Set.of(ADMINISTRADOR, RECEPCION, MEDICO, DESPACHO_MEDICO)),
            // Usuarios y perfiles: solo administrador
            new Regla("/usuario/", TODOS, Set.of(ADMINISTRADOR)),
            // Especialidades y tarifas: todos consultan, solo el admin modifica
            new Regla("/especializacion/", LECTURA, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            new Regla("/especializacion/", TODOS, Set.of(ADMINISTRADOR)),
            new Regla("/tarifa/", LECTURA, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            new Regla("/tarifa/", TODOS, Set.of(ADMINISTRADOR)),
            // Inventario: despacho y medico consultan, solo el admin crea/edita/carga
            new Regla("/medicamento/", LECTURA, Set.of(ADMINISTRADOR, MEDICO, DESPACHO_MEDICO)),
            new Regla("/medicamento/", TODOS, Set.of(ADMINISTRADOR)),
            new Regla("/inventario/", LECTURA, Set.of(ADMINISTRADOR, DESPACHO_MEDICO)),
            new Regla("/inventario/", TODOS, Set.of(ADMINISTRADOR)),
            // Despacho: la reversion la autoriza el admin, el resto solo DESPACHO_MEDICO
            new Regla("/despacho/revertir", TODOS, Set.of(ADMINISTRADOR)),
            new Regla("/despacho/", LECTURA, Set.of(ADMINISTRADOR, DESPACHO_MEDICO)),
            new Regla("/despacho/", TODOS, Set.of(DESPACHO_MEDICO)),
            // Facturacion y reportes
            new Regla("/factura/", TODOS, Set.of(ADMINISTRADOR, RECEPCION)),
            new Regla("/reporte/", TODOS, Set.of(ADMINISTRADOR)),
            // Agenda (calendario) y atencion: solo el medico
            new Regla("/agenda/", TODOS, Set.of(MEDICO)),
            new Regla("/atencion/", TODOS, Set.of(MEDICO)),
            // Modulo de citas ya desarrollado: secretaria y administrador
            new Regla("/cita/", TODOS, Set.of(ADMINISTRADOR, RECEPCION)),
            // Historias y formulas: medico y administrador (despacho NO)
            new Regla("/historia-medica/", TODOS, Set.of(ADMINISTRADOR, MEDICO)),
            new Regla("/anotacion-historia/", TODOS, Set.of(ADMINISTRADOR, MEDICO)),
            new Regla("/formula-medica/", TODOS, Set.of(ADMINISTRADOR, MEDICO)),
            // Clientes, mascotas y razas
            new Regla("/cliente/", TODOS, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            new Regla("/mascota/", TODOS, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            new Regla("/raza/", TODOS, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            // Medicos y horarios: todos consultan, el admin modifica
            new Regla("/medico/", LECTURA, Set.of(ADMINISTRADOR, RECEPCION, MEDICO)),
            new Regla("/medico/", TODOS, Set.of(ADMINISTRADOR)),
            new Regla("/clinica/", TODOS, Set.of(ADMINISTRADOR, RECEPCION, MEDICO, DESPACHO_MEDICO))
    );

    private Permisos() {
    }

    /**
     * Indica si un perfil puede usar una ruta con un metodo.
     * Si ninguna regla coincide, solo el administrador puede entrar.
     *
     * @param ruta     ruta sin context-path, ej: /medicamento/listar.
     * @param metodo   metodo HTTP.
     * @param perfilId perfil del usuario.
     * @return true si tiene permiso.
     */
    public static boolean tienePermiso(String ruta, String metodo, Integer perfilId) {
        for (Regla regla : REGLAS) {
            if (ruta.startsWith(regla.prefijo()) && regla.metodos().contains(metodo)) {
                return regla.perfiles().contains(perfilId);
            }
        }
        return Integer.valueOf(ADMINISTRADOR).equals(perfilId);
    }
}
