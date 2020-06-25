import { Interaction, InteractionType } from './../model/audit';
import { EmsSupplier } from './../model/emsSupplier';
import { CdssSupplier } from 'src/app/model/cdssSupplier';
import { of } from 'rxjs';
import { AuditService } from './../service/audit.service';
import { EmsService } from './../service/ems.service';
import { async, ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';

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
      const origin = row.query(By.css('.interactionType')).nativeElement.innerText;
      const createdDate = row.query(By.css('.interactionCreatedDate')).nativeElement.innerText;
      const caseId: number = +row.query(By.css('.interactionCaseId')).nativeElement.innerText;

      return {origin, createdDate, caseId};
    });
  }

  private queryAll(by: Predicate<DebugElement>): DebugElement[] {
    return fixture.debugElement.queryAll(by);
  }
}

describe('ValidationReportComponent', () => {

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

  it('should display end points', fakeAsync(() => {
    let cdss = new CdssSupplier();
    cdss.name = "A cdss name";
    cdss.baseUrl = "http://cdss.base.url/fhir";

    let ems = new EmsSupplier();
    ems.name = "An ems name";
    ems.baseUrl = "http://ems.base.url/fhir";

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([cdss]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([ems]));
    auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));

    fixture.detectChanges(); // init
    tick();
    fixture.detectChanges(); // resolve async promises

    expect(comp.loaded).toBeTruthy();
    expect(page.endpoints).toContain(
      {name: cdss.name, baseUrl: cdss.baseUrl}, 
      {name: ems.name, baseUrl: ems.baseUrl}
    );
  }));

  it('should display interactions', fakeAsync(() => {
    let encounter = new Interaction();
    encounter.additionalProperties["caseId"] = 4;
    encounter.createdDate = 835222942; //'Jun 19, 1996, 10:22:22 PM'
    encounter.interactionType = InteractionType.ENCOUNTER;

    let sdSearch = new Interaction();
    sdSearch.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM'
    sdSearch.interactionType = InteractionType.SERVICE_SEARCH;

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
    auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([encounter]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([sdSearch]));

    fixture.detectChanges(); // init
    tick();
    fixture.detectChanges(); // resolve async promises

    expect(comp.loaded).toBeTruthy();
    expect(page.interactions).toContain(
      {origin: encounter.interactionType, createdDate: 'Jun 19, 1996, 10:22:22 PM', caseId: encounter.additionalProperties['caseId']},
      {origin: sdSearch.interactionType, createdDate: 'Apr 10, 2000, 3:03:03 AM', caseId: 0}
    );
  }));

  it('should display one interaction per encounter', fakeAsync(() => {
    let encounter = new Interaction();
    encounter.additionalProperties["caseId"] = 4;
    encounter.createdDate = 835222942; //'Jun 19, 1996, 10:22:22 PM'
    encounter.interactionType = InteractionType.ENCOUNTER;

    let encounter2 = new Interaction();
    encounter2.additionalProperties["caseId"] = 4;
    encounter2.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM'
    encounter2.interactionType = InteractionType.ENCOUNTER;

    let encounter3 = new Interaction();
    encounter3.additionalProperties["caseId"] = 5;
    encounter3.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM'
    encounter3.interactionType = InteractionType.ENCOUNTER;

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
    auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([encounter, encounter2, encounter3]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));

    fixture.detectChanges(); // init
    tick();
    fixture.detectChanges(); // resolve async promises

    expect(comp.loaded).toBeTruthy();
    expect(page.interactions).toContain(
      {origin: encounter.interactionType, createdDate: 'Jun 19, 1996, 10:22:22 PM', caseId: encounter.additionalProperties['caseId']},
      {origin: encounter3.interactionType, createdDate: 'Apr 10, 2000, 3:03:03 AM', caseId: encounter3.additionalProperties['caseId']},
    );
  }));

  it('should not display when loading fails', () => {
    cdssServiceSpy.getCdssSuppliers.and.returnValue(of());
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of());
    auditServiceSpy.getEncounterAudits.and.returnValue(Promise.reject(""));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.reject(""));

    fixture.detectChanges(); // init

    expect(comp.loaded).toBeFalsy();
  });
});
