
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import {
  FormulaMedica,
  FormulaMedicaRq,
  FormulasMedicasService,
  CitaFormula
} from './service/formulas-medicas.service';

import { Medicamento } from '../medicamentos/service/medicamentos.service';

@Component({
  selector: 'app-formulas-medicas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './formulas-medicas.component.html',
  styleUrl: './formulas-medicas.component.scss'
})
export class FormulasMedicasComponent implements OnInit {
  formulas: FormulaMedica[] = [];
  citas: CitaFormula[] = [];
  medicamentos: Medicamento[] = [];

  cargando = false;
  cargandoOpciones = false;
  guardando = false;

  error = '';
  mensaje = '';

  mostrarFormulario = false;

  formulario: FormulaMedicaRq = this.formularioVacio();

  constructor(
    private readonly formulasService: FormulasMedicasService
  ) {}

  ngOnInit(): void {
    this.listarFormulas();
  }

  private formularioVacio(): FormulaMedicaRq {
    return {
      citaId: 0,
      medicamentoId: 0,
      dosis: '',
      indicaciones: ''
    };
  }

  listarFormulas(): void {
    this.cargando = true;
    this.error = '';

    this.formulasService.listar().subscribe({
      next: (respuesta) => {
        this.formulas = respuesta ?? [];
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al listar fórmulas médicas:', err);

        this.error = this.obtenerMensajeError(
          err,
          'No fue posible cargar las fórmulas médicas.'
        );

        this.cargando = false;
      }
    });
  }

  nuevaFormula(): void {
    this.formulario = this.formularioVacio();
    this.mostrarFormulario = true;
    this.error = '';
    this.mensaje = '';

    this.cargarOpciones();
  }

  private cargarOpciones(): void {
    this.cargandoOpciones = true;

    let medicamentosCargados = false;
    let citasCargadas = false;

    this.formulasService.listarMedicamentos().subscribe({
      next: (respuesta) => {
        this.medicamentos = respuesta ?? [];
        medicamentosCargados = true;
        finalizarCarga();
      },
      error: (err) => {
        console.error('Error al cargar medicamentos:', err);

        this.error = this.obtenerMensajeError(
          err,
          'No fue posible cargar los medicamentos.'
        );

        medicamentosCargados = true;
        finalizarCarga();
      }
    });

    const hoy = new Date();

    const inicio = new Date(hoy);
    inicio.setFullYear(inicio.getFullYear() - 1);

    const fin = new Date(hoy);
    fin.setFullYear(fin.getFullYear() + 1);

    this.formulasService.listarCitas(
      this.formatearFecha(inicio),
      this.formatearFecha(fin)
    ).subscribe({
      next: (respuesta) => {
        this.citas = respuesta ?? [];
        citasCargadas = true;
        finalizarCarga();
      },
      error: (err) => {
        console.error('Error al cargar citas:', err);

        this.error = this.obtenerMensajeError(
          err,
          'No fue posible cargar las citas.'
        );

        citasCargadas = true;
        finalizarCarga();
      }
    });

    const finalizarCarga = (): void => {
      if (medicamentosCargados && citasCargadas) {
        this.cargandoOpciones = false;
      }
    };
  }

  private formatearFecha(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');

    return `${anio}-${mes}-${dia}`;
  }

  obtenerNombreMedicamento(medicamentoId: number): string {
    const medicamento = this.medicamentos.find(
      item => item.id === medicamentoId
    );

    return medicamento
      ? medicamento.nombre
      : `Medicamento no disponible (${medicamentoId})`;
  }

  obtenerDescripcionCita(citaId: number): string {
    const cita = this.citas.find(
      item => item.id === citaId
    );

    if (!cita) {
      return `Cita no disponible (${citaId})`;
    }

    const fecha = cita.fechaHora
      ? new Date(cita.fechaHora).toLocaleString('es-CO')
      : 'Fecha no disponible';

    const motivo = cita.motivo || 'Consulta veterinaria';

    return `${fecha} — ${motivo}`;
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
    this.formulario = this.formularioVacio();
    this.error = '';
  }

  guardarFormula(): void {
    this.error = '';
    this.mensaje = '';

    const citaId = Number(this.formulario.citaId);
    const medicamentoId = Number(this.formulario.medicamentoId);
    const dosis = this.formulario.dosis.trim();
    const indicaciones = this.formulario.indicaciones.trim();

    if (
      !Number.isInteger(citaId) ||
      citaId <= 0 ||
      !Number.isInteger(medicamentoId) ||
      medicamentoId <= 0 ||
      !dosis ||
      !indicaciones
    ) {
      this.error =
        'Selecciona una cita y un medicamento, e ingresa la dosis y las indicaciones.';
      return;
    }

    const solicitud: FormulaMedicaRq = {
      citaId,
      medicamentoId,
      dosis,
      indicaciones
    };

    this.guardando = true;

    this.formulasService.guardar(solicitud).subscribe({
      next: (respuesta) => {
        this.guardando = false;
        this.mostrarFormulario = false;
        this.formulario = this.formularioVacio();

        this.mensaje = this.obtenerMensajeRespuesta(
          respuesta,
          'Fórmula médica registrada exitosamente.'
        );

        this.listarFormulas();
      },
      error: (err) => {
        console.error('Error al registrar fórmula médica:', err);

        this.error = this.obtenerMensajeError(
          err,
          'No fue posible registrar la fórmula médica.'
        );

        this.guardando = false;
      }
    });
  }

  private obtenerMensajeError(
    err: any,
    mensajePredeterminado: string
  ): string {
    const mensaje =
      err?.error?.message ??
      err?.error?.mensaje ??
      err?.message;

    return typeof mensaje === 'string' && mensaje.trim()
      ? mensaje
      : mensajePredeterminado;
  }

  private obtenerMensajeRespuesta(
    respuesta: unknown,
    mensajePredeterminado: string
  ): string {
    if (respuesta && typeof respuesta === 'object') {
      const resultado = respuesta as {
        mensaje?: string;
        message?: string;
      };

      return resultado.mensaje ??
        resultado.message ??
        mensajePredeterminado;
    }

    return mensajePredeterminado;
  }
}
