package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.AnotacionHistoria;
import com.uniminuto.clinica.repository.AnotacionHistoriaRepository;
import com.uniminuto.clinica.service.AnotacionHistoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementación de la interfaz AnotacionHistoriaService que contiene la lógica de negocio
 * para la gestión de anotaciones de historia clínica[cite: 20].
 *
 * @author Juan Kamilo Rodriguez Diaz
 * @version 1.0
 */
@Service
public class AnotacionHistoriaServiceImpl implements AnotacionHistoriaService {

    /** Repositorio JPA para realizar operaciones de persistencia en la entidad AnotacionHistoria[cite: 20]. */
    @Autowired
    private AnotacionHistoriaRepository anotacionHistoriaRepository;

    /**
     * Guarda una nueva anotación de historia en el sistema, asignando la fecha actual si no viene especificada[cite: 20].
     *
     * @param anotacion Objeto AnotacionHistoria con los datos a registrar[cite: 20].
     * @return La anotación guardada exitosamente en la base de datos[cite: 20].
     */
    @Override
    public AnotacionHistoria guardarAnotacion(AnotacionHistoria anotacion) {
        if (anotacion.getFecha() == null) {
            anotacion.setFecha(LocalDateTime.now());
        }
        return anotacionHistoriaRepository.save(anotacion);
    }

    /**
     * Actualiza una anotación de historia existente a partir de su identificador[cite: 20].
     *
     * @param id Identificador único de la anotación a modificar[cite: 20].
     * @param detallesAnotacion Objeto que contiene los nuevos datos de la anotación[cite: 20].
     * @return La anotación actualizada y persistida[cite: 20].
     * @throws RuntimeException si no se encuentra la anotación con el id proporcionado[cite: 20].
     */
    @Override
    public AnotacionHistoria actualizarAnotacion(Long id, AnotacionHistoria detallesAnotacion) {
        AnotacionHistoria anotacionExistente = anotacionHistoriaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Anotacion no encontrada con id: " + id));

        anotacionExistente.setHistoriaId(detallesAnotacion.getHistoriaId());
        anotacionExistente.setMedicoId(detallesAnotacion.getMedicoId());
        anotacionExistente.setDescripcion(detallesAnotacion.getDescripcion());
        if (detallesAnotacion.getFecha() != null) {
            anotacionExistente.setFecha(detallesAnotacion.getFecha());
        }

        return anotacionHistoriaRepository.save(anotacionExistente);
    }

    /**
     * Filtra las anotaciones de historia dentro de un rango de fechas específico[cite: 20].
     *
     * @param fechaInicio Fecha y hora inicial del filtro[cite: 20].
     * @param fechaFin Fecha y hora final del filtro[cite: 20].
     * @return Lista de anotaciones comprendidas en el rango, ordenadas descendentemente[cite: 20].
     */
    @Override
    public List<AnotacionHistoria> filtrarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return anotacionHistoriaRepository.findByFechaBetweenOrderByFechaDesc(fechaInicio, fechaFin);
    }
}