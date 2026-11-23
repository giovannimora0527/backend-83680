package com.uniminuto.clinica.service;

import com.uniminuto.clinica.entity.Cita;
import com.uniminuto.clinica.entity.HorarioMedico;
import com.uniminuto.clinica.models.CitaRq;
import com.uniminuto.clinica.models.CitaRs;
import com.uniminuto.clinica.models.MiRespuestaRS;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio con la lógica de negocio de las citas (modulo de secretaria y agenda del medico).
 */
public interface CitaService {

    /**
     * Lista las citas (todas o entre dos fechas), de la más reciente a la más antigua.
     *
     * @param fechaInicial primer día (opcional).
     * @param fechaFinal   último día (opcional).
     * @return las citas.
     */
    List<CitaRs> listarCitasPorFechas(LocalDate fechaInicial, LocalDate fechaFinal);

    /**
     * Historial de citas de una mascota.
     *
     * @param mascotaId id de la mascota.
     * @return las citas de la mascota.
     */
    List<CitaRs> listarCitasPorMascota(Integer mascotaId);

    /**
     * Citas de un medico en un rango (calendario y reporte).
     *
     * @param medicoId id del medico.
     * @param desde    inicio.
     * @param hasta    fin.
     * @return citas del medico.
     */
    List<CitaRs> listarCitasMedico(Long medicoId, LocalDateTime desde, LocalDateTime hasta);

    /**
     * Consulta una cita por su id.
     *
     * @param citaId id de la cita.
     * @return la cita convertida a respuesta.
     */
    CitaRs consultarCita(Long citaId);

    /**
     * Busca la entidad cita y lanza error si no existe.
     *
     * @param citaId id de la cita.
     * @return la entidad cita.
     */
    Cita buscarCita(Long citaId);

    /**
     * Convierte una entidad cita al objeto de respuesta.
     *
     * @param cita entidad.
     * @return objeto de respuesta.
     */
    CitaRs convertirCita(Cita cita);

    /**
     * Crea una nueva cita validando horario, cruces y estado del medico.
     *
     * @param citaRq datos de la cita.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarCita(CitaRq citaRq);

    /**
     * Actualiza una cita existente respetando su estado actual.
     *
     * @param citaRq datos de la cita, incluido su identificador.
     * @return respuesta de éxito.
     */
    MiRespuestaRS actualizarCita(CitaRq citaRq);

    /**
     * Cambia la fecha y hora de una cita (arrastrar en el calendario).
     *
     * @param citaId id de la cita.
     * @param inicio nueva fecha y hora de inicio.
     * @param fin    nueva fecha y hora de fin.
     * @return respuesta de éxito.
     */
    MiRespuestaRS reprogramarCita(Long citaId, LocalDateTime inicio, LocalDateTime fin);

    /**
     * Cambia el estado de una cita (confirmar, cancelar o marcar atendida).
     *
     * @param citaId id de la cita.
     * @param estado nuevo estado.
     * @return respuesta de éxito.
     */
    MiRespuestaRS cambiarEstado(Long citaId, String estado);

    /**
     * Reactiva una cita cancelada, queda en estado pendiente.
     *
     * @param citaId id de la cita.
     * @return respuesta de éxito.
     */
    MiRespuestaRS reactivarCita(Long citaId);

    /**
     * Horarios de disponibilidad de un medico.
     *
     * @param medicoId id del medico.
     * @return horarios.
     */
    List<HorarioMedico> listarHorarios(Long medicoId);

    /**
     * Reemplaza los horarios de un medico.
     *
     * @param medicoId  id del medico.
     * @param horarios  nuevos horarios.
     * @return respuesta de éxito.
     */
    MiRespuestaRS guardarHorarios(Long medicoId, List<HorarioMedico> horarios);

    /**
     * Genera el Excel de citas de un medico entre dos fechas.
     *
     * @param medicoId id del medico.
     * @param desde    fecha inicial.
     * @param hasta    fecha final.
     * @return bytes del archivo xlsx.
     */
    byte[] reporteCitasExcel(Long medicoId, LocalDate desde, LocalDate hasta);
}
