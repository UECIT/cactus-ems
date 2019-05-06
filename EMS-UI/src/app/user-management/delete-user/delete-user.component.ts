import { Component, OnInit, OnDestroy } from '@angular/core';
import { ManageUsersService } from '../../service/manage-users.service';
import { User } from '../../model/user';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './delete-user.component.html',
  styleUrls: ['./delete-user.component.css']
})
export class DeleteUserComponent implements OnInit, OnDestroy {
  title: String = 'Delete User';
  sub: any;
  username: string;
  loaded: boolean;
  user: User;
  warning: boolean;
  warningMessage: string;
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
    this.sub = this.route.queryParams.subscribe(params => {
      this.username = params['username'];
    });
    this.getUser(this.username);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  getUser(username: string) {
    this.manageUsersService.getUser(username).subscribe(
      user => {
        this.user = user;
        this.loaded = true;
      },
      error => {
        this.warning = true;
        this.warningMessage =
          'The application is experiencing some issues, creating a new user may not work as expected.';
        this.errorObject = error;
        this.user = new User();
        this.user.name = '';
        this.user.username = username;
      }
    );
  }

  deleteUser() {
    this.manageUsersService.deleteUser(this.username).subscribe(
      resp => {
        this.router.navigate(['/users']);
      },
      error => {
        this.error = true;
        if (error.status === 401) {
          this.loginService.logout(null);
        } else {
          this.errorMessage = 'Error deleting user.';
          this.errorObject = error;
        }
      }
    );
  }
}
