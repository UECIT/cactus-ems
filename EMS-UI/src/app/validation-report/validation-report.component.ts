import { Interaction, ValidationRequest, SupplierInstance } from '../model';
import { AuditService, EmsService, CdssService } from '../service';
import { Component, OnInit } from '@angular/core';
import { SelectionModel } from "@angular/cdk/collections";

@Component({
  selector: 'validation-report',
  templateUrl: './validation-report.component.html',
  styleUrls: ['./validation-report.component.css']
})
export class ValidationReportComponent implements OnInit {

  endpoints: SupplierInstance[] = [];
  loadedEms = false;
  loadedCdss = false;
  interactions: Interaction[] = [];
  loadedAudits = false;

  sentSuccess = false;
  sentError: string;

  endpointSelection = new SelectionModel<SupplierInstance>();
  interactionSelection = new SelectionModel<Interaction>();

  constructor(
    private emsService: EmsService, 
    private cdssService: CdssService,
    private auditService: AuditService
  ) { }

  ngOnInit() {
    this.fetchEndpoints();
    this.fetchAudits();
  }

  get loaded() {
    return this.loadedCdss
        && this.loadedEms
        && this.loadedAudits;
  }

  fetchEndpoints() {
    this.emsService.getAllEmsSuppliers()
      .subscribe(
        suppliers => {
          this.endpoints = this.endpoints.concat(suppliers);
          this.loadedEms = true;
        }
      );
    this.cdssService.getCdssSuppliers()
      .subscribe(
        suppliers => {
          this.endpoints = this.endpoints.concat(suppliers);
          this.loadedCdss = true;
        }
      );
  }

  fetchAudits() {
    this.auditService.getAudits().then(interactions => {
      this.interactions = interactions;
      this.loadedAudits = true;
    })
    //TODO: handle errors properly
    .catch(err => this.loadedAudits = true);
  }

  sendValidationRequest() {
    let endpointSelection = this.endpointSelection.selected[0];
    let interactionSelection = this.interactionSelection.selected[0];

    let request: ValidationRequest = {
      type: interactionSelection.type,
      interactionId: interactionSelection.interactionId,
      instanceBaseUrl: endpointSelection.baseUrl
    };

    this.auditService.sendValidationRequest(request)
      .then(res => {
        this.sentSuccess = true;
        this.sentError = null;
      })
      .catch(err => {
        this.sentError = err.message;
        this.sentSuccess = false;
      })
  }

  get eitherNotSelected() {
    return this.endpointSelection.isEmpty() || this.interactionSelection.isEmpty();
  }

}
