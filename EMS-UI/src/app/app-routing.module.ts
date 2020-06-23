import { ValidationReportComponent } from './validation-report/validation-report.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MainComponent } from './main/main.component';
import { AuthGuard } from './auth.guard';
import { ManageUsersComponent } from './user-management/manage-users/manage-users.component';
import { TriageComponent } from './triage/triage.component';
import { UpdateUsersComponent } from './user-management/update-users/update-users.component';
import { CreateUsersComponent } from './user-management/create-users/create-users.component';
import { ResetPasswordComponent } from './user-management/reset-password/reset-password.component';
import { DeleteUserComponent } from './user-management/delete-user/delete-user.component';
import { CreateCdssSupplierComponent } from './supplier-managment/create-cdss-supplier/create-cdss-supplier.component';
import { ManageCdssSupplierComponent } from './supplier-managment/manage-cdss-supplier/manage-cdss-supplier.component';
import { UpdateCdssSupplierComponent } from './supplier-managment/update-cdss-supplier/update-cdss-supplier.component';
import { DeleteCdssSupplierComponent } from './supplier-managment/delete-cdss-supplier/delete-cdss-supplier.component';
import { EmsSupplierComponent } from './supplier-managment/ems-supplier/ems-supplier.component';
import { ManageSettingsComponent } from './manage-settings/manage-settings.component';
import { ViewAuditsComponent } from './view-audits/view-audits.component';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full'},
  { path: 'login', component: LoginComponent },
  { path: 'main', component: MainComponent, canActivate: [AuthGuard]  },
  { path: 'users', component: ManageUsersComponent, canActivate: [AuthGuard]  },
  { path: 'users/update', component: UpdateUsersComponent, canActivate: [AuthGuard]  },
  { path: 'users/delete', component: DeleteUserComponent, canActivate: [AuthGuard]  },
  { path: 'users/password', component: ResetPasswordComponent, canActivate: [AuthGuard]  },
  { path: 'users/create', component: CreateUsersComponent, canActivate: [AuthGuard]  },
  { path: 'suppliers', component: ManageCdssSupplierComponent, canActivate: [AuthGuard]  },
  { path: 'suppliers/create', component: CreateCdssSupplierComponent, canActivate: [AuthGuard]  },
  { path: 'suppliers/update', component: UpdateCdssSupplierComponent, canActivate: [AuthGuard]  },
  { path: 'suppliers/delete', component: DeleteCdssSupplierComponent, canActivate: [AuthGuard]  },
  { path: 'ems_suppliers', component: EmsSupplierComponent, canActivate: [AuthGuard]},
  { path: 'settings', component: ManageSettingsComponent, canActivate: [AuthGuard]  },
  { path: 'triage', component: TriageComponent, canActivate: [AuthGuard]  },
  { path: 'account/password', component: ResetPasswordComponent, canActivate: [AuthGuard]  },
  { path: 'view-audits', component: ViewAuditsComponent, canActivate: [AuthGuard]  },
  { path: 'create_report', component: ValidationReportComponent, canActivate: [AuthGuard]}
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule { }
