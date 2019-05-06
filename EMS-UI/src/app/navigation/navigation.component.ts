import { Component, OnInit } from '@angular/core';
import { LoginService } from '../service/login.service';
import { Router } from '@angular/router';
import { Token } from '../model/token';
import { SessionStorage } from 'h5webstorage';

@Component({
  selector: 'app-navigation',
  templateUrl: './navigation.component.html',
  styleUrls: ['./navigation.component.css']
})
export class NavigationComponent implements OnInit {
  isLoggedIn: boolean;
  isAdmin: boolean;
  username: string;
  tokenInfo: Token;

  constructor(private loginService: LoginService, public router: Router, private sessionStorage: SessionStorage) {}

  ngOnInit() {
    this.checkLoginStatus();
    this.loginService.watchAuthToken().subscribe((loggedIn: boolean) => {
      this.checkLoginStatus();
    });
  }

  logoff() {
    if (this.sessionStorage['auth_token'] != null) {
      this.loginService.logout(null);
    }
  }

  checkLoginStatus() {
    this.isAdmin = this.loginService.isAdmin;
    this.isLoggedIn = this.loginService.isLoggedIn;
    this.username = this.loginService.username;
  }
}
