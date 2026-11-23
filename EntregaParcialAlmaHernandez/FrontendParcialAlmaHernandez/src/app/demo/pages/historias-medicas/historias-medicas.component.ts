
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import {
  AnotacionHistoria,
  HistoriaMedica,
  HistoriaMedicaService,
  Medico
} from '../../../historias-medicas/historia-medica.service';
import {
  Paciente,
  PacientesService
} from '../pacientes/service/pacientes.service';

@Component({
  selector: 'app-historias-medicas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './historias-medicas.component.html',
  styleUrl: './historias-medicas.component.scss'
})
export class HistoriasMedicasComponent implements OnInit {

  pacienteId: number | null = null;

  pacientes: Paciente[] = [];
  medicos: Medico[] = [];
  historias: HistoriaMedica[] = [];

  fechaIni = '';
  fechaFin = '';

  cargando = false;
  cargandoPacientes = false;
  cargandoMedicos = false;
  guardandoHistoria = false;
  guardandoAnotacion = false;
  anotacionActualizando: number | null = null;

  mensaje = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';

  anotacionEditando: number | null = null;
  descripcionEditada = '';

  historiaAnotacionId: number | null = null;
  medicoAnotacionId: number | null = null;
  descripcionNuevaAnotacion = '';

  constructor(
    private readonly historiaService: HistoriaMedicaService,
    private readonly pacientesService: PacientesService
  ) {}

  ngOnInit(): void {
    const hoy = new Date();
    const haceTreintaDias = new Date();

    haceTreintaDias.setDate(hoy.getDate() - 30);

    this.fechaIni = this.formatearFechaInput(haceTreintaDias);
    this.fechaFin = this.formatearFechaInput(hoy);

    this.listarHistorias();
    this.listarMedicos();
    this.listarPacientes();
  }

  private formatearFechaInput(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');

    return `${anio}-${mes}-${dia}T00:00`;
  }

  private convertirFechaBackend(
    fecha: string,
    esFinal: boolean
  ): string {
    if (!fecha) {
      return '';
    }

    if (fecha.length === 16) {
      return `${fecha}:00`;
    }

    return fecha;
  }

