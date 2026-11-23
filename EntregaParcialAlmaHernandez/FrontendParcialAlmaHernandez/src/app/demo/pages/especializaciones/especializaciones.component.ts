
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from 'src/environments/environment';

interface Especializacion {
  codigo?: string;
  nombre?: string;
  descripcion?: string;
  [key: string]: unknown;
}

@Component({
  selector: 'app-especializaciones',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './especializaciones.component.html',
  styleUrl: './especializaciones.component.scss'
})
export class EspecializacionesComponent implements OnInit {
  Object = Object;

  especializaciones: Especializacion[] = [];
  especializacionesFiltradas: Especializacion[] = [];

  busqueda = '';
  cargando = false;
  error = '';

  private readonly apiUrl =
    `${environment.apiUrlAuth}/especializacion`;

  constructor(private readonly http: HttpClient) {}

  ngOnInit(): void {
    this.listarEspecializaciones();
  }

  listarEspecializaciones(): void {
    this.cargando = true;
    this.error = '';

    const token = localStorage.getItem('token');

    let headers = new HttpHeaders();

    if (token) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    this.http.get<Especializacion[]>(
      `${this.apiUrl}/listar`,
      { headers }
    ).subscribe({
      next: (respuesta) => {
        this.especializaciones = respuesta ?? [];
        this.filtrarEspecializaciones();
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar especializaciones:', err);

        this.error =
          err?.error?.message ||
          err?.error?.mensaje ||
          'No fue posible cargar las especializaciones.';

        this.cargando = false;
      }
    });
  }

  filtrarEspecializaciones(): void {
    const termino = this.busqueda.trim().toLowerCase();

    this.especializacionesFiltradas =
      this.especializaciones.filter(especializacion =>
        Object.values(especializacion).some(valor =>
          String(valor ?? '').toLowerCase().includes(termino)
        )
      );
  }
}
