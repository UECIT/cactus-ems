import { SupplierInstance } from './../model/emsSupplier';
import { Interaction } from './../model/audit';
import { AuditService } from './../service/audit.service';
import { CdssSupplier } from './../model/cdssSupplier';
import { Component, OnInit } from '@angular/core';
import { EmsService } from '../service/ems.service';
import { EmsSupplier } from '../model';
import { CdssService } from '../service';

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

  error: any;

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
    return this.error || 
      (this.loadedCdss 
        && this.loadedEms 
        && this.loadedEncounterAudits 
        && this.loadedSearchAudits);
  }

  fetchEndpoints() {
    this.emsService.getAllEmsSuppliers()
      .subscribe(
        suppliers => {
          this.endpoints = this.endpoints.concat(suppliers);
          this.loadedEms = true;
        },
        error => this.error = error
      );
    this.cdssService.getCdssSuppliers()
      .subscribe(
        suppliers => {
          this.endpoints = this.endpoints.concat(suppliers);
          this.loadedCdss = true;
        },
        error => this.error = error
      );
  }

  fetchAudits() {
    this.auditService.getEncounterAudits()
      .subscribe(
        interactions => {
          this.interactions = this.interactions.concat(interactions);
          this.loadedEncounterAudits = true;
        },
        error => this.error = error
      );
    this.auditService.getServiceDefinitionSearchAudits()
      .subscribe(
        interactions => {
          this.interactions = this.interactions.concat(interactions);
          this.loadedSearchAudits = true;
        },
        error => this.error = error
      );
  }



}