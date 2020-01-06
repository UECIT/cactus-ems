import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {Patient} from '../model/patient';
import {PatientService} from '../service/patient.service';
import {Store} from '@ngrx/store';
import {AppState} from '../app.state';
import * as PatientActions from '../actions/patient.actions';
import {CdssService} from '../service/cdss.service';
import {CdssSupplier, ServiceDefinition} from '../model/cdssSupplier';
import {MatSnackBar} from '@angular/material';
import {SessionStorage} from 'h5webstorage';
import {SelectService} from '../model/selectService';
import {TriageService} from '../service/triage.service';
import {ToastrService} from 'ngx-toastr';
import {Code} from '../model/case';
import {Settings} from '../model/settings';


@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  items = [{text: 'Manage Users'}, {text: 'Settings'}];

  patients: Patient[];
  selectedPatient: Patient;
  cdssSuppliers: CdssSupplier[];
  serviceDefinitions: ServiceDefinition[];
  selectedSupplier: number;
  selectedServiceDefinition: string;
  displayedTestWarningMessage = false;
  selectedQueryType = 'id';
  serviceDefinitionMode = 'automated';
  availableServiceDefinitions: CdssSupplier[];
  roles: Code[];
  settings: Code[];
  jurisdictions: Code[];
  selectionModeOptions: any[];

  constructor(
      public router: Router,
      private patientService: PatientService,
      private store: Store<AppState>,
      private cdssSupplierService: CdssService,
      private triageService: TriageService,
      public snackBar: MatSnackBar,
      private sessionStorage: SessionStorage,
      private toastr: ToastrService
  ) {
  }

  disableLaunch() {
    return !(
        this.selectedPatient != null &&
        this.selectedSupplier != null &&
        this.selectedServiceDefinition != null
    );
  }

  ngOnInit() {
    this.getPatients();
    this.getCdssSuppliers();
    this.getRoles();
    this.getSettings();
    this.getJurisdictions();
    this.getSelectionModeOptions();

    var settings: Settings = this.sessionStorage['settings'];
    settings.jurisdiction = this.jurisdictions[0];
    settings.setting = this.settings[0];
    settings.userType = this.roles[0];
    this.sessionStorage.setItem('settings', JSON.stringify(settings));

    this.autoSelectServiceDefinition(false);
    this.openSnackBar();

    this.sessionStorage.setItem('triageItems', '[]');
  }

  openSnackBar() {
    var hasDisplayed = this.sessionStorage['displayedTestWarningMessage']
    if (hasDisplayed === 'false' || hasDisplayed == null) {
      setTimeout(() => 
        this.snackBar.open('This Test Harness is for demonstration purposes only and is not representative of any EMS final product.', 'I Understand'));
    }
    this.sessionStorage.setItem('displayedTestWarningMessage', 'true');
  }

  triage() {
    this.sessionStorage.setItem(
        'serviceDefinitionId',
        this.selectedServiceDefinition.toString()
    );
    this.sessionStorage.setItem(
        'cdssSupplierId',
        this.selectedSupplier.toString()
    );

    this.router.navigate(['/triage']);
  }

  async getPatients() {
    this.patients = await this.patientService.getAllPatients().toPromise();
    this.selectedPatient = this.patients[0];
    this.store.dispatch(new PatientActions.AddPatient(this.selectedPatient));
  }

  async getCdssSuppliers() {
    this.cdssSuppliers =
        await this.cdssSupplierService.getCdssSuppliers().toPromise();
  }

  getRoles() {
    this.roles = [
      {
        'id': 1,
        'description': 'Clinical',
        'code': '103GC0700X',
        'display': 'Clinical'
      },
      {
        'id': 2,
        'description': 'Urgent',
        'code': '261QU0200X',
        'display': 'Call Handler'
      },
      {
        'id': 3,
        'description': 'Patient',
        'code': 'PA',
        'display': 'Patient'
      }
    ]
  }

  getSettings() {
    this.settings = [
      {
        'id': 1,
        'description': 'Online',
        'code': 'online',
        'display': 'Online'
      },
      {
        'id': 2,
        'description': 'Phone call',
        'code': 'phone',
        'display': 'Phone call'
      }
    ]
  }

  getJurisdictions() {
    this.jurisdictions = [
      {
        'id': 1,
        'description': 'United Kingdom of Great Britain and Northern Ireland (the)',
        'code': 'GB',
        'display': 'United Kingdom of Great Britain and Northern Ireland (the)'
      },
      {
        'id': 2,
        'description': 'Tokelau',
        'code': 'TK',
        'display': 'Tokelau'
      }
    ]
  }

  getSelectionModeOptions() {
    this.selectionModeOptions = [
      {
        'id':'automated',
        'display': 'Automated'
      },
      {
        'id':'manual',
        'display': 'Manual'
      }
    ]
  }

  async setSelectedSupplier(supplier: CdssSupplier) {
    this.selectedSupplier = supplier.id;
    this.addSupplierToStore(supplier);

    // Request list of SDs from CDSS
    this.serviceDefinitions = await this.cdssSupplierService.listServiceDefinitions(supplier.id);
  }

  addSupplierToStore(supplier: CdssSupplier) {
    this.sessionStorage.setItem('cdssSupplierName', supplier.name);
  }

  addRoleToStore(role: Code) {
    var settings: Settings = this.sessionStorage['settings'];
    settings.userType = role;
    this.sessionStorage.setItem('settings', JSON.stringify(settings));
    this.autoSelectServiceDefinition(false);
  }

  addSettingToStore(setting: Code) {
    var settings: Settings = this.sessionStorage['settings'];
    settings.setting = setting;
    this.sessionStorage.setItem('settings', JSON.stringify(settings));
    this.autoSelectServiceDefinition(false);
  }

  addJurisdictionToStore(jurisdiction: Code) {
    var settings: Settings = this.sessionStorage['settings'];
    settings.jurisdiction = jurisdiction;
    this.sessionStorage.setItem('settings', JSON.stringify(settings));
    this.autoSelectServiceDefinition(false);
  }

  addPatientToStore(patient: Patient) {
    this.selectedPatient = patient;
    this.store.dispatch(new PatientActions.AddPatient(patient));
    this.sessionStorage.setItem('patient', JSON.stringify(patient));
    this.autoSelectServiceDefinition(false);
  }

  changeSelectionMode(mode: any) {
    this.serviceDefinitionMode = mode.id;
    if (this.serviceDefinitionMode === 'automated') {
      this.autoSelectServiceDefinition(false);
    } else if (this.cdssSuppliers.length > 0) {
      this.setSelectedSupplier(this.cdssSuppliers[0])
    }
  }

  async autoSelectServiceDefinition(force: boolean) {
    if (this.serviceDefinitionMode === 'automated' || force) {
      this.serviceDefinitionMode = 'automated';

      // Request available SDs for the current patient (or no known patient)
      const request = new SelectService();
      if (this.selectedPatient) {
        request.patientId = this.selectedPatient.id;
      }
      var settings: Settings = this.sessionStorage['settings'];
      request.settings = settings;

      var patient: Patient = this.sessionStorage['patient']
      
      const selectedSDs = await this.triageService.selectServiceDefinitions(request);

      if (selectedSDs.length > 0) {
        this.availableServiceDefinitions = selectedSDs;
      } else {
        this.toastr.info('No available service definitions');
        this.availableServiceDefinitions = [];
      }
    }
  }

  selectServiceDefinition(serviceDefinition: ServiceDefinition) {
    this.selectedServiceDefinition = serviceDefinition.serviceDefinitionId;
  }

  // NCTH-269: Unsure which field should be displayed, for now falling back to ID
  getServiceDefinitionText(serviceDefinition: ServiceDefinition) {
    return serviceDefinition.description || serviceDefinition.serviceDefinitionId;
  }
}
