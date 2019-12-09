import { CreateUsersComponent } from './../user-management/create-users/create-users.component';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Code } from '../model/case';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getRoles(): Observable<Code[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/role`;
      return this.http.get<Code[]>(url, httpOptions).pipe();
    }
  }
}
