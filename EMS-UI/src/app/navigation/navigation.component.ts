import { EnvironmentService } from './../service/environment.service';
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
  colour: string;

  constructor(
    private loginService: LoginService,
    private environmentService: EnvironmentService, 
    public router: Router, 
    private sessionStorage: SessionStorage) {}

  ngOnInit() {
    this.checkLoginStatus();
    this.loginService.watchAuthToken().subscribe((loggedIn: boolean) => {
      this.checkLoginStatus();
      if (loggedIn) {
        this.environmentService.getBackgroundColour()
        .then(res => this.colour = res);
      }
    });
  }

  logoff() {
    if (this.sessionStorage['auth_token'] != null) {
      this.loginService.logout(null, null);
    }
  }

  checkLoginStatus() {
    this.isAdmin = this.loginService.isAdmin;
    this.isLoggedIn = this.loginService.isLoggedIn;
    this.username = this.loginService.username;
  }
}
