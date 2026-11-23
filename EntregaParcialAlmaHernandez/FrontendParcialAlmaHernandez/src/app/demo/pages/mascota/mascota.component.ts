
import { Component, OnInit } from '@angular/core';
import { CommonModule, formatDate } from '@angular/common';
import {
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  FormsModule,
  ReactiveFormsModule
} from '@angular/forms';
import { Modal } from 'bootstrap';
import Swal from 'sweetalert2';

import { MascotaServiceService } from './service/mascota-service.service';
import { Mascota } from 'src/app/models/mascota';

@Component({
  selector: 'app-mascota',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './mascota.component.html',
  styleUrl: './mascota.component.scss'
})
export class MascotaComponent implements OnInit {
  modalInstance: Modal | null = null;

  titleModal = '';
  modoFormulario = '';
  titleBoton = '';

  listMascotas: Mascota[] = [];
  listClientes: any[] = [];
  listRazas: any[] = [];

  terminoBusqueda = '';
  paginaActual = 1;
  readonly registrosPorPagina = 10;

  form!: FormGroup;
  mascotaSelected: Mascota | null = null;

  constructor(
    private readonly mascotaService: MascotaServiceService,
    private readonly formBuilder: FormBuilder
  ) {
    this.inicializarFormulario();
  }

  ngOnInit(): void {
    this.listar();
    this.cargarRazas();
    this.cargarClientes();
  }

  inicializarFormulario(): void {
    this.form = this.formBuilder.group({
      nombreMascota: ['', Validators.required],
      razaId: ['', Validators.required],
      edad: ['', [Validators.required, Validators.min(0)]],
      clienteId: ['', Validators.required]
    });
  }

  get f(): { [key: string]: AbstractControl } {
    return this.form.controls;
  }

  get mascotasFiltradas(): Mascota[] {
    const termino = this.normalizarTexto(
      this.terminoBusqueda.trim()
    );

    if (!termino) {
      return this.listMascotas;
    }

    return this.listMascotas.filter((mascota) => {
      const valores = [
        mascota.nombreMascota,
        mascota.raza?.especie,
        mascota.raza?.nombre,
        mascota.edad,
        `${mascota.cliente?.nombres ?? ''} ${mascota.cliente?.apellidos ?? ''}`,
        mascota.fechaRegistro
          ? formatDate(
              mascota.fechaRegistro,
              'yyyy-MM-dd HH:mm',
              'en-US'
            )
          : ''
      ];

      return valores.some((valor) =>
        this.normalizarTexto(
          String(valor ?? '')
        ).includes(termino)
      );
    });
  }

  get mascotasPaginadas(): Mascota[] {
    const inicio =
      (this.paginaActual - 1) * this.registrosPorPagina;

    return this.mascotasFiltradas.slice(
      inicio,
      inicio + this.registrosPorPagina
    );
  }

  get totalPaginas(): number {
    return Math.ceil(
      this.mascotasFiltradas.length /
      this.registrosPorPagina
    );
  }

  get paginas(): number[] {
    return Array.from(
      { length: this.totalPaginas },
      (_, indice) => indice + 1
    );
  }

  private normalizarTexto(texto: string): string {
    return texto
      .toLocaleLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }

  listar(): void {
    this.mascotaService.getMascotas().subscribe({
      next: (data) => {
        this.listMascotas = data ?? [];
        this.paginaActual = 1;
      },
      error: (error) => {
        console.error('Error al obtener las mascotas:', error);

        Swal.fire(
          'Error',
          'No se pudieron cargar las mascotas.',
          'error'
        );
      }
    });
  }

  cargarRazas(): void {
    this.mascotaService.getRazas().subscribe({
      next: (data) => {
        this.listRazas = data ?? [];
      },
      error: (error) => {
        console.error('Error al cargar las razas:', error);

        Swal.fire(
          'Error',
          'No se pudieron cargar las razas desde la base de datos.',
          'error'
        );
      }
    });
  }

  cargarClientes(): void {
    this.mascotaService.getClientes().subscribe({
      next: (data) => {
        this.listClientes = data ?? [];
      },
      error: (error) => {
        console.error('Error al cargar los clientes:', error);

        Swal.fire(
          'Error',
          'No se pudieron cargar los clientes desde la base de datos.',
          'error'
        );
      }
    });
  }

  actualizarBusqueda(event: Event): void {
    this.terminoBusqueda =
      (event.target as HTMLInputElement).value;

    this.paginaActual = 1;
  }

