
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import Swal from 'sweetalert2';

import { CitasService } from './service/citas.service';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-citas',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './citas.component.html',
  styleUrl: './citas.component.scss'
})
export class CitasComponent implements OnInit {
  form: FormGroup;

  citas: any[] = [];
  clientes: any[] = [];
  mascotas: any[] = [];
  medicos: any[] = [];

  busqueda = '';
  pagina = 1;
  readonly porPagina = 10;
  editando = false;
  citaId: number | null = null;

  readonly estados = [
    'Pendiente',
    'Confirmada',
    'Atendida',
    'Cancelada',
    'Programada'
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly citasService: CitasService,
    private readonly backend: BackendService
  ) {
    this.form = this.fb.group({
      clienteId: ['', Validators.required],
      mascotaId: ['', Validators.required],
      medicoId: ['', Validators.required],
      fecha: ['', Validators.required],
      hora: ['', Validators.required],
      motivo: ['', [Validators.required, Validators.maxLength(500)]],
      estado: ['Programada', Validators.required]
    });
  }

  ngOnInit(): void {
    this.cargarClientes();
    this.cargarMascotas();
    this.cargarMedicos();
    this.cargarCitas();
  }

  get citasFiltradas(): any[] {
    const texto = this.busqueda.trim().toLowerCase();

    if (!texto) {
      return this.citas;
    }

    return this.citas.filter(cita =>
      [
        cita.clienteNombre,
        cita.mascotaNombre,
        cita.medicoNombre,
        cita.estado,
        cita.motivo,
        cita.fechaHora
      ].some(valor =>
        String(valor ?? '').toLowerCase().includes(texto)
      )
    );
  }

  get citasPaginadas(): any[] {
    const inicio = (this.pagina - 1) * this.porPagina;
    return this.citasFiltradas.slice(inicio, inicio + this.porPagina);
  }

  get totalPaginas(): number {
    return Math.ceil(this.citasFiltradas.length / this.porPagina);
  }

  cargarClientes(): void {
    this.backend.get<any[]>(
      environment.apiUrlAuth,
      'mascota',
      'listar'
    ).subscribe({
      next: datos => {
        const clientesUnicos = new Map<number, any>();

        (datos ?? []).forEach(mascota => {
          const cliente = mascota.cliente;
          const id = Number(cliente?.id ?? cliente?.clienteId);

          if (Number.isInteger(id) && id > 0 && !clientesUnicos.has(id)) {
            clientesUnicos.set(id, {
              id,
              clienteId: id,
              nombres: cliente?.nombres ?? '',
              apellidos: cliente?.apellidos ?? ''
            });
          }
        });

        this.clientes = Array.from(clientesUnicos.values());
        this.actualizarNombresCitas();
      },
      error: error => {
        console.error('Error al cargar clientes:', error);
        this.clientes = [];

        Swal.fire(
          'Error',
          'No se pudieron cargar los clientes desde las mascotas.',
          'error'
        );
      }
    });
  }

  cargarMascotas(): void {
    this.backend.get<any[]>(
      environment.apiUrlAuth,
      'mascota',
      'listar'
    ).subscribe({
      next: datos => {
        this.mascotas = datos ?? [];
        this.actualizarNombresCitas();
      },
      error: error => {
        console.error('Error al cargar mascotas:', error);
        this.mascotas = [];

        Swal.fire(
          'Error',
          'No se pudieron cargar las mascotas.',
          'error'
        );
      }
    });
  }

  cargarMedicos(): void {
    this.backend.get<any[]>(
      environment.apiUrlAuth,
      'medico',
      'listar'
    ).subscribe({
      next: datos => {
        this.medicos = datos ?? [];
        this.actualizarNombresCitas();
      },
      error: error => {
        console.error('Error al cargar médicos:', error);
        this.medicos = [];
      }
    });
  }

  get mascotasDelCliente(): any[] {
    const clienteId = Number(this.form.get('clienteId')?.value);

    if (!clienteId) {
      return [];
    }

    return this.mascotas.filter(mascota =>
      Number(
        mascota.cliente?.id ??
        mascota.cliente?.clienteId ??
        mascota.clienteId
      ) === clienteId
    );
  }

  cambioCliente(): void {
    this.form.patchValue({ mascotaId: '' });
  }

  cargarCitas(): void {
    const ahora = new Date();
    const inicio = new Date(ahora.getFullYear(), 0, 1);
    const fin = new Date(ahora.getFullYear() + 1, 0, 1);

    this.citasService.listar(
      this.formatearFechaHora(inicio),
      this.formatearFechaHora(fin)
    ).subscribe({
      next: respuesta => {
        this.citas = Array.isArray(respuesta) ? respuesta : [];
        this.actualizarNombresCitas();

        this.citas.sort((a, b) =>
          String(b.fechaHora ?? '').localeCompare(
            String(a.fechaHora ?? '')
          )
        );

        this.pagina = 1;
      },
      error: error => {
        console.error('Error al cargar citas:', error);

        Swal.fire(
          'Error',
          'No se pudieron cargar las citas. Revisa la terminal del backend.',
          'error'
        );
      }
    });
  }

