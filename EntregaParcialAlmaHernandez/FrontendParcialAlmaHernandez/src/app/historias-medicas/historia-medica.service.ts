
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AnotacionHistoria {
  id: number;
  historiaId: number;
  medicoId: number;
  fecha: string;
  descripcion: string;
}

export interface HistoriaMedica {
  id: number;
  pacienteId: number;
  fechaCreacion: string;
  anotaciones: AnotacionHistoria[];
}

export interface Medico {
  id: number;
  tipoDocumento: string;
  documento: string;
  nombres: string;
  apellidos: string;
  telefono: string;
  registroProfesional: string;
  especializacion: {
    id?: number;
    nombre?: string;
    descripcion?: string;
    [key: string]: unknown;
  } | null;
}

export interface Respuesta {
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class HistoriaMedicaService {

  private readonly apiUrl =
    'http://localhost:8000/clinica/v1/historia-medica';

  private readonly medicoApiUrl =
    'http://localhost:8000/clinica/v1/medico';

  constructor(private http: HttpClient) {}

  guardarHistoria(pacienteId: number): Observable<Respuesta> {
    return this.http.post<Respuesta>(
      `${this.apiUrl}/guardar`,
      { pacienteId }
    );
  }

  guardarAnotacion(
    historiaId: number,
    medicoId: number,
    descripcion: string
  ): Observable<Respuesta> {
    return this.http.post<Respuesta>(
      `${this.apiUrl}/anotacion/guardar`,
      {
        historiaId,
        medicoId,
        descripcion
      }
    );
  }

  listarHistorias(
    fechaIni: string,
    fechaFin: string
  ): Observable<HistoriaMedica[]> {
    const params = new HttpParams()
      .set('fechaIni', fechaIni)
      .set('fechaFin', fechaFin);

    return this.http.get<HistoriaMedica[]>(
      `${this.apiUrl}/listar`,
      { params }
    );
  }

  listarMedicos(): Observable<Medico[]> {
    return this.http.get<Medico[]>(
      `${this.medicoApiUrl}/listar`
    );
  }

  actualizarAnotacion(
    id: number,
    descripcion: string
  ): Observable<Respuesta> {
    return this.http.put<Respuesta>(
      `${this.apiUrl}/anotacion/actualizar`,
      { descripcion },
      {
        params: new HttpParams().set('id', id.toString())
      }
    );
  }
}
