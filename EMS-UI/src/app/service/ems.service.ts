import { EmsSupplier } from './../model/emsSupplier';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class EmsService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getAllEmsSuppliers(): Observable<EmsSupplier[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/ems`;
      return this.http.get<EmsSupplier[]>(url, httpOptions).pipe();
    }
  }

  deleteEms(id: number) {
    if (this.sessionStorage['auth_token'] != null) {
        httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
        );
        httpOptions.headers = httpOptions.headers.set(
            'Content-Type',
            'application/json'
        );
        const url = `${environment.EMS_API}/ems/${id}`;
        return this.http.delete(url, httpOptions).toPromise();
    }
  }

  createOrUpdateEms(supplier: EmsSupplier) {
    if (this.sessionStorage['auth_token'] != null) {
        httpOptions.headers = httpOptions.headers.set(
            'Authorization',
            this.sessionStorage['auth_token']
        );
        httpOptions.headers = httpOptions.headers.set(
            'Content-Type',
            'application/json'
        );

        const url = `${environment.EMS_API}/ems/`;
        return this.http.post<EmsSupplier>(url, JSON.stringify(supplier), httpOptions).toPromise();
      }
  }
}
