
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from './service/usuario.service';
import { Usuario } from 'src/app/models/usuario';

@Component({
  selector: 'app-usuario',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './usuario.component.html',
  styleUrl: './usuario.component.scss'
})
export class UsuarioComponent implements OnInit {
  usuarios: Usuario[] = [];
  usuariosFiltrados: Usuario[] = [];

  busqueda = '';
  rolSeleccionado = '';
  estadoSeleccionado = '';

  cargando = false;
  error = '';

  constructor(private readonly usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.cargando = true;
    this.error = '';

    this.usuarioService.getUsuarios().subscribe({
      next: (respuesta: Usuario[]) => {
        this.usuarios = respuesta ?? [];
        this.filtrarUsuarios();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar usuarios:', err);

        this.error =
          err?.error?.message ||
          err?.error?.mensaje ||
          'No fue posible cargar los usuarios.';

        this.cargando = false;
      }
    });
  }

  filtrarUsuarios(): void {
    const termino = this.busqueda.trim().toLowerCase();

    this.usuariosFiltrados = this.usuarios.filter(usuario => {
      const coincideBusqueda =
        String(usuario.username ?? '').toLowerCase().includes(termino) ||
        String(usuario.email ?? '').toLowerCase().includes(termino) ||
        String(usuario.rol ?? '').toLowerCase().includes(termino) ||
        String(usuario.id ?? '').includes(termino);

      const coincideRol =
        !this.rolSeleccionado ||
        usuario.rol === this.rolSeleccionado;

      const coincideEstado =
        this.estadoSeleccionado === '' ||
        String(usuario.activo) === this.estadoSeleccionado;

      return coincideBusqueda && coincideRol && coincideEstado;
    });
  }

  obtenerRoles(): string[] {
    return [
      ...new Set(
        this.usuarios
          .map(usuario => usuario.rol)
          .filter((rol): rol is string => Boolean(rol))
      )
    ];
  }
}
