import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { Cliente } from 'src/app/models/cliente';
import { ClienteService } from './service/cliente.service';

import { Modal } from 'bootstrap';

@Component({
  selector: 'app-cliente',
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './cliente.component.html',
  styleUrl: './cliente.component.scss'
})
export class ClienteComponent {
  listClientes: Cliente[] = [];
  // Variables para el modal.
  modalInstance: Modal | null = null;
  titleModal: string = "";
  modoFormulario: string = "";
  titleBoton: string = "";

  terminoBusqueda = '';
  paginaActual = 1;
  readonly registrosPorPagina = 10;

  constructor(private readonly clienteService: ClienteService) {
    this.getClientes();
  }

  getClientes() {
    this.clienteService.getClientes()
      .subscribe(
        {
          next: (data) => {
            console.log(data);
            this.listClientes = data;
          },
          error: (error) => {
            console.error(error);
          }
        }
      );
  }

  /**
     * Filtra la lista de clientes según el término de búsqueda ingresado por el usuario.
     * @returns Un arreglo de clientes filtrados.
     */
  get clientesFiltrados(): Cliente[] {
    const termino = this.normalizarTexto(this.terminoBusqueda.trim());
    if (!termino) {
      return this.listClientes;
    }

    return this.listClientes.filter((cliente) => {
      const valores = [
        cliente.nombres,
        cliente.apellidos,
        cliente.numeroDocumento,
        cliente.telefono,
        cliente.direccion
      ];

      return valores.some((valor) =>
        this.normalizarTexto(String(valor ?? '')).includes(termino)
      );
    });
  }

  // Metodo que actualiza el termino de busqueda y reinicia la pagina actual a 1.
  actualizarBusqueda(event: Event) {
    this.terminoBusqueda = (event.target as HTMLInputElement).value;
    this.paginaActual = 1;
  }

  // Cambia paginacion.
  cambiarPagina(pagina: number) {
    if (pagina >= 1 && pagina <= this.totalPaginas) {
      this.paginaActual = pagina;
    }
  }

  // Metodo privado para normalizar el texto, eliminando acentos y convirtiendo a minúsculas.
  private normalizarTexto(texto: string): string {
    return texto
      .toLocaleLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }


  get clientesPaginados(): Cliente[] {
    const inicio = (this.paginaActual - 1) * this.registrosPorPagina;
    return this.clientesFiltrados.slice(inicio, inicio + this.registrosPorPagina);
  }

  get totalPaginas(): number {
    return Math.ceil(this.clientesFiltrados.length / this.registrosPorPagina);
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, indice) => indice + 1);
  }

  // Evento para abrir el modal de crear o editar cliente, dependiendo del modo recibido.
  nuevoCliente(modo: string) {
    this.titleModal = modo === 'C' ? 'Crear Cliente' : 'Editar Cliente';
    this.modoFormulario = modo;    
    this.openModal(modo);
  }

  abrirEdicion(cliente: Cliente) {      
    this.modoFormulario = 'E';      
    this.openModal(this.modoFormulario);
  }

  closeModal() {
    if (this.modalInstance) {
      this.modalInstance.hide();
    }      
  }

  openModal(modo: string) {
    this.titleModal = modo === 'C' ? 'Crear Cliente' : 'Editar Cliente';
    this.titleBoton = modo === 'C' ? 'Guardar Cliente' : 'Actualizar Cliente';
    this.modoFormulario = modo;
    const modalElement = document.getElementById('modalCrearCliente');
    if (modalElement) {
      // Verificar si ya existe una instancia del modal
      this.modalInstance ??= new Modal(modalElement);
      this.modalInstance.show();
    }
  }
}