  // Formato requerido para consultar citas: yyyy-MM-ddTHH:mm:ss
  private formatearFechaHora(fecha: Date): string {
    const dos = (valor: number) =>
      String(valor).padStart(2, '0');

    return `${fecha.getFullYear()}-${dos(fecha.getMonth() + 1)}-${dos(fecha.getDate())}T${dos(fecha.getHours())}:${dos(fecha.getMinutes())}:${dos(fecha.getSeconds())}`;
  }

  private actualizarNombresCitas(): void {
    this.citas = this.citas.map((cita: any) => ({
      ...cita,
      clienteNombre: this.nombreCliente(cita.clienteId),
      mascotaNombre: this.nombreMascota(cita.mascotaId),
      medicoNombre: this.nombreMedico(cita.medicoId)
    }));
  }

  private nombreCliente(id: number): string {
    const cliente = this.clientes.find(c =>
      Number(c.id ?? c.clienteId) === Number(id)
    );

    return cliente
      ? `${cliente.nombres ?? ''} ${cliente.apellidos ?? ''}`.trim()
      : `Cliente ${id ?? ''}`;
  }

  private nombreMascota(id: number): string {
    const mascota = this.mascotas.find(m =>
      Number(m.mascotaId ?? m.id) === Number(id)
    );

    return mascota?.nombreMascota ?? `Mascota ${id ?? ''}`;
  }

  private nombreMedico(id: number): string {
    const medico = this.medicos.find(m =>
      Number(m.id ?? m.medicoId) === Number(id)
    );

    return medico
      ? `${medico.nombres ?? medico.nombre ?? ''} ${medico.apellidos ?? ''}`.trim()
      : `Médico ${id ?? ''}`;
  }

  nuevaCita(): void {
    this.editando = false;
    this.citaId = null;

    this.form.reset({
      clienteId: '',
      mascotaId: '',
      medicoId: '',
      fecha: '',
      hora: '',
      motivo: '',
      estado: 'Programada'
    });
  }

  editarCita(cita: any): void {
    const fecha = String(cita.fechaHora ?? '').replace(' ', 'T');

    this.editando = true;
    this.citaId = Number(cita.id ?? cita.citaId);

    this.form.patchValue({
      clienteId: cita.clienteId,
      mascotaId: cita.mascotaId,
      medicoId: cita.medicoId,
      fecha: fecha.substring(0, 10),
      hora: fecha.substring(11, 16),
      motivo: cita.motivo,
      estado: cita.estado
    });

    window.scrollTo({
      top: 0,
      behavior: 'smooth'
    });
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();

      Swal.fire(
        'Formulario incompleto',
        'Completa todos los campos.',
        'warning'
      );

      return;
    }

    const f = this.form.getRawValue();

    // El backend valida el guardado con yyyy-MM-dd HH:mm:ss.
    const fechaHora = `${f.fecha} ${f.hora}:00`;

    const fechaSeleccionada = new Date(`${f.fecha}T${f.hora}:00`);

    if (Number.isNaN(fechaSeleccionada.getTime())) {
      Swal.fire(
        'Fecha inválida',
        'Verifica la fecha y la hora seleccionadas.',
        'warning'
      );

      return;
    }

    if (fechaSeleccionada.getTime() < Date.now()) {
      Swal.fire(
        'Fecha inválida',
        'No puedes registrar una cita en el pasado.',
        'warning'
      );

      return;
    }

    const datos = {
      clienteId: Number(f.clienteId),
      mascotaId: Number(f.mascotaId),
      medicoId: Number(f.medicoId),
      fechaHora,
      estado: f.estado,
      motivo: String(f.motivo).trim()
    };

    const peticion = this.editando && this.citaId
      ? this.citasService.actualizar(this.citaId, datos)
      : this.citasService.guardar(datos);

    peticion.subscribe({
      next: (respuesta: any) => {
        Swal.fire(
          'Operación exitosa',
          respuesta?.mensaje ?? 'Cita guardada correctamente.',
          'success'
        );

        this.nuevaCita();
        this.cargarCitas();
      },
      error: error => {
        console.error('Error al guardar cita:', error);

        Swal.fire(
          'No se pudo guardar',
          error?.error?.message ??
          error?.error?.mensaje ??
          'Verifica los datos y el estado del servidor.',
          'error'
        );
      }
    });
  }

  cancelarEdicion(): void {
    this.nuevaCita();
  }

  buscar(event: Event): void {
    this.busqueda = (event.target as HTMLInputElement).value;
    this.pagina = 1;
  }
}
