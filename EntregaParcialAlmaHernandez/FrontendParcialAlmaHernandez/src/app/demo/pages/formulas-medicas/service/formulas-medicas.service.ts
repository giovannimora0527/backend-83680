
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Medicamento } from '../../medicamentos/service/medicamentos.service';

export interface FormulaMedica {
  id: number;
  citaId: number;
  medicamentoId: number;
  dosis: string;
  indicaciones: string;
  fechaCreacionRegistro: string;
}

export interface FormulaMedicaRq {
  citaId: number;
  medicamentoId: number;
  dosis: string;
  indicaciones: string;
}

export interface CitaFormula {
  id: number;
  clienteId: number;
  mascotaId: number;
  medicoId: number;
  fechaHora: string;
  estado: string;
  motivo: string;
}

@Injectable({
  providedIn: 'root'
})
export class FormulasMedicasService {
  private readonly api = `${environment.apiUrlAuth}/receta`;
  private readonly medicamentoApi = `${environment.apiUrlAuth}/medicamento`;
  private readonly citaApi = `${environment.apiUrlAuth}/cita`;

  constructor(private readonly http: HttpClient) {}

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

  listar(): Observable<FormulaMedica[]> {
    return this.http.get<FormulaMedica[]>(
      `${this.api}/listar`,
      { headers: this.headers() }
    );
  }

  listarMedicamentos(): Observable<Medicamento[]> {
    return this.http.get<Medicamento[]>(
      `${this.medicamentoApi}/listar`,
      { headers: this.headers() }
    );
  }

  listarCitas(
    fechaIni: string,
    fechaFin: string
  ): Observable<CitaFormula[]> {
    return this.http.get<CitaFormula[]>(
      `${this.citaApi}/listar`,
      {
        params: {
          fechaIni: `${fechaIni}T00:00:00`,
          fechaFin: `${fechaFin}T23:59:59`
        },
        headers: this.headers()
      }
    );
  }

  guardar(datos: FormulaMedicaRq): Observable<unknown> {
    return this.http.post(
      `${this.api}/guardar`,
      datos,
      { headers: this.headers() }
    );
  }
}
