import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Patient } from '../model/patient';
import { PatientService } from '../service/patient.service';
import { Store } from '@ngrx/store';
import { AppState } from '../app.state';
import * as PatientActions from '../actions/patient.actions';
import { CdssService } from '../service/cdss.service';
import { CdssSupplier, ServiceDefinition } from '../model/cdssSupplier';
import { MatSnackBar } from '@angular/material';
import { SessionStorage } from 'h5webstorage';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
  items = [{ text: 'Manage Users' }, { text: 'Settings' }];

  patients: Patient[];
  selectedPatient: Patient;
  cdssSuppliers: CdssSupplier[];
  serviceDefinitions: ServiceDefinition[];
  selectedSupplier: CdssSupplier;
  selectedServiceDefinition: string;
  displayedTestWarningMessage = false;
  selectedQueryType = 'id';

  constructor(
    public router: Router,
    private patientService: PatientService,
    private store: Store<AppState>,
    private cdssSupplierService: CdssService,
    public snackBar: MatSnackBar,
    private sessionStorage: SessionStorage
  ) {}

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
    // this.openSnackBar();
    console.log(this.sessionStorage['displayedTestWarningMessage']);
    if (
      this.sessionStorage['displayedTestWarningMessage'] === 'false' ||
      this.sessionStorage['displayedTestWarningMessage'] == null
    ) {
      setTimeout(() =>
        this.openSnackBar(
          'This Test Harness is for demonstration purposes only and is not representative of any EMS final product.'
        )
      );
      this.sessionStorage.setItem('displayedTestWarningMessage', 'true');
    }

    this.sessionStorage.setItem('triageItems', '[]');
  }

  openSnackBar(message) {
    this.snackBar.open(message, 'I Understand');
  }

  getPatients() {
    this.patientService
      .getAllPatients()
      .subscribe(patients => (this.patients = patients));
  }

  triage() {
    this.sessionStorage.setItem(
      'serviceDefinitionId',
      this.selectedServiceDefinition
    );
    this.sessionStorage.setItem(
      'cdssSupplierId',
      this.selectedSupplier.id.toString()
    );
    this.router.navigate(['/triage']);
  }

  addPatientToStore(patient: Patient) {
    this.selectedPatient = patient;
    this.store.dispatch(new PatientActions.AddPatient(patient));
  }

  getCdssSuppliers() {
    this.cdssSupplierService
      .getCdssSuppliers()
      .subscribe(cdssSuppliers => (this.cdssSuppliers = cdssSuppliers));
  }

  getServiceDefinitionForSupplier(supplier: CdssSupplier) {
    this.serviceDefinitions = supplier.serviceDefinitions;
    this.selectedSupplier = supplier;
    this.addSupplierToStore(supplier);
  }

  addSupplierToStore(supplier: CdssSupplier) {
    this.sessionStorage.setItem('cdssSupplierName', supplier.name);
  }
}
