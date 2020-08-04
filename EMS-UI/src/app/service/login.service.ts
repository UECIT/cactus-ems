import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Login } from '../model/login';
import { environment } from '../../environments/environment';
import { Subject, Observable } from 'rxjs';
import * as jwt_decode from 'jwt-decode';
import { Router, Params } from '@angular/router';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

const adminRoles = ['ROLE_ADMIN', 'ROLE_SUPPLIER_ADMIN'];

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  // To control if the user is logged in or not
  authSub = new Subject<boolean>();

  constructor(private http: HttpClient, private router: Router, private sessionStorage: SessionStorage) {}

  authenticate(login: Login) {
    const url = `${environment.EMS_API}/login`;
    return this.http.post(url, login, { observe: 'response' });
  }

  logout(returnUrl: string, params: Params) {
    this.sessionStorage.removeItem('auth_token');
    this.authSub.next(true);
    if (returnUrl != null) {
      this.router.navigate(['/login'], {
        queryParams: params
      });
    } else {
      this.router.navigate(['/login']);
    }
  }

  watchAuthToken(): Observable<any> {
    return this.authSub.asObservable();
  }

  get isLoggedIn(): boolean {
    return this.sessionStorage['auth_token'] != null;
  }

  get isAdmin(): boolean {
    const authToken: string = this.sessionStorage['auth_token'];
    if (authToken != null) {
      const tokenInfo: any = jwt_decode(authToken);
      const roles = (tokenInfo.roles || '').split(',');
      return roles
        .filter(role => adminRoles.includes(role))
        .length > 0;
    }
    return false;
  }

  get username(): string {
    const authToken: string = this.sessionStorage['auth_token'];
    if (authToken != null) {
      const tokenInfo: any = jwt_decode(authToken);
      return tokenInfo.sub;
    }
    return null;
  }
}
