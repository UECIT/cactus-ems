import { Component, OnInit } from '@angular/core';
import { ManageUsersService } from '../../service/manage-users.service';
import { User } from '../../model/user';
import { ActivatedRoute, Router } from '@angular/router';
import { ChangePassword } from '../../model/changePassword';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {
  title: string;
  sub: any;
  formData: FormGroup = new FormGroup({ password: new FormControl() });
  username: string;
  loaded: boolean;
  user: User;
  passwordChange: ChangePassword;
  reset: boolean;
  error: boolean;
  errorMessage: string;
  errorObject: any;

  constructor(
    private manageUsersService: ManageUsersService,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    const self = this;
    this.route.url.subscribe(url => {
      if (url[0].path === 'users' && url[1].path === 'password') {
        this.title = 'Reset Password';
        this.reset = true;
      } else if (url[0].path === 'account' && url[1].path === 'password') {
        this.title = 'Change Password';
        this.reset = false;
      }
      self.route.queryParams.subscribe(params => {
        this.username = params['username'];
        self.setFormData(this.username);
        this.loaded = true;
      });
    });
  }

  setFormData(username: string) {
    this.formData = new FormGroup({
      username: new FormControl({ value: username, disabled: true }),
      password: new FormControl('', [Validators.required]),
      confirmPassword: new FormControl('', [
        Validators.compose([
          Validators.required,
          this.validateAreEqual.bind(this)
        ])
      ])
    });
    if (!this.reset) {
      this.formData.addControl(
        'oldPassword',
        new FormControl('', [Validators.required])
      );
    }
  }

  get oldPassword() {
    return this.formData.get('oldPassword');
  }
  get password() {
    return this.formData.get('password');
  }
  get confirmPassword() {
    return this.formData.get('confirmPassword');
  }

  validateAreEqual(fieldControl: FormControl) {
    return fieldControl.value === this.formData.get('password').value
      ? null
      : {
          NotEqual: true
        };
  }

  resetPassword(data) {
    this.passwordChange = {
      username: this.username,
      oldPassword: data.oldPassword,
      newPassword: data.password,
      confirmPassword: ''
    };
    if (this.reset) {
      this.manageUsersService.resetPassword(this.passwordChange).subscribe(
        user => {
          this.router.navigate(['/users']);
        },
        error => {
          if (error.status === 401) {
            this.loginService.logout(null, null);
          } else {
            this.error = true;
            this.errorMessage = 'Error resetting password.';
            this.errorObject = error;
          }
        }
      );
    } else {
      this.manageUsersService
        .updateUsersPassword(this.passwordChange)
        .subscribe(
          user => {
            this.router.navigate(['/main']);
          },
          error => {
            this.error = true;
            if (error.status === 401) {
              this.errorMessage = 'Current password is incorrect.';
              this.errorObject = error;
            } else {
              this.errorMessage = 'Error changing password.';
              this.errorObject = error;
            }
          }
        );
    }
  }
}
