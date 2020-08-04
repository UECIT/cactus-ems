import {ResourceReferenceType} from '../../model';
import {Component, OnInit} from '@angular/core';
import {CdssService} from 'src/app/service/cdss.service';
import {ActivatedRoute, Router} from '@angular/router';
import {LoginService} from 'src/app/service/login.service';
import {User} from 'src/app/model/user';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {ServiceDefinition} from 'src/app/model/cdssSupplier';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-update-cdss-supplier',
  templateUrl: './update-cdss-supplier.component.html',
  styleUrls: ['./update-cdss-supplier.component.css']
})
export class UpdateCdssSupplierComponent implements OnInit {
  resourceReferenceType = ResourceReferenceType;
  user: User = null;
  sub: any;
  supplierId: string;
  supplier: any;
  loaded = false;
  title: String = 'Update Supplier';
  formData: FormGroup = new FormGroup({password: new FormControl()});
  warning: boolean;
  warningMessage: string;
  error: boolean;
  errorMessage: string;

  supportedVersions: string[] = ['1.1', '2.0'];

  constructor(
      private cdssService: CdssService,
      private route: ActivatedRoute,
      private router: Router,
      private loginService: LoginService,
      private toastr: ToastrService
  ) {
  }

  ngOnInit() {
    this.sub = this.route.queryParams.subscribe(params => {
      this.supplierId = params['supplier'];
      if (this.supplierId) {
        this.getSupplier(this.supplierId);
      }
    });
  }

  setFormData() {
    this.formData = new FormGroup({
      name: new FormControl(this.supplier.name, [Validators.required]),
      baseUrl: new FormControl(this.supplier.baseUrl, [Validators.required]),
      authToken: new FormControl(this.supplier.authToken, [Validators.required]),
      serviceDefinitionId: new FormControl('', []),
      serviceDescription: new FormControl('', []),
      supportedVersion: new FormControl(this.supplier.supportedVersion, [Validators.required])
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

  async getSupplier(supplierId: string) {
    this.supplier = await this.cdssService.getCdssSupplier(supplierId)
    .catch(err => {
      this.toastr.error(
          err.error.target.__zone_symbol__xhrURL + ' - ' +
          err.message);
    });
    this.setFormData();
    this.loaded = true;
  }

  addSeviceDefinition(data: any) {
    const x = new ServiceDefinition();
    x.serviceDefinitionId = data.serviceDefinitionId;
    x.description = data.serviceDescription;
    this.formData.get('serviceDefinitionId').reset();
    this.formData.get('serviceDescription').reset();
    this.supplier.serviceDefinitions.push(x);
  }

  removeServiceDefinition(serviceDefinition: ServiceDefinition) {
    this.supplier.serviceDefinitions = this.arrayRemove(
        this.supplier.serviceDefinitions,
        serviceDefinition
    );
  }

  arrayRemove(arr, value) {
    return arr.filter(function (ele) {
      return ele !== value;
    });
  }

  updateSupplier(data) {
    this.supplier.name = data.name;
    this.supplier.baseUrl = data.baseUrl;
    this.supplier.supportedVersion = data.supportedVersion;
    this.supplier.authToken = data.authToken;

    this.cdssService.updateCdssSupplier(this.supplier).subscribe(
        supplier => {
          this.router.navigate(['/suppliers']);
        },
        error => {
          this.error = true;
          if (error.status === 401) {
            this.loginService.logout(null, null);
          } else {
            this.errorMessage = 'Error updating supplier.';
          }
        }
    );
  }
}
