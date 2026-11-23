
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Paciente, PacientesService } from './service/pacientes.service';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pacientes.component.html',
  styleUrl: './pacientes.component.scss'
})
export class PacientesComponent implements OnInit {
  pacientes: Paciente[] = [];
  cargando = true;
  error = '';

  constructor(private pacientesService: PacientesService) {}

  ngOnInit(): void {
    this.pacientesService.listarPacientes().subscribe({
      next: (datos: Paciente[]) => {
        this.pacientes = datos;
        this.cargando = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al consultar pacientes:', error);
        this.error = 'No fue posible cargar los pacientes.';
        this.cargando = false;
      }
    });
  }
}
