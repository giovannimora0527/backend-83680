
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

export interface Medicamento {
  id: number;
  nombre: string;
  descripcion: string | null;
  presentacion: string | null;
  fechaCompra: string;
  fechaVence: string;
  fechaCreacionRegistro?: string;
  fechaModificacionRegistro?: string | null;
  codigo?: string;
  tipoProducto?: 'MEDICAMENTO' | 'UTENSILIO';
  precioCompra?: number;
  precioVenta?: number;
  cantidadActual?: number;
  stockMinimo?: number;
  fechaIngreso?: string;
  fechaProximoPedido?: string;
  registroInvima?: string;
}

export interface MedicamentoRq {
  id?: number;
  nombre: string;
  descripcion: string;
  presentacion: string;
  fechaCompra: string;
  fechaVence: string;
  codigo: string;
  tipoProducto: 'MEDICAMENTO' | 'UTENSILIO';
  precioCompra?: number;
  precioVenta?: number;
  cantidadActual?: number;
  stockMinimo?: number;
  fechaIngreso?: string;
  fechaProximoPedido?: string;
  registroInvima?: string;
}

@Injectable({
  providedIn: 'root'
})
export class MedicamentosService {
  private readonly apiUrl =
    `${environment.apiUrlAuth}/medicamento`;

  constructor(private readonly http: HttpClient) {}

  listar(): Observable<Medicamento[]> {
    return this.http.get<Medicamento[]>(
      `${this.apiUrl}/listar`
    );
  }

  guardar(medicamento: MedicamentoRq): Observable<unknown> {
    return this.http.post<unknown>(
      `${this.apiUrl}/guardar`,
      medicamento
    );
  }

  actualizar(medicamento: MedicamentoRq): Observable<unknown> {
    return this.http.post<unknown>(
      `${this.apiUrl}/actualizar`,
      medicamento
    );
  }
}
