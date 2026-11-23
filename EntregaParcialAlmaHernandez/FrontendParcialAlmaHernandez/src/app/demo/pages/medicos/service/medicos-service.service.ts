import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

export interface Especializacion {
  id: number;
  nombre?: string;
  descripcion?: string;
  codigoEspecializacion?: string;
}

export interface Medico {
  id: number;
  tipoDocumento: string;
  documento: string;
  nombres: string;
  apellidos: string;
  telefono: string;
  registroProfesional: string;
  especializacion?: Especializacion;
}

@Injectable({
  providedIn: 'root'
})
export class MedicosServiceService {
  private api = 'medico';

  constructor(private backendService: BackendService) {}

  listarMedicos(): Observable<Medico[]> {
    return this.backendService.get<Medico[]>(
      environment.apiUrlAuth,
      this.api,
      'listar'
    );
  }
}