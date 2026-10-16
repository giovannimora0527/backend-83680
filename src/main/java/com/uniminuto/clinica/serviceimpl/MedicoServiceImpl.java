package com.uniminuto.clinica.serviceimpl;

import com.uniminuto.clinica.entity.Especializacion;
import com.uniminuto.clinica.entity.Medico;
import com.uniminuto.clinica.exception.BadRequestException;
import com.uniminuto.clinica.models.MedicoRq;
import com.uniminuto.clinica.models.MiRespuestaRS;
import com.uniminuto.clinica.repository.EspecializacionRepository;
import com.uniminuto.clinica.repository.MedicoRepository;
import com.uniminuto.clinica.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementacion de la logica de los medicos.
 */
@Service
public class MedicoServiceImpl implements MedicoService {

    /** Tipos de documento permitidos para un medico (mayor de edad). */
    private static final List<String> TIPOS_DOCUMENTO = List.of("CC", "CE", "PA");

    /** Repositorio de medicos. */
    @Autowired
    private MedicoRepository medicoRepository;

    /** Repositorio de especializaciones. */
    @Autowired
    private EspecializacionRepository especializacionRepository;

    @Override
    public List<Medico> listarMedicos() {
        return medicoRepository.findAllByOrderByNombresAscApellidosAsc();
    }

    @Override
    public MiRespuestaRS guardarMedico(MedicoRq medicoRq) {
        // Paso 1. Validar los datos
        this.validarObjetoMedico(medicoRq);

        // Paso 2. Evitar duplicados de documento y de registro profesional
        if (medicoRepository.existsByNumeroDocumento(medicoRq.getNumeroDocumento().trim())) {
            throw new BadRequestException("Ya existe un médico con ese número de documento");
        }
        if (medicoRepository.existsByRegistroProfesional(medicoRq.getRegistroProfesional().trim())) {
            throw new BadRequestException("Ya existe un médico con ese registro profesional");
        }

        // Paso 3. Crear y guardar
        Medico medico = new Medico();
        this.copiarDatos(medicoRq, medico);
        medicoRepository.save(medico);
        return this.respuestaExito("Médico guardado correctamente");
    }

    @Override
    public MiRespuestaRS actualizarMedico(MedicoRq medicoRq) {
        // Paso 1. Validar los datos y buscar el medico
        this.validarObjetoMedico(medicoRq);
        if (medicoRq.getMedicoId() == null) {
            throw new BadRequestException("El ID del médico no puede ser nulo");
        }
        Optional<Medico> optMedico = medicoRepository.findById(medicoRq.getMedicoId());
        if (optMedico.isEmpty()) {
            throw new BadRequestException("Médico no encontrado");
        }

        // Paso 2. El documento y el registro no pueden quedar iguales a los de otro medico
        if (medicoRepository.existsByNumeroDocumentoAndIdNot(
                medicoRq.getNumeroDocumento().trim(), medicoRq.getMedicoId())) {
            throw new BadRequestException("Ya existe otro médico con ese número de documento");
        }
        if (medicoRepository.existsByRegistroProfesionalAndIdNot(
                medicoRq.getRegistroProfesional().trim(), medicoRq.getMedicoId())) {
            throw new BadRequestException("Ya existe otro médico con ese registro profesional");
        }

        // Paso 3. Actualizar y guardar
        Medico medico = optMedico.get();
        this.copiarDatos(medicoRq, medico);
        medicoRepository.save(medico);
        return this.respuestaExito("Médico actualizado correctamente");
    }

    /**
     * Pasa los datos del request a la entidad (busca la especializacion).
     *
     * @param medicoRq datos que llegan.
     * @param medico   entidad a llenar.
     */
    private void copiarDatos(MedicoRq medicoRq, Medico medico) {
        Optional<Especializacion> optEspecializacion = especializacionRepository.findById(medicoRq.getEspecializacionId());
        if (optEspecializacion.isEmpty()) {
            throw new BadRequestException("Especialización no encontrada");
        }
        medico.setTipoDocumento(medicoRq.getTipoDocumento().toUpperCase());
        medico.setNumeroDocumento(medicoRq.getNumeroDocumento().trim());
        medico.setNombres(medicoRq.getNombres().trim());
        medico.setApellidos(medicoRq.getApellidos().trim());
        medico.setTelefono(medicoRq.getTelefono());
        medico.setRegistroProfesional(medicoRq.getRegistroProfesional().trim().toUpperCase());
        medico.setEspecializacion(optEspecializacion.get());
    }

    /**
     * Valida los campos obligatorios y su formato.
     *
     * @param medicoRq datos del medico.
     */
    private void validarObjetoMedico(MedicoRq medicoRq) {
        if (medicoRq == null) {
            throw new BadRequestException("Los datos del médico son obligatorios");
        }
        if (medicoRq.getTipoDocumento() == null
                || !TIPOS_DOCUMENTO.contains(medicoRq.getTipoDocumento().toUpperCase())) {
            throw new BadRequestException("El tipo de documento debe ser CC, CE o PA");
        }
        if (medicoRq.getNumeroDocumento() == null || !medicoRq.getNumeroDocumento().trim().matches("\\d{5,20}")) {
            throw new BadRequestException("El número de documento debe tener entre 5 y 20 dígitos");
        }
        if (medicoRq.getNombres() == null || medicoRq.getNombres().isBlank()) {
            throw new BadRequestException("Los nombres son obligatorios");
        }
        if (medicoRq.getApellidos() == null || medicoRq.getApellidos().isBlank()) {
            throw new BadRequestException("Los apellidos son obligatorios");
        }
        if (medicoRq.getRegistroProfesional() == null || medicoRq.getRegistroProfesional().isBlank()) {
            throw new BadRequestException("El registro profesional es obligatorio");
        }
        if (medicoRq.getRegistroProfesional().trim().length() > 50) {
            throw new BadRequestException("El registro profesional no puede tener más de 50 caracteres");
        }
        if (medicoRq.getEspecializacionId() == null) {
            throw new BadRequestException("Debe seleccionar una especialización");
        }
        if (medicoRq.getTelefono() != null && !medicoRq.getTelefono().isBlank()
                && !medicoRq.getTelefono().matches("\\d{7,15}")) {
            throw new BadRequestException("El teléfono debe tener entre 7 y 15 dígitos");
        }
    }

    /**
     * Arma la respuesta de exito.
     *
     * @param mensaje mensaje para el usuario.
     * @return respuesta.
     */
    private MiRespuestaRS respuestaExito(String mensaje) {
        MiRespuestaRS rta = new MiRespuestaRS();
        rta.setStatus(200);
        rta.setMessage(mensaje);
        return rta;
    }
}
