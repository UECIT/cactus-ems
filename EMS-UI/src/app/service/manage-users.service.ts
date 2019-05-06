import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, NewUser } from '../model/user';
import { environment } from '../../environments/environment';
import { ChangePassword } from '../model/changePassword';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class ManageUsersService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getRoles(): string[] {
    return ['ROLE_ADMIN', 'ROLE_NHS', 'ROLE_CDSS'];
  }

  getUsers(): Observable<User[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/users`;
      return this.http.get<User[]>(url, httpOptions);
    }
  }

  getUser(username: String): Observable<User> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/users/` + username;
      return this.http.get<User>(url, httpOptions);
    }
  }

  updateUser(user: User): Observable<User> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/users`;
      return this.http.put<User>(url, JSON.stringify(user), httpOptions);
    }
  }

  createUser(user: NewUser): Observable<User> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/users`;
      return this.http.post<User>(url, JSON.stringify(user), httpOptions);
    }
  }

  updateUsersPassword(newPasswordModel: ChangePassword) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/users/update`;
      return this.http.put(url, newPasswordModel, httpOptions);
    }
  }

  resetPassword(newPasswordModel: ChangePassword) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/users/reset`;
      return this.http.put(url, newPasswordModel, httpOptions);
    }
  }

  deleteUser(username: string) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/users/` + username;
      return this.http.delete(url, httpOptions);
    }
  }

  getUsernames(): Observable<String[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/users`;
      return this.http.get<string[]>(url, httpOptions);
    }
  }
}
