import { Interaction } from './../model/audit';
import { EmsSupplier } from './../model/emsSupplier';
import { CdssSupplier } from 'src/app/model/cdssSupplier';
import { of } from 'rxjs';
import { AuditService } from './../service/audit.service';
import { EmsService } from './../service/ems.service';
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ValidationReportComponent } from './validation-report.component';
import { Predicate, DebugElement, Component, Input } from '@angular/core';
import { CdssService } from '../service';
import { MaterialModule } from '../material.module';
import { By } from '@angular/platform-browser';

@Component({selector: 'app-error-display', template: ''})
class ErrorDisplayStub {
    @Input('errorObject')
    public errorObject: any;
}

let comp: ValidationReportComponent;
let fixture: ComponentFixture<ValidationReportComponent>;
let page: ValidationReportComponentPage;

class ValidationReportComponentPage {

  get endpoints() {
    const rows = this.queryAll(By.css('.endpoint'));
    return rows.map(row => {
      const name = row.query(By.css('.endpointName')).nativeElement.innerText;
      const baseUrl = row.query(By.css('.endpointBaseUrl')).nativeElement.innerText;

      return {name, baseUrl};
    });
  }

  get interactions() {
    const rows = this.queryAll(By.css('.interaction'));
    return rows.map(row => {
      const origin = row.query(By.css('.interactionRequestOrigin')).nativeElement.innerText;
      const createdDate = row.query(By.css('.interactionCreatedDate')).nativeElement.innerText;
      const caseId: number = +row.query(By.css('.interactionCaseId')).nativeElement.innerText;

      return {origin, createdDate, caseId};
    });
  }

  private queryAll(by: Predicate<DebugElement>): DebugElement[] {
    return fixture.debugElement.queryAll(by);
  }
}

fdescribe('ValidationReportComponent', () => {

  let emsServiceSpy: { getAllEmsSuppliers: jasmine.Spy };
  let cdssServiceSpy: { getCdssSuppliers: jasmine.Spy };
  let auditServiceSpy: { 
    getEncounterAudits: jasmine.Spy, 
    getServiceDefinitionSearchAudits: jasmine.Spy
  }

  beforeEach(() => {
    emsServiceSpy = jasmine.createSpyObj('EmsService', ['getAllEmsSuppliers']);
    cdssServiceSpy = jasmine.createSpyObj('CdssService', ['getCdssSuppliers']);
    auditServiceSpy = jasmine.createSpyObj('AuditService', 
      ['getEncounterAudits', 'getServiceDefinitionSearchAudits']);

    TestBed.configureTestingModule({
        imports: [MaterialModule],
        declarations: [ValidationReportComponent, ErrorDisplayStub],
        providers: [
            {provide: CdssService, useValue: cdssServiceSpy},
            {provide: EmsService, useValue: emsServiceSpy},
            {provide: AuditService, useValue: auditServiceSpy}
        ]
    });
    fixture = TestBed.createComponent(ValidationReportComponent);
    comp = fixture.componentInstance;
    page = new ValidationReportComponentPage();
  });

  it('should create', () => {
    expect(comp).toBeTruthy();
  });

  it('should display end points', () => {
    let cdss = new CdssSupplier();
    cdss.name = "A cdss name";
    cdss.baseUrl = "http://cdss.base.url/fhir";

    let ems = new EmsSupplier();
    ems.name = "An ems name";
    ems.baseUrl = "http://ems.base.url/fhir";

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([cdss]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([ems]));
    auditServiceSpy.getEncounterAudits.and.returnValue(of([]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(of([]));

    fixture.detectChanges(); // init

    expect(comp.loaded).toBeTruthy();
    expect(page.endpoints).toContain(
      {name: cdss.name, baseUrl: cdss.baseUrl}, 
      {name: ems.name, baseUrl: ems.baseUrl}
    );
  });

  it('should display interactions', () => {
    let encounter = new Interaction();
    encounter.caseId = 4;
    encounter.createdDate = "23/06/2020 12:12:12";
    encounter.requestOrigin = "https://some-encounter-location/fhir";

    let sdSearch = new Interaction();
    sdSearch.createdDate = "23/06/2020 12:12:10";
    sdSearch.requestOrigin = "https://some-service-location/fhir";

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
    auditServiceSpy.getEncounterAudits.and.returnValue(of([encounter]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(of([sdSearch]));

    fixture.detectChanges(); // init

    expect(comp.loaded).toBeTruthy();
    expect(page.interactions).toContain(
      {origin: encounter.requestOrigin, createdDate: encounter.createdDate, caseId: encounter.caseId},
      {origin: sdSearch.requestOrigin, createdDate: sdSearch.createdDate, caseId: ''}
    );
  });

  it('should not display when loading fails', () => {
    cdssServiceSpy.getCdssSuppliers.and.returnValue(of());
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of());
    auditServiceSpy.getEncounterAudits.and.returnValue(of());
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(of());

    fixture.detectChanges(); // init

    expect(comp.loaded).toBeFalsy();
  });
});
