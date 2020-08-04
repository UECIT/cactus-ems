import { Component, OnInit } from '@angular/core';
import { ManageUsersService } from '../../service/manage-users.service';
import { CdssSupplier } from '../../model/cdssSupplier';
import { NewUser } from '../../model/user';
import { ActivatedRoute, Router } from '@angular/router';
import { CdssService } from '../../service/cdss.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './create-users.component.html',
  styleUrls: ['./create-users.component.css']
})
export class CreateUsersComponent implements OnInit {
  data: any = {};
  user: NewUser;
  title: String = 'Create New User';
  formData: FormGroup = new FormGroup({ password: new FormControl() });
  loaded = false;
  suppliers: CdssSupplier[];
  roles: string[];
  selectedSupplierIds: number[] = [];
  usernames: string[] = [];
  warning: boolean;
  warningMessage: string;
  error: boolean;
  errorMessage: string;
  errorObject: any;

  constructor(
    private manageUsersService: ManageUsersService,
    private cdssService: CdssService,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.roles = this.manageUsersService.getRoles();
    forkJoin([
      this.manageUsersService.getUsers(),
      this.cdssService.getCdssSuppliers()
    ]).subscribe(
      results => {
        results[0].forEach(user => this.usernames.push(user.username));
        this.suppliers = results[1];
      },
      error => {
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.warning = true;
          this.warningMessage =
            'The application is experiencing some issues, creating a new user may not work as expected.';
          this.errorObject = error;
        }
      },
      () => {
        this.setFormData();
        this.loaded = true;
      }
    );
  }

  setFormData() {
    this.formData = new FormGroup({
      name: new FormControl('', [
        Validators.required,
        Validators.maxLength(20)
      ]),
      username: new FormControl('', [
        Validators.required,
        Validators.maxLength(20),
        this.usernameTaken.bind(this)
      ]),
      password: new FormControl('', [Validators.required]),
      confirmPassword: new FormControl('', [
        Validators.compose([
          Validators.required,
          this.validateAreEqual.bind(this)
        ])
      ]),
      role: new FormControl('', [Validators.required]),
      supplierIds: new FormControl([]),
      enabled: new FormControl(true)
    });
  }

  get name() {
    return this.formData.get('name');
  }
  get username() {
    return this.formData.get('username');
  }
  get password() {
    return this.formData.get('password');
  }
  get confirmPassword() {
    return this.formData.get('confirmPassword');
  }
  get cdssSuppliers() {
    return this.formData.get('supplierIds');
  }

  validateAreEqual(fieldControl: FormControl) {
    return fieldControl.value === this.formData.get('password').value
      ? null
      : {
          NotEqual: true
        };
  }

  usernameTaken(fieldControl: FormControl): { [key: string]: boolean } | null {
    return this.usernames.includes(fieldControl.value)
      ? {
          usernameTaken: true
        }
      : null;
  }

  createUser(data) {
    this.user = {
      name: data.name,
      username: data.username,
      password: data.password,
      role: data.role,
      cdssSuppliers: [],
      enabled: data.enabled
    };
    this.suppliers.forEach(supplier => {
      if (data.supplierIds.includes(supplier.id)) {
        this.user.cdssSuppliers.push(supplier);
      }
    });
    this.manageUsersService.createUser(this.user).subscribe(
      user => {
        this.router.navigate(['/users']);
      },
      error => {
        this.error = true;
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.errorMessage = 'Error creating new user.';
          this.errorObject = error;
        }
      }
    );
  }
}
