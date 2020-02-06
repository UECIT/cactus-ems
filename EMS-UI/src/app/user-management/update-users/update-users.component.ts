import { Component, OnInit, OnDestroy } from '@angular/core';
import { ManageUsersService } from '../../service/manage-users.service';
import { CdssSupplier } from '../../model/cdssSupplier';
import { User } from '../../model/user';
import { ActivatedRoute, Router } from '@angular/router';
import { CdssService } from '../../service/cdss.service';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { LoginService } from '../../service/login.service';

@Component({
  selector: 'app-manage-users',
  templateUrl: './update-users.component.html',
  styleUrls: ['./update-users.component.css']
})
export class UpdateUsersComponent implements OnInit, OnDestroy {
  user: User;
  title: String = 'Update User';
  sub: any;
  formData: FormGroup;
  username: string;
  loaded = false;
  suppliers: CdssSupplier[];
  roles: string[];
  selectedSupplierIds: number[] = [];
  warning: boolean;
  warningMessage: string;
  error: boolean;
  errorMessage: string;
  updateError: boolean;
  updateErrorMessage: string;
  errorObject: any;

  constructor(
    private manageUsersService: ManageUsersService,
    private cdssService: CdssService,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.sub = this.route.queryParams.subscribe(params => {
      this.username = params['username'];
      if (this.username) {
        this.getUser(this.username);
      }
    });
    this.roles = this.manageUsersService.getRoles();
    this.cdssService.getCdssSuppliers().subscribe(
      cdssSuppliers => (this.suppliers = cdssSuppliers),
      error => {
        this.warning = true;
        this.warningMessage =
          'The application is experiencing some issues, updating the user may not work as expected';
        this.errorObject = error;
      }
    );
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  getUser(username: string) {
    this.manageUsersService.getUser(username).subscribe(
      user => {
        user.cdssSuppliers.forEach(supplier => {
          this.selectedSupplierIds.push(supplier.id);
        });
        this.formData = new FormGroup({
          name: new FormControl(user.name, [
            Validators.required,
            Validators.minLength(4),
            Validators.maxLength(20)
          ]),
          username: new FormControl({ value: user.username, disabled: true }),
          role: new FormControl({
            value: user.role,
            disabled: user.role === 'ROLE_ADMIN'
          }),
          supplierIds: new FormControl(this.selectedSupplierIds),
          enabled: new FormControl(user.enabled)
        });
        this.user = user;
      },
      error => {
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.error = true;
          this.errorMessage =
            'The application is experiencing issues and is unable to update the user at this time.';
          this.errorObject = error;
        }
      },
      () => (this.loaded = true)
    );
  }

  get name() {
    return this.formData.get('name');
  }
  get cdssSuppliers() {
    return this.formData.get('supplierIds');
  }

  updateUser(data) {
    this.user.name = data.name;
    this.user.role = data.role;
    this.user.enabled = data.enabled;
    this.user.cdssSuppliers = [];
    this.suppliers.forEach(supplier => {
      if (data.supplierIds.includes(supplier.id)) {
        this.user.cdssSuppliers.push(supplier);
      }
    });
    this.manageUsersService.updateUser(this.user).subscribe(
      user => {
        this.router.navigate(['/users']);
      },
      error => {
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.updateError = true;
          this.updateErrorMessage = 'Error updating user.';
          this.errorObject = error;
        }
      }
    );
  }
}
