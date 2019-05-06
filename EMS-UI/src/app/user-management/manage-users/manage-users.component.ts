import { Component, OnInit } from '@angular/core';
import { LoginService } from '../../service/login.service';
import { ManageUsersService } from '../../service/manage-users.service';
import { User } from '../../model/user';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-manage-users',
  templateUrl: './manage-users.component.html',
  styleUrls: ['./manage-users.component.css']
})
export class ManageUsersComponent implements OnInit {
  data: any = {};
  users: User[];
  loaded = false;
  error = false;
  errorMessage: string;
  errorObject: any;

  constructor(
    private manageUsersService: ManageUsersService,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.manageUsersService.getUsers().subscribe(
      users => {
        this.users = users;
        this.loaded = true;
      },
      error => {
        this.error = true;
        this.loaded = true;
        if (error.status === 401) {
          this.loginService.logout(null);
        } else {
          this.errorMessage = 'Error retrieving users';
          this.errorObject = error;
        }
      }
    );
  }
}
