import { Interaction, InteractionType, ValidationRequest } from '../model';
import { AuditService, EmsService, CdssService } from '../service';
import { Component, OnInit } from '@angular/core';
import { SupplierInstance } from '../model/supplierInstance';
import { firstGroupedBy } from '../utils/functions';
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
  loadedEncounterAudits = false;
  loadedSearchAudits = false;

  sentSuccess = false;
  sentError: string;

  public endpointSelection = new SelectionModel<SupplierInstance>();
  public interactionSelection = new SelectionModel<Interaction>();

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
        && this.loadedEncounterAudits 
        && this.loadedSearchAudits;
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
    this.auditService.getEncounterAudits()
      .then(
        interactions => {
          let encounterInteractions = firstGroupedBy(interactions, int => int.additionalProperties["caseId"]); //One interaction per case
          encounterInteractions
            .forEach(int => int.interactionType = InteractionType.ENCOUNTER);
          this.interactions = this.interactions.concat(encounterInteractions);
          this.loadedEncounterAudits = true;
        }
      )
      //TODO: handle errors properly
      .catch(err => this.loadedEncounterAudits = true);
    this.auditService.getServiceDefinitionSearchAudits()
      .then(
        interactions => {
          interactions
            .forEach(int => int.interactionType = InteractionType.SERVICE_SEARCH);
          this.interactions = this.interactions.concat(interactions);
          this.loadedSearchAudits = true;
        }
        //TODO: handle errors properly
      ).catch(err => this.loadedSearchAudits = true);
  }

  sendValidationRequest() {
    //TODO: Get actual selections
    let fakeEndpointSelection: SupplierInstance = {
      name: "This is a fake",
      baseUrl: "this.is.a.fake"
    };
    let fakeInteractionSelection: Interaction = {
      interactionType: InteractionType.ENCOUNTER,
      createdDate: 955335783,
      additionalProperties: new Map<string, string>([["caseId", "43"]])
    };

    let request: ValidationRequest = {
      endpoint: fakeEndpointSelection.baseUrl,
      createdDate: fakeInteractionSelection.createdDate,
      caseId: fakeInteractionSelection.additionalProperties.get("caseId"),
    };
    this.auditService.sendValidationRequest(request)
      .then(res => this.sentSuccess = true)
      .catch(err => this.sentError = err.message)
  }

}
