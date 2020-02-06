import { Component, OnInit } from '@angular/core';
import { Login } from '../model/login';
import { LoginService } from '../service/login.service';
import { Router, ActivatedRoute } from '@angular/router';
import { SessionStorage } from 'h5webstorage';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  error: boolean;
  errorMessage: string;
  errorObject: any;

  constructor(
    private loginService: LoginService,
    private route: ActivatedRoute,
    public router: Router,
    private sessionStorage: SessionStorage
  ) {}

  ngOnInit() {
    if (this.sessionStorage['auth_token'] != null) {
      this.router.navigate(['/main']);
    }
  }

  login(login: Login): void {
    this.loginService.authenticate(login).subscribe(
      resp => {
        const authorizationResponseHeader = resp.headers.get('authorization');
        if (authorizationResponseHeader != null) {
          this.sessionStorage.setItem('auth_token', authorizationResponseHeader);
          sessionStorage.setItem('auth_token', authorizationResponseHeader);
          this.loginService.authSub.next(true);
          this.router.navigate(['/main'], {
            queryParams: this.route.snapshot.queryParams
          });
        }
      },
      error => {
        this.error = true;
        if (error.status === 401) {
          this.errorMessage = 'Username or password incorrect';
          this.errorObject = error;
        } else {
          this.errorMessage = 'Error logging in';
          this.errorObject = error;
        }
      }
    );
  }
}
