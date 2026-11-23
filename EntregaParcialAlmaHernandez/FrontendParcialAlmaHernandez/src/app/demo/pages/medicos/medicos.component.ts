
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Medico, MedicosServiceService} from './service/medicos-service.service';

@Component({
  selector: 'app-medicos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './medicos.component.html',
  styleUrl: './medicos.component.scss'
})
export class MedicosComponent implements OnInit {
  medicos: Medico[] = [];
  cargando = true;
  error = '';

  constructor(private medicosService: MedicosServiceService) {}

  ngOnInit(): void {
    this.medicosService.listarMedicos().subscribe({
      next: (datos: Medico[]) => {
        this.medicos = datos;
        this.cargando = false;
      },
      error: (error: HttpErrorResponse) => {
        console.error('Error al consultar médicos:', error);
        this.error = 'No fue posible cargar los médicos.';
        this.cargando = false;
      }
    });
  }
}
