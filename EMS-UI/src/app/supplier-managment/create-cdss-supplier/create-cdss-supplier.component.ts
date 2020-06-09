import { ResourceReferenceType, CdssSupplier, ServiceDefinition } from './../../model/cdssSupplier';
import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { CdssService } from 'src/app/service/cdss.service';
import { Router } from '@angular/router';
import { LoginService } from 'src/app/service/login.service';

@Component({
  selector: 'app-create-cdss-supplier',
  templateUrl: './create-cdss-supplier.component.html',
  styleUrls: ['./create-cdss-supplier.component.css']
})
export class CreateCdssSupplierComponent implements OnInit {
  data: any = {};
  supplier: CdssSupplier;
  title: String = 'Create New Supplier';
  formData: FormGroup = new FormGroup({ password: new FormControl() });
  loaded = false;
  serviceDefinitions: ServiceDefinition[] = [];
  resourceReferenceType = ResourceReferenceType;
  inputDataRefType: ResourceReferenceType = ResourceReferenceType.ByReference;
  inputParamRefType: ResourceReferenceType = ResourceReferenceType.ByReference;
  warning: boolean;
  warningMessage: string;
  error: boolean;
  errorMessage: string;
  errorObject: any;

  supportedVersions: string[] = ['1.1', '2.0'];

  constructor(
    private cdssService: CdssService,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.setFormData();
    this.loaded = true;
  }

  addSeviceDefinition(data: any) {
    const x = new ServiceDefinition();
    x.serviceDefinitionId = data.serviceDefinitionId;
    x.description = data.serviceDescription;
    this.formData.get('serviceDefinitionId').reset();
    this.formData.get('serviceDescription').reset();
    this.serviceDefinitions.push(x);
  }

  removeServiceDefinition(serviceDefinition: ServiceDefinition) {
    this.serviceDefinitions = this.arrayRemove(
      this.serviceDefinitions,
      serviceDefinition
    );
  }

  arrayRemove(arr, value) {
    return arr.filter(function(ele) {
      return ele !== value;
    });
  }

  setFormData() {
    this.formData = new FormGroup({
      name: new FormControl('', [Validators.required]),
      baseUrl: new FormControl('', [Validators.required]),
      serviceDefinitionId: new FormControl('', []),
      serviceDescription: new FormControl('', []),
      // Support version 1.1 by default
      supportedVersion: new FormControl(this.supportedVersions[0], [Validators.required]),
      authToken: new FormControl('', []),
    });
  }

  get name() {
    return this.formData.get('name');
  }
  get baseUrl() {
    return this.formData.get('baseUrl');
  }
  get serviceDefinitionId() {
    return this.formData.get('serviceDefinitionId');
  }
  get serviceDescription() {
    return this.formData.get('serviceDescription');
  }

  get supportedVersion() {
    return this.formData.get('supportedVersion');
  }

  createSupplier(data) {
    this.supplier = {
      id: data.id,
      name: data.name,
      baseUrl: data.baseUrl,
      serviceDefinitions: [],
      inputDataRefType: this.inputDataRefType,
      inputParamsRefType: this.inputParamRefType,
      supportedVersion: data.supportedVersion,
      authToken: data.authToken
    };
    this.serviceDefinitions.forEach(serviceDefinition => {
      this.supplier.serviceDefinitions.push(serviceDefinition);
    });
    this.cdssService.createCdssSupplier(this.supplier).subscribe(
      () => this.router.navigate(['/suppliers']),
      error => {
        this.error = true;
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.errorMessage = 'Error creating new supplier.';
          this.errorObject = error;
        }
      }
    );
  }
}
