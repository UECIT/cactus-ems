import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Practitioner } from '../model';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class PractitionerService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getAllPractitioners(): Observable<Practitioner[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/practitioner/all`;
      return this.http.get<Practitioner[]>(url, httpOptions).pipe();
    }
  }
}
