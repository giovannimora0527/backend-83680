
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import {
  Medicamento,
  MedicamentoRq,
  MedicamentosService
} from './service/medicamentos.service';

@Component({
  selector: 'app-medicamentos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './medicamentos.component.html',
  styleUrl: './medicamentos.component.scss'
})
export class MedicamentosComponent implements OnInit {
  medicamentos: Medicamento[] = [];

  cargando = false;
  guardando = false;
  error = '';
  mensaje = '';

  mostrarFormulario = false;
  editando = false;

  formulario: MedicamentoRq = this.formularioVacio();

  constructor(
    private readonly medicamentosService: MedicamentosService
  ) {}

  ngOnInit(): void {
    this.listarMedicamentos();
  }

  private formularioVacio(): MedicamentoRq {
    return {
      nombre: '',
      descripcion: '',
      presentacion: '',
      fechaCompra: '',
      fechaVence: '',
      codigo: '',
      tipoProducto: 'MEDICAMENTO',
      cantidadActual: 0,
      stockMinimo: 0
    };
  }

  listarMedicamentos(): void {
    this.cargando = true;
    this.error = '';

    this.medicamentosService.listar().subscribe({
      next: (respuesta) => {
        this.medicamentos = respuesta ?? [];
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al listar medicamentos:', err);
        this.error = this.obtenerMensajeError(
          err,
          'No fue posible cargar los medicamentos.'
        );
        this.cargando = false;
      }
    });
  }

  nuevoMedicamento(): void {
    this.formulario = this.formularioVacio();
    this.editando = false;
    this.mostrarFormulario = true;
    this.error = '';
    this.mensaje = '';
  }

  editarMedicamento(medicamento: Medicamento): void {
    this.formulario = {
      id: medicamento.id,
      nombre: medicamento.nombre ?? '',
      descripcion: medicamento.descripcion ?? '',
      presentacion: medicamento.presentacion ?? '',
      fechaCompra: medicamento.fechaCompra ?? '',
      fechaVence: medicamento.fechaVence ?? '',
      codigo: medicamento.codigo ?? '',
      tipoProducto: medicamento.tipoProducto ?? 'MEDICAMENTO',
      precioCompra: medicamento.precioCompra,
      precioVenta: medicamento.precioVenta,
      cantidadActual: medicamento.cantidadActual,
      stockMinimo: medicamento.stockMinimo,
      fechaIngreso: medicamento.fechaIngreso,
      fechaProximoPedido: medicamento.fechaProximoPedido,
      registroInvima: medicamento.registroInvima
    };

    this.editando = true;
    this.mostrarFormulario = true;
    this.error = '';
    this.mensaje = '';
  }

  cancelarFormulario(): void {
    this.mostrarFormulario = false;
    this.formulario = this.formularioVacio();
    this.editando = false;
    this.error = '';
  }

  guardarMedicamento(): void {
    this.error = '';
    this.mensaje = '';

    const nombre = this.formulario.nombre.trim();
    const descripcion = this.formulario.descripcion.trim();
    const presentacion = this.formulario.presentacion.trim();

    if (!nombre || !descripcion || !presentacion ||
        !this.formulario.fechaCompra || !this.formulario.fechaVence) {
      this.error = 'Completa todos los campos obligatorios.';
      return;
    }
    if (!/^[A-Za-z0-9]{8}$/.test(this.formulario.codigo)) {
      this.error = 'El código debe tener exactamente 8 caracteres alfanuméricos.';
      return;
    }
    if (this.formulario.tipoProducto === 'MEDICAMENTO'
        && !this.formulario.registroInvima?.trim()) {
      this.error = 'El registro Invima es obligatorio para medicamentos.';
      return;
    }

    if (this.formulario.fechaVence < this.formulario.fechaCompra) {
      this.error =
        'La fecha de vencimiento no puede ser anterior a la fecha de compra.';
      return;
    }

    const solicitud: MedicamentoRq = {
      nombre,
      descripcion,
      presentacion,
      fechaCompra: this.formulario.fechaCompra,
      fechaVence: this.formulario.fechaVence,
      codigo: this.formulario.codigo.toUpperCase(),
      tipoProducto: this.formulario.tipoProducto,
      precioCompra: this.formulario.precioCompra,
      precioVenta: this.formulario.precioVenta,
      cantidadActual: this.formulario.cantidadActual,
      stockMinimo: this.formulario.stockMinimo,
      fechaIngreso: this.formulario.fechaIngreso,
      fechaProximoPedido: this.formulario.fechaProximoPedido,
      registroInvima: this.formulario.registroInvima
    };

    this.guardando = true;

    if (this.editando) {
      if (this.formulario.id == null) {
        this.error = 'No se encontró el identificador del medicamento.';
        this.guardando = false;
        return;
      }

      solicitud.id = this.formulario.id;

      this.medicamentosService.actualizar(solicitud).subscribe({
        next: (respuesta) => {
          this.guardando = false;
          this.mostrarFormulario = false;
          this.formulario = this.formularioVacio();
          this.editando = false;
          this.mensaje = this.obtenerMensajeRespuesta(
            respuesta,
            'Medicamento actualizado exitosamente.'
          );
          this.listarMedicamentos();
        },
        error: (err) => {
          console.error('Error al actualizar medicamento:', err);
          this.error = this.obtenerMensajeError(
            err,
            'No fue posible actualizar el medicamento.'
          );
          this.guardando = false;
        }
      });
    } else {
      this.medicamentosService.guardar(solicitud).subscribe({
        next: (respuesta) => {
          this.guardando = false;
          this.mostrarFormulario = false;
          this.formulario = this.formularioVacio();
          this.mensaje = this.obtenerMensajeRespuesta(
            respuesta,
            'Medicamento registrado exitosamente.'
          );
          this.listarMedicamentos();
        },
        error: (err) => {
          console.error('Error al registrar medicamento:', err);
          this.error = this.obtenerMensajeError(
            err,
            'No fue posible registrar el medicamento.'
          );
          this.guardando = false;
        }
      });
    }
  }

  private obtenerMensajeError(err: any, mensajePredeterminado: string): string {
    const mensaje = err?.error?.message
      ?? err?.error?.mensaje
      ?? err?.message;

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

      return resultado.mensaje
        ?? resultado.message
        ?? mensajePredeterminado;
    }

    return mensajePredeterminado;
  }
}