  listarHistorias(): void {
    this.mensaje = '';

    if (!this.fechaIni || !this.fechaFin) {
      this.mostrarMensaje(
        'Selecciona la fecha inicial y la fecha final.',
        'error'
      );
      return;
    }

    if (new Date(this.fechaIni) > new Date(this.fechaFin)) {
      this.mostrarMensaje(
        'La fecha inicial no puede ser posterior a la fecha final.',
        'error'
      );
      return;
    }

    this.cargando = true;

    const fechaInicial = this.convertirFechaBackend(
      this.fechaIni,
      false
    );

    const fechaFinal = this.convertirFechaBackend(
      this.fechaFin,
      true
    );

    this.historiaService.listarHistorias(
      fechaInicial,
      fechaFinal
    ).subscribe({
      next: (historias) => {
        this.historias = historias ?? [];
        this.cargando = false;

        if (
          this.historiaAnotacionId !== null &&
          !this.historias.some(
            historia => historia.id === Number(this.historiaAnotacionId)
          )
        ) {
          this.historiaAnotacionId = null;
        }

        if (this.historias.length === 0) {
          this.mostrarMensaje(
            'No se encontraron historias médicas en ese rango de fechas.',
            'info'
          );
        } else {
          this.mensaje = '';
        }
      },
      error: (error) => {
        console.error(
          'Error al listar historias médicas:',
          error
        );

        this.cargando = false;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible consultar las historias médicas.'
          ),
          'error'
        );
      }
    });
  }

  listarPacientes(): void {
    this.cargandoPacientes = true;

    this.pacientesService.listarPacientes().subscribe({
      next: (pacientes: Paciente[]) => {
        this.pacientes = pacientes ?? [];
        this.cargandoPacientes = false;

        if (
          this.pacienteId !== null &&
          !this.pacientes.some(
            paciente => paciente.id === Number(this.pacienteId)
          )
        ) {
          this.pacienteId = null;
        }
      },
      error: (error) => {
        console.error('Error al listar pacientes:', error);
        this.pacientes = [];
        this.cargandoPacientes = false;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible cargar la lista de pacientes.'
          ),
          'error'
        );
      }
    });
  }

  listarMedicos(): void {
    this.cargandoMedicos = true;

    this.historiaService.listarMedicos().subscribe({
      next: (medicos) => {
        this.medicos = medicos ?? [];
        this.cargandoMedicos = false;

        if (
          this.medicoAnotacionId !== null &&
          !this.medicos.some(
            medico => medico.id === Number(this.medicoAnotacionId)
          )
        ) {
          this.medicoAnotacionId = null;
        }
      },
      error: (error) => {
        console.error('Error al listar médicos:', error);
        this.medicos = [];
        this.cargandoMedicos = false;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible cargar la lista de médicos.'
          ),
          'error'
        );
      }
    });
  }

  nombreMedico(medico: Medico): string {
    const nombre =
      `${medico.nombres ?? ''} ${medico.apellidos ?? ''}`.trim();

    return nombre || `Médico #${medico.id}`;
  }

  nombrePaciente(paciente: Paciente): string {
    const nombre =
      `${paciente.nombres ?? ''} ${paciente.apellidos ?? ''}`.trim();

    return nombre || `Paciente #${paciente.id}`;
  }

  guardarHistoria(): void {
    this.mensaje = '';

    if (
      this.pacienteId === null ||
      !this.pacientes.some(
        paciente => paciente.id === Number(this.pacienteId)
      )
    ) {
      this.mostrarMensaje(
        'Selecciona un paciente válido.',
        'error'
      );
      return;
    }

    this.guardandoHistoria = true;

    this.historiaService.guardarHistoria(
      Number(this.pacienteId)
    ).subscribe({
      next: () => {
        this.guardandoHistoria = false;
        this.pacienteId = null;

        this.mostrarMensaje(
          'La historia médica se guardó correctamente.',
          'success'
        );

        this.listarHistorias();
      },
      error: (error) => {
        console.error(
          'Error al guardar historia médica:',
          error
        );

        this.guardandoHistoria = false;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible guardar la historia médica.'
          ),
          'error'
        );
      }
    });
  }

  guardarAnotacion(): void {
    this.mensaje = '';

    if (
      this.historiaAnotacionId === null ||
      !this.historias.some(
        historia => historia.id === Number(this.historiaAnotacionId)
      )
    ) {
      this.mostrarMensaje(
        'Selecciona una historia médica válida.',
        'error'
      );
      return;
    }

    if (
      this.medicoAnotacionId === null ||
      !this.medicos.some(
        medico => medico.id === Number(this.medicoAnotacionId)
      )
    ) {
      this.mostrarMensaje(
        'Selecciona un médico válido.',
        'error'
      );
      return;
    }

    const descripcion = this.descripcionNuevaAnotacion.trim();

    if (!descripcion) {
      this.mostrarMensaje(
        'La descripción de la anotación no puede estar vacía.',
        'error'
      );
      return;
    }

    this.guardandoAnotacion = true;

    this.historiaService.guardarAnotacion(
      Number(this.historiaAnotacionId),
      Number(this.medicoAnotacionId),
      descripcion
    ).subscribe({
      next: () => {
        this.guardandoAnotacion = false;
        this.historiaAnotacionId = null;
        this.medicoAnotacionId = null;
        this.descripcionNuevaAnotacion = '';

        this.mostrarMensaje(
          'La anotación se guardó correctamente.',
          'success'
        );

        this.listarHistorias();
      },
      error: (error) => {
        console.error(
          'Error al guardar anotación:',
          error
        );

        this.guardandoAnotacion = false;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible guardar la anotación.'
          ),
          'error'
        );
      }
    });
  }

  iniciarEdicion(anotacion: AnotacionHistoria): void {
    this.anotacionEditando = anotacion.id;
    this.descripcionEditada = anotacion.descripcion ?? '';
    this.mensaje = '';
  }

  cancelarEdicion(): void {
    this.anotacionEditando = null;
    this.descripcionEditada = '';
  }

  actualizarAnotacion(anotacion: AnotacionHistoria): void {
    const descripcion = this.descripcionEditada.trim();

    if (!descripcion) {
      this.mostrarMensaje(
        'La descripción de la anotación no puede estar vacía.',
        'error'
      );
      return;
    }

    this.anotacionActualizando = anotacion.id;

    this.historiaService.actualizarAnotacion(
      anotacion.id,
      descripcion
    ).subscribe({
      next: () => {
        anotacion.descripcion = descripcion;
        this.anotacionActualizando = null;
        this.cancelarEdicion();

        this.mostrarMensaje(
          'La anotación se actualizó correctamente.',
          'success'
        );
      },
      error: (error) => {
        console.error(
          'Error al actualizar anotación:',
          error
        );

        this.anotacionActualizando = null;

        this.mostrarMensaje(
          this.obtenerMensajeError(
            error,
            'No fue posible actualizar la anotación.'
          ),
          'error'
        );
      }
    });
  }

  private mostrarMensaje(
    mensaje: string,
    tipo: 'success' | 'error' | 'info'
  ): void {
    this.mensaje = mensaje;
    this.tipoMensaje = tipo;
  }

  private obtenerMensajeError(
    error: any,
    mensajePredeterminado: string
  ): string {
    if (
      typeof error?.error === 'string' &&
      error.error.trim()
    ) {
      return error.error;
    }

    if (typeof error?.error?.message === 'string') {
      return error.error.message;
    }

    if (typeof error?.error?.mensaje === 'string') {
      return error.error.mensaje;
    }

    if (error?.status === 0) {
      return 'No se pudo conectar con el backend. Verifica que Spring Boot esté ejecutándose en el puerto 8000.';
    }

    if (error?.status === 400) {
      return 'La solicitud contiene datos inválidos. Revisa los campos e inténtalo nuevamente.';
    }

    if (error?.status === 404) {
      return 'No se encontró la ruta solicitada en el backend.';
    }

    if (error?.status >= 500) {
      return 'El backend presentó un error interno. Revisa la consola de Spring Boot.';
    }

    return mensajePredeterminado;
  }
}
