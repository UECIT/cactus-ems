import { Interaction, InteractionType } from './../model/audit';
import { AuditService } from './../service/audit.service';
import { Component, OnInit } from '@angular/core';
import { EmsService } from '../service/ems.service';
import { CdssService } from '../service';
import { SupplierInstance } from '../model/supplierInstance';

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
          let encounterInteractions = this.groupByCase(interactions); //One interaction per case
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

  private groupByCase(interactions: Interaction[]) : Interaction[] {
    let grouped = interactions.reduce(function(r, a) {
      r[a.additionalProperties["caseId"]] = r[a.additionalProperties["caseId"]] || [];
      r[a.additionalProperties["caseId"]].push(a);
      return r;
    }, Object.create(null));

    var firstByCase = [];
    let cases = Object.keys(grouped);
    // Only need to display the first interaction
    for (let i = 0; i < cases.length; i++) {
      firstByCase.push(grouped[cases[i]][0]);
    }
    return firstByCase;
  }
}
