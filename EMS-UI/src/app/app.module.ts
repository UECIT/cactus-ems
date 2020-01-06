import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HashLocationStrategy, LocationStrategy } from '@angular/common';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { MainComponent } from './main/main.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { MaterialModule } from './material.module';
import { BannerComponent } from './banner/banner.component';
import { ManageUsersComponent } from './user-management/manage-users/manage-users.component';
import { LoginService } from './service/login.service';
import { ManageUsersService } from './service/manage-users.service';
import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import { PatientService } from './service/patient.service';
import { TriageComponent } from './triage/triage.component';
import { StoreModule } from '@ngrx/store';
import { patientReducer } from './reducers/patient.reducer';
import { tokenReducer } from './reducers/auth-token.reducer';
import { QuestionnaireComponent } from './triage/questionnaire/questionnaire.component';
import { TriageService } from './service/triage.service';
import { AgePipe } from './pipe/age';
import { UpdateUsersComponent } from './user-management/update-users/update-users.component';
import { CreateUsersComponent } from './user-management/create-users/create-users.component';
import { CdssService } from './service/cdss.service';
import { CaseService } from './service/case.service';
import { CaseComponent } from './triage/case/case.component';
import { RolePipe } from './pipe/role';
import { ReportPipe} from './pipe/report';
import { NavigationComponent } from './navigation/navigation.component';
import { ResetPasswordComponent } from './user-management/reset-password/reset-password.component';
import { DeleteUserComponent } from './user-management/delete-user/delete-user.component';
import { ErrorStateMatcher, ShowOnDirtyErrorStateMatcher, MAT_DATE_LOCALE } from '@angular/material';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { ServiceDefinitionComponent } from './service-definition/service-definition.component';
import { ServiceDefinitionService } from './service/service-definition.service';
import { CreateCdssSupplierComponent } from './supplier-managment/create-cdss-supplier/create-cdss-supplier.component';
import { ManageCdssSupplierComponent } from './supplier-managment/manage-cdss-supplier/manage-cdss-supplier.component';
import { UpdateCdssSupplierComponent } from './supplier-managment/update-cdss-supplier/update-cdss-supplier.component';
import { DeleteCdssSupplierComponent } from './supplier-managment/delete-cdss-supplier/delete-cdss-supplier.component';
import { AuditDisplayComponent } from './audit-display/audit-display.component';
import { ErrorDisplayComponent } from './error-display/error-display.component';
import { AuditService } from './service/audit.service';
import { SwitchSupplierDialogComponent } from './switch-supplier-dialog/switch-supplier-dialog.component';
import { SwitchServicePromptDialogComponent } from './switch-service-prompt-dialog/switch-service-prompt-dialog.component';
import { DosService } from './service/dos.service';
import { DosDisplayComponent } from './triage/dos-display/dos-display.component';
import { ManageSettingsComponent } from './manage-settings/manage-settings.component';
import { QuestionsDisplayComponent } from './triage/questions-display/questions-display.component';
import {WebStorageModule} from 'h5webstorage';
import { ViewAuditsComponent } from './view-audits/view-audits.component';
import { HandoverMessageDialogComponent } from './triage/handover-message-dialog/handover-message-dialog.component';
import { ResourceService } from './service/resource.service';
import { ReportService } from './service/report.service';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { HighlightModule } from 'ngx-highlightjs';
import xml from 'highlight.js/lib/languages/xml';
import { ToastrModule } from 'ngx-toastr';
import { TriageSelectionComponent } from './main/triage-selection/triage-selection.component';
import { PatientSelectionComponent } from './main/patient-selection/patient-selection.component';

export function hljsLanguages() {
  return [
    {name: 'xml', func: xml}
  ];
}

@NgModule({
  schemas: [ CUSTOM_ELEMENTS_SCHEMA ],
  declarations: [
    AppComponent,
    LoginComponent,
    MainComponent,
    BannerComponent,
    ManageUsersComponent,
    UpdateUsersComponent,
    CreateUsersComponent,
    DeleteUserComponent,
    CaseComponent,
    TriageComponent,
    QuestionnaireComponent,
    AgePipe,
    RolePipe,
    ReportPipe,
    NavigationComponent,
    ResetPasswordComponent,
    ServiceDefinitionComponent,
    CreateCdssSupplierComponent,
    ManageCdssSupplierComponent,
    UpdateCdssSupplierComponent,
    DeleteCdssSupplierComponent,
    AuditDisplayComponent,
    ErrorDisplayComponent,
    SwitchSupplierDialogComponent,
    SwitchServicePromptDialogComponent,
    DosDisplayComponent,
    ManageSettingsComponent,
    QuestionsDisplayComponent,
    ViewAuditsComponent,
    HandoverMessageDialogComponent,
    TriageSelectionComponent,
    PatientSelectionComponent
  ],
  entryComponents: [SwitchSupplierDialogComponent, SwitchServicePromptDialogComponent, HandoverMessageDialogComponent],
  imports: [
    BrowserModule,
    StoreModule.forRoot({
      patient: patientReducer,
      authToken: tokenReducer
    }),
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ToastrModule.forRoot(),
    MaterialModule,
    NgxJsonViewerModule,
    WebStorageModule.forRoot(),
    HighlightModule.forRoot({
      languages: hljsLanguages
    })
  ],
  exports: [AgePipe, RolePipe],
  providers: [
    LoginService,
    ManageUsersService,
    PatientService,
    TriageService,
    CdssService,
    CaseService,
    DosService,
    {provide: LocationStrategy, useClass: HashLocationStrategy},
    {provide: ErrorStateMatcher, useClass: ShowOnDirtyErrorStateMatcher},
    ServiceDefinitionService,
    AuditService,
    ResourceService,
    ReportService,
    {provide: MAT_DATE_LOCALE, useValue: 'en-GB'}],
  bootstrap: [AppComponent]
})
export class AppModule { }
platformBrowserDynamic().bootstrapModule(AppModule);

