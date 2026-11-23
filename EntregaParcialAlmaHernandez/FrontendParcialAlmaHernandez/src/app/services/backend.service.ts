
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BackendService {
  constructor(private http: HttpClient) {}

  construirHeader(): HttpHeaders {
    const tokenRecuperado = localStorage.getItem('token') || '';
    const authorizationGuardada = localStorage.getItem('Authorization');
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (authorizationGuardada) {
      headers = headers.set('Authorization', authorizationGuardada);
    } else if (tokenRecuperado) {
      headers = headers.set('Authorization', `Bearer ${tokenRecuperado}`);
    } else {
      headers = headers.set(
        'Authorization',
        `Basic ${btoa('gmora:DemoTemporal2026!')}`
      );
    }

    return headers;
  }

  get<T>(
    urlApi: string,
    endpoint: string,
    service: string,
    routerParams?: HttpParams
  ): Observable<T> {
    return this.http.get<T>(`${urlApi}/${endpoint}/${service}`, {
      params: routerParams,
      headers: this.construirHeader()
    });
  }

  post<T>(
    urlApi: string,
    endpoint: string,
    service: string,
    data: unknown
  ): Observable<T> {
    return this.http.post<T>(`${urlApi}/${endpoint}/${service}`, data, {
      headers: this.construirHeader()
    });
  }

  put<T>(
    urlApi: string,
    endpoint: string,
    service: string,
    data: unknown
  ): Observable<T> {
    return this.http.put<T>(`${urlApi}/${endpoint}/${service}`, data, {
      headers: this.construirHeader()
    });
  }

  postFile<T>(
    urlApi: string,
    endpoint: string,
    service: string,
    data: FormData
  ): Observable<T> {
    const tokenRecuperado = localStorage.getItem('token') || '';
    const authorizationGuardada = localStorage.getItem('Authorization');
    let headers = new HttpHeaders();

    if (authorizationGuardada) {
      headers = headers.set('Authorization', authorizationGuardada);
    } else if (tokenRecuperado) {
      headers = headers.set('Authorization', `Bearer ${tokenRecuperado}`);
    } else {
      headers = headers.set(
        'Authorization',
        `Basic ${btoa('gmora:DemoTemporal2026!')}`
      );
    }

    return this.http.post<T>(`${urlApi}/${endpoint}/${service}`, data, {
      headers
    });
  }
}
