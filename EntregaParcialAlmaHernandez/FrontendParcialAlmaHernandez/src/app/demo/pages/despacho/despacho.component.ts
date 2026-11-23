import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-despacho',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './despacho.component.html'
})
export class DespachoComponent implements OnInit {
  formulas: any[] = [];
  seleccionada: any = null;
  loteId: number | null = null;
  cantidad: number | null = null;
  mensaje = '';
  error = '';

  constructor(private backend: BackendService) {}

  ngOnInit(): void {
    this.cargarPendientes();
  }

  cargarPendientes(): void {
    this.backend.get<any[]>(
      environment.apiUrlAuth,
      'despacho',
      'pendientes'
    ).subscribe({
      next: datos => this.formulas = datos ?? [],
      error: err => this.error = err?.error?.message ?? 'No fue posible cargar las fórmulas.'
    });
  }

  seleccionar(formula: any): void {
    this.seleccionada = formula;
    this.loteId = null;
    this.cantidad = null;
    this.error = '';
    this.mensaje = '';
  }

  confirmar(): void {
    if (!this.seleccionada || !this.loteId || !this.cantidad || this.cantidad <= 0) {
      this.error = 'Seleccione el lote y una cantidad válida.';
      return;
    }
    this.backend.post<any>(
      environment.apiUrlAuth,
      'despacho',
      'confirmar',
      {
        formulaId: this.seleccionada.id,
        items: [{
          medicamentoId: this.seleccionada.medicamentoId,
          loteId: this.loteId,
          cantidad: this.cantidad
        }]
      }
    ).subscribe({
      next: respuesta => {
        this.mensaje = respuesta?.mensaje ?? 'Despacho confirmado.';
        this.error = '';
        this.seleccionada = null;
        this.cargarPendientes();
      },
      error: err => {
        this.error = err?.error?.message ?? 'No fue posible confirmar el despacho.';
      }
    });
  }
}
