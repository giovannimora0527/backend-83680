
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CitasService {
  private readonly api = `${environment.apiUrlAuth}/cita`;

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

  listar(fechaIni: string, fechaFin: string): Observable<any> {
    const params = new HttpParams()
      .set('fechaIni', fechaIni)
      .set('fechaFin', fechaFin);

    return this.http.get(`${this.api}/listar`, {
      params,
      headers: this.headers()
    });
  }

  guardar(datos: any): Observable<any> {
    return this.http.post(`${this.api}/guardar`, datos, {
      headers: this.headers()
    });
  }

  actualizar(id: number, datos: any): Observable<any> {
    const params = new HttpParams().set('id', id);

    return this.http.put(`${this.api}/actualizar`, datos, {
      params,
      headers: this.headers()
    });
  }
}
