import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Case } from '../model/case';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class CaseService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getCase(caseId: number): Observable<Case> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/case/${caseId}`;
      return this.http.get<Case>(url, httpOptions);
    }
  }
}
