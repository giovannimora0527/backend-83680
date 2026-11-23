
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { environment } from 'src/environments/environment';

interface Anotacion {
  id: number;
  historiaId: number;
  medicoId: number;
  fecha: string;
  descripcion: string;
}

interface HistoriaMedica {
  id: number;
  pacienteId: number;
  fechaCreacion: string;
  anotaciones: Anotacion[];
}

interface Medico {
  id: number;
  nombres?: string;
  apellidos?: string;
  nombre?: string;
  apellido?: string;
  primerNombre?: string;
  segundoNombre?: string;
  primerApellido?: string;
  segundoApellido?: string;
  [key: string]: unknown;
}

@Component({
  selector: 'app-anotaciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './anotaciones.component.html',
  styleUrl: './anotaciones.component.scss'
})
export class AnotacionesComponent implements OnInit {
  anotaciones: Anotacion[] = [];
  anotacionesFiltradas: Anotacion[] = [];
  medicos: Medico[] = [];

  busqueda = '';
  cargando = false;
  error = '';
  mensaje = '';

  anotacionSeleccionada: Anotacion | null = null;
  descripcionEditada = '';
  guardando = false;

  private readonly api =
    `${environment.apiUrlAuth}/historia-medica`;

  private readonly medicoApi =
    `${environment.apiUrlAuth}/medico`;

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.cargarMedicos();
    this.listarAnotaciones();
  }

  private headers(): HttpHeaders {
    const token = localStorage.getItem('token') || '';

    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    return headers;
  }

  cargarMedicos(): void {
    this.http.get<Medico[]>(
      `${this.medicoApi}/listar`,
      { headers: this.headers() }
    ).subscribe({
      next: (respuesta) => {
        this.medicos = respuesta ?? [];
      },
      error: (err) => {
        console.error('Error al cargar médicos:', err);
      }
    });
  }

  obtenerNombreMedico(medicoId: number): string {
    const medico = this.medicos.find(
      item => Number(item.id) === Number(medicoId)
    );

    if (!medico) {
      return 'Médico no disponible';
    }

    const nombreCompleto = [
      medico.nombres,
      medico.segundoNombre,
      medico.apellidos,
      medico.segundoApellido
    ].filter(Boolean).join(' ').trim();

    if (nombreCompleto) {
      return nombreCompleto;
    }

    const nombreAlternativo = [
      medico.nombre,
      medico.primerNombre,
      medico.apellido,
      medico.primerApellido
    ].filter(Boolean).join(' ').trim();

    return nombreAlternativo || 'Médico sin nombre';
  }

  listarAnotaciones(): void {
    this.cargando = true;
    this.error = '';
    this.mensaje = '';

    const hoy = new Date();
    const fechaInicio = new Date(hoy);
    fechaInicio.setFullYear(fechaInicio.getFullYear() - 10);

    const fechaFin = new Date(hoy);
    fechaFin.setFullYear(fechaFin.getFullYear() + 1);

    const params = new HttpParams()
      .set('fechaIni', this.formatearFecha(fechaInicio, false))
      .set('fechaFin', this.formatearFecha(fechaFin, true));

    this.http.get<HistoriaMedica[]>(
      `${this.api}/listar`,
      {
        params,
        headers: this.headers()
      }
    ).subscribe({
      next: (historias) => {
        this.anotaciones = (historias ?? [])
          .flatMap(historia => historia.anotaciones ?? [])
          .sort((a, b) =>
            new Date(b.fecha).getTime() -
            new Date(a.fecha).getTime()
          );

        this.filtrarAnotaciones();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar anotaciones:', err);

        this.error =
          err?.error?.message ||
          err?.error?.mensaje ||
          'No fue posible cargar las anotaciones.';

        this.cargando = false;
      }
    });
  }

  private formatearFecha(fecha: Date, finalDelDia: boolean): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    const hora = finalDelDia ? '23:59:59' : '00:00:00';

    return `${anio}-${mes}-${dia}T${hora}`;
  }

  filtrarAnotaciones(): void {
    const termino = this.busqueda.trim().toLowerCase();

    this.anotacionesFiltradas = this.anotaciones.filter(anotacion =>
      anotacion.descripcion?.toLowerCase().includes(termino) ||
      String(anotacion.historiaId).includes(termino) ||
      this.obtenerNombreMedico(anotacion.medicoId)
        .toLowerCase().includes(termino)
    );
  }

  editarAnotacion(anotacion: Anotacion): void {
    this.anotacionSeleccionada = anotacion;
    this.descripcionEditada = anotacion.descripcion;
    this.error = '';
    this.mensaje = '';
  }

  cancelarEdicion(): void {
    this.anotacionSeleccionada = null;
    this.descripcionEditada = '';
    this.error = '';
  }

  guardarEdicion(): void {
    if (!this.anotacionSeleccionada) {
      return;
    }

    const descripcion = this.descripcionEditada.trim();

    if (!descripcion) {
      this.error = 'La descripción de la anotación es obligatoria.';
      return;
    }

    this.error = '';
    this.mensaje = '';
    this.guardando = true;

    const params = new HttpParams().set(
      'id',
      String(this.anotacionSeleccionada.id)
    );

    this.http.put(
      `${this.api}/anotacion/actualizar`,
      { descripcion },
      {
        params,
        headers: this.headers()
      }
    ).subscribe({
      next: (respuesta: any) => {
        const idActual = this.anotacionSeleccionada!.id;

        this.anotaciones = this.anotaciones.map(anotacion =>
          anotacion.id === idActual
            ? { ...anotacion, descripcion }
            : anotacion
        );

        this.filtrarAnotaciones();
        this.anotacionSeleccionada = null;
        this.descripcionEditada = '';
        this.guardando = false;

        this.mensaje =
          respuesta?.mensaje ||
          respuesta?.message ||
          'Anotación actualizada correctamente.';
      },
      error: (err) => {
        console.error('Error al actualizar anotación:', err);

        this.error =
          err?.error?.message ||
          err?.error?.mensaje ||
          'No fue posible actualizar la anotación.';

        this.guardando = false;
      }
    });
  }
}