  cambiarPagina(pagina: number): void {
    if (
      pagina >= 1 &&
      pagina <= this.totalPaginas
    ) {
      this.paginaActual = pagina;
    }
  }

  nuevaMascota(modo: string): void {
    this.modoFormulario = modo;
    this.mascotaSelected = null;

    this.titleModal = modo === 'C'
      ? 'Crear Mascota'
      : 'Editar Mascota';

    this.resetFormulario();
    this.openModal(modo);
  }

  resetFormulario(): void {
    this.form.reset({
      nombreMascota: '',
      razaId: '',
      edad: '',
      clienteId: ''
    });

    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  abrirEdicion(mascota: Mascota): void {
    this.mascotaSelected = mascota;
    this.modoFormulario = 'E';

    const raza: any = mascota.raza;
    const cliente: any = mascota.cliente;

    const razaId = Number(
      raza?.razaId ?? raza?.id
    );

    const clienteId = Number(
      cliente?.clienteId ?? cliente?.id
    );

    this.form.patchValue({
      nombreMascota: mascota.nombreMascota,
      razaId: Number.isInteger(razaId) ? razaId : '',
      edad: mascota.edad,
      clienteId: Number.isInteger(clienteId) ? clienteId : ''
    });

    this.openModal('E');
  }

  closeModal(): void {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }

    this.resetFormulario();
  }

  openModal(modo: string): void {
    this.titleModal = modo === 'C'
      ? 'Crear Mascota'
      : 'Editar Mascota';

    this.titleBoton = modo === 'C'
      ? 'Guardar Mascota'
      : 'Actualizar Mascota';

    this.modoFormulario = modo;

    const modalElement =
      document.getElementById('modalCrearMascota');

    if (modalElement) {
      this.modalInstance ??= new Modal(modalElement);
      this.modalInstance.show();
    }
  }

  guardarMascota(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();

      Swal.fire(
        'Formulario incompleto',
        'Completa todos los campos antes de continuar.',
        'warning'
      );

      return;
    }

    const formulario = this.form.getRawValue();

    const nombreMascota = String(
      formulario.nombreMascota ?? ''
    ).trim();

    const edad = Number(formulario.edad);
    const razaId = Number(formulario.razaId);
    const clienteId = Number(formulario.clienteId);

    if (
      !nombreMascota ||
      !Number.isInteger(edad) ||
      edad < 0 ||
      !Number.isInteger(razaId) ||
      razaId <= 0 ||
      !Number.isInteger(clienteId) ||
      clienteId <= 0
    ) {
      Swal.fire(
        'Datos inválidos',
        'Verifica el nombre, la edad, la raza y el cliente.',
        'warning'
      );

      return;
    }

    const razaExiste = this.listRazas.some(
      (raza) => Number(raza.id ?? raza.razaId) === razaId
    );

    const clienteExiste = this.listClientes.some(
      (cliente) =>
        Number(cliente.id ?? cliente.clienteId) === clienteId
    );

    if (!razaExiste || !clienteExiste) {
      Swal.fire(
        'Datos no disponibles',
        'Selecciona una raza y un cliente que existan en la base de datos.',
        'warning'
      );

      return;
    }

    const datos: any = {
      nombreMascota,
      edad,
      razaId,
      clienteId
    };

    if (this.modoFormulario === 'E') {
      if (!this.mascotaSelected) {
        Swal.fire(
          'Error',
          'No se encontró la mascota que deseas editar.',
          'error'
        );
        return;
      }

      datos.mascotaId = this.mascotaSelected.mascotaId;
    }

    const peticion = this.modoFormulario === 'E'
      ? this.mascotaService.actualizarMascota(datos)
      : this.mascotaService.guardarMascota(datos);

    peticion.subscribe({
      next: (respuesta) => {
        Swal.fire(
          '¡Operación exitosa!',
          respuesta?.mensaje ??
            (this.modoFormulario === 'E'
              ? 'Mascota actualizada correctamente.'
              : 'Mascota registrada correctamente.'),
          'success'
        );

        this.closeModal();
        this.listar();
      },
      error: (error) => {
        console.error('Error al guardar la mascota:', error);

        const mensaje =
          error?.error?.mensaje ??
          error?.error?.message ??
          'No se pudo guardar. Verifica los datos y el estado del servidor.';

        Swal.fire(
          'No se pudo guardar',
          mensaje,
          'error'
        );
      }
    });
  }
}
