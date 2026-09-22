package com.uniminuto.clinica.api;

import com.uniminuto.clinica.entity.Mascota;
import com.uniminuto.clinica.entity.Medicamento;
import com.uniminuto.clinica.models.MascotaRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Interfaz que gestiona los servicios web REST para la entidad Mascota.
 * Permite realizar operaciones de consulta general, filtrado por cliente/raza y registro o actualización.
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/mascota")
public interface MascotaApi {

    /**
     * Obtiene el listado completo de todas las mascotas registradas en el sistema.
     *
     * @return ResponseEntity con una lista de objetos Mascota en formato JSON.
     * @throws BadRequestException si hay un error al recuperar la información o los datos de consumo.
     */
    @GetMapping(value = "/listar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> listarMascotas()
            throws BadRequestException;


    /**
     * Busca y filtra las mascotas asociadas a un cliente específico mediante su identificador único.
     *
     * @param clienteId Identificador numérico único del cliente propietario de las mascotas.
     * @return ResponseEntity con la lista de mascotas filtradas por el cliente consultado.
     * @throws BadRequestException si el parámetro clienteId es inválido o no se encuentra.
     */
    @GetMapping(value = "/listar-by-cliente",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorCliente(
            @RequestParam Long clienteId)
            throws BadRequestException;

    /**
     * Busca y filtra las mascotas que pertenecen a una raza específica dentro del sistema.
     *
     * @param razaId Identificador numérico de la raza a consultar.
     * @return ResponseEntity con la lista de mascotas correspondientes a la raza indicada.
     * @throws BadRequestException si el parámetro razaId no es válido o presenta problemas de formato.
     */
    @GetMapping(value = "/listar-by-raza",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<List<Mascota>> buscarMascotasPorRaza(
            @RequestParam Integer razaId)
            throws BadRequestException;


    /**
     * Registra y almacena una nueva mascota en la base de datos de la clínica.
     *
     * @param mascotaRq Objeto de transferencia de datos (Request) que contiene la información de la mascota a crear.
     * @return ResponseEntity con un objeto MiRespuestaRS que detalla el éxito o fallo de la transacción.
     * @throws BadRequestException si los datos enviados en el cuerpo de la petición son incorrectos o incompletos.
     */
    @PostMapping(value = "/guardar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> guardarMascota(
            @RequestBody MascotaRq mascotaRq)
            throws BadRequestException;

    /**
     * Actualiza la información de una mascota existente en el sistema.
     *
     * @param mascotaRq Objeto de transferencia de datos (Request) con los nuevos atributos de la mascota a modificar.
     * @return ResponseEntity con un objeto MiRespuestaRS confirmando la actualización del registro.
     * @throws BadRequestException si la mascota no existe o el formato del cuerpo de la petición es erróneo.
     */
    @PostMapping(value = "/actualizar",
            produces = {"application/json"},
            consumes = {"application/json"})
    ResponseEntity<MiRespuestaRS> actualizarMascota(
            @RequestBody MascotaRq mascotaRq)
            throws BadRequestException;
}