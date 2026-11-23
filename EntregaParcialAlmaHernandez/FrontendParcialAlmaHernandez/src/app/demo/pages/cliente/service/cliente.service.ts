
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';
import { Cliente } from 'src/app/models/cliente';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

interface PacienteApi {
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
export class ClienteService {
  private readonly api = 'paciente';

  constructor(private readonly backendService: BackendService) {}

  getClientes(): Observable<Cliente[]> {
    return this.backendService
      .get<PacienteApi[]>(
        environment.apiUrlAuth,
        this.api,
        'listar'
      )
      .pipe(
        map((pacientes) =>
          pacientes.map((paciente) => ({
            clienteId: paciente.id,
            usuarioId: paciente.usuarioId,
            tipoDocumento: paciente.tipoDocumento,
            numeroDocumento: paciente.numeroDocumento,
            nombres: paciente.nombres,
            apellidos: paciente.apellidos,
            fechaNacimiento: paciente.fechaNacimiento,
            genero: paciente.genero,
            telefono: paciente.telefono,
            direccion: paciente.direccion,
            activo: true
          }))
        )
      );
  }
}
