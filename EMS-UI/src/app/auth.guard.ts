import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { LoginService } from './service/login.service';
import { SessionStorage } from 'h5webstorage';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private loginService: LoginService, private sessionStorage: SessionStorage) { }

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
      if (this.sessionStorage['auth_token']) {
        // logged in so return true
        return true;
    }

    // not logged in so redirect to login page with the return url
    this.loginService.logout(next.routeConfig.path, next.queryParams);
    return false;
  }
}
