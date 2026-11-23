
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Mascota } from 'src/app/models/mascota';
import { BackendService } from 'src/app/services/backend.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MascotaServiceService {
  private api = 'mascota';

  constructor(
    private readonly backendService: BackendService
  ) {}

  getMascotas(): Observable<Mascota[]> {
    return this.backendService.get(
      environment.apiUrlAuth,
      this.api,
      'listar'
    );
  }

  getRazas(): Observable<any[]> {
    return this.backendService.get(
      environment.apiUrlAuth,
      this.api,
      'razas'
    );
  }

  getClientes(): Observable<any[]> {
    return this.backendService.get(
      environment.apiUrlAuth,
      this.api,
      'clientes'
    );
  }

  guardarMascota(mascota: any): Observable<any> {
    return this.backendService.post(
      environment.apiUrlAuth,
      this.api,
      'guardar',
      mascota
    );
  }

  actualizarMascota(mascota: any): Observable<any> {
    return this.backendService.put(
      environment.apiUrlAuth,
      this.api,
      'actualizar',
      mascota
    );
  }
}
