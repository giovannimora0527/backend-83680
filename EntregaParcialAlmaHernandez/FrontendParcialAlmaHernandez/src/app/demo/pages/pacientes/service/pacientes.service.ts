
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

export interface Paciente {
  id: number;
  usuarioId: number;
  tipoDocumento: string;
  numeroDocumento: string;
  nombres: string;
  apellidos: string;
  fechaNacimiento: string;
  genero: string;
  telefono: string;
  direccion: string;
}

@Injectable({
  providedIn: 'root'
})
export class PacientesService {
  private api = 'paciente';

  constructor(private backendService: BackendService) {}

  listarPacientes(): Observable<Paciente[]> {
    return this.backendService.get<Paciente[]>(
      environment.apiUrlAuth,
      this.api,
      'listar'
    );
  }
}
