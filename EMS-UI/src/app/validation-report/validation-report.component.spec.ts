import { Interaction, InteractionType, EmsSupplier, CdssSupplier } from '../model';
import { of } from 'rxjs';
import { AuditService, EmsService } from '../service';
import { ComponentFixture, TestBed, tick, fakeAsync } from '@angular/core/testing';

import { ValidationReportComponent } from './validation-report.component';
import { Predicate, DebugElement, Component, Input, PipeTransform, Pipe } from '@angular/core';
import { CdssService } from '../service';
import { MaterialModule } from '../material.module';
import { By } from '@angular/platform-browser';

@Component({selector: 'app-error-display', template: ''})
class ErrorDisplayStub {
    @Input('errorObject')
    public errorObject: any;
}

@Pipe({name: 'date'})
class MockDatePipe implements PipeTransform {
  transform(value: number): string {
    var date = new Date(value);
    return date.toUTCString(); //for testing, always return UTC
  }
}

let comp: ValidationReportComponent;
let fixture: ComponentFixture<ValidationReportComponent>;
let page: ValidationReportComponentPage;

class ValidationReportComponentPage {

  private getEndpoint(option: number): DebugElement {
    return this.queryAll(By.css('.endpoint'))[option];
  }

  getEndpointRow(option: number): HTMLElement {
    return this.getEndpoint(option).nativeElement;
  }

  getEndpointRowCheckbox(option: number): HTMLElement {
    return this.getEndpoint(option).query(By.css('.endpointCheckbox input')).nativeElement;
  }

  get endpoints() {
    const rows = this.queryAll(By.css('.endpoint'));
    return rows.map(row => {
      const name = row.query(By.css('.endpointName')).nativeElement.innerText;
      const baseUrl = row.query(By.css('.endpointBaseUrl')).nativeElement.innerText;

      return {name, baseUrl};
    });
  }

  private getInteraction(option: number): DebugElement {
    return this.queryAll(By.css('.interaction'))[option];
  }

  getInteractionRow(option: number): HTMLElement {
    return this.getInteraction(option).nativeElement;
  }

  getInteractionRowCheckbox(option: number): HTMLElement {
    return this.getInteraction(option).query(By.css('.interactionCheckbox input')).nativeElement;
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

  get validateButton() {
    return this.query<HTMLButtonElement>(By.css(".actionButton"));
  }

  private queryAll(by: Predicate<DebugElement>): DebugElement[] {
    return fixture.debugElement.queryAll(by);
  }

  private query<T>(by: Predicate<DebugElement>): T {
    return fixture.debugElement.query(by).nativeElement;
  }
}

describe('ValidationReportComponent', () => {

  let emsServiceSpy: { getAllEmsSuppliers: jasmine.Spy };
  let cdssServiceSpy: { getCdssSuppliers: jasmine.Spy };
  let auditServiceSpy: { 
    getEncounterAudits: jasmine.Spy, 
    getServiceDefinitionSearchAudits: jasmine.Spy,
    sendValidationRequest: jasmine.Spy
  };

  beforeEach(() => {
    emsServiceSpy = jasmine.createSpyObj('EmsService', ['getAllEmsSuppliers']);
    cdssServiceSpy = jasmine.createSpyObj('CdssService', ['getCdssSuppliers']);
    auditServiceSpy = jasmine.createSpyObj('AuditService', 
      ['getEncounterAudits', 'getServiceDefinitionSearchAudits', 'sendValidationRequest']);

    TestBed.configureTestingModule({
        imports: [MaterialModule],
        declarations: [ValidationReportComponent, ErrorDisplayStub, MockDatePipe],
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

  function setupSupplierSpies() {
    let cdss = new CdssSupplier();
    cdss.name = "A cdss name";
    cdss.baseUrl = "http://cdss.base.url/fhir";

    let ems = new EmsSupplier();
    ems.name = "An ems name";
    ems.baseUrl = "http://ems.base.url/fhir";

    cdssServiceSpy.getCdssSuppliers.and.returnValue(of([cdss]));
    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([ems]));
    return {cdss, ems};
  }

  function setupInteractionSpies() {
    let encounter = new Interaction();
    encounter.additionalProperties["caseId"] = 4;
    encounter.createdDate = 835222942; //'Jun 19, 1996, 10:22:22 PM' (UTC)
    encounter.interactionType = InteractionType.ENCOUNTER;

    let sdSearch = new Interaction();
    sdSearch.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM' (UTC)
    sdSearch.interactionType = InteractionType.SERVICE_SEARCH;

    auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([encounter]));
    auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([sdSearch]));
    return {encounter, sdSearch};
  }

  describe('Component', () => {

    it('should create', () => {
      expect(comp).toBeTruthy();
    });
  
    it('should display end points', fakeAsync(() => {
      let {cdss, ems} = setupSupplierSpies();
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
      let {encounter, sdSearch} = setupInteractionSpies();
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges(); // resolve async promises
  
      expect(comp.loaded).toBeTruthy();
      expect(page.interactions).toContain(
        {
          origin: encounter.interactionType, 
          createdDate: new Date(encounter.createdDate * 1000).toUTCString(), 
          caseId: encounter.additionalProperties['caseId']
        },
        {
          origin: sdSearch.interactionType, 
          createdDate: new Date(sdSearch.createdDate * 1000).toUTCString(), 
          caseId: 0
        }
      );
    }));
  
    it('should display one interaction per encounter', fakeAsync(() => {
      let encounter = new Interaction();
      encounter.additionalProperties["caseId"] = 4;
      encounter.createdDate = 835222942; //'Jun 19, 1996, 10:22:22 PM' (UTC)
      encounter.interactionType = InteractionType.ENCOUNTER;
  
      let encounter2 = new Interaction();
      encounter2.additionalProperties["caseId"] = 4;
      encounter2.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM' (UTC)
      encounter2.interactionType = InteractionType.ENCOUNTER;
  
      let encounter3 = new Interaction();
      encounter3.additionalProperties["caseId"] = 5;
      encounter3.createdDate = 955335783; //'Apr 10, 2000, 3:03:03 AM' (UTC)
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
        {
          origin: encounter.interactionType, 
          createdDate: new Date(encounter.createdDate * 1000).toUTCString(), 
          caseId: encounter.additionalProperties['caseId']
        },
        {
          origin: encounter3.interactionType, 
          createdDate: new Date(encounter3.createdDate * 1000).toUTCString(), 
          caseId: encounter3.additionalProperties['caseId']
        },
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

  describe('Endpont Table Selection', () => {

    it('should select one when clicking on endpoint row', fakeAsync(() => {
      let {cdss,ems} = setupSupplierSpies();
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getEndpointRow(1).click();
  
      fixture.detectChanges(); // clicked
  
      expect(comp.endpointSelection.selected).toContain(cdss);
      expect(comp.endpointSelection.selected).not.toContain(ems);
    }));
  
    it('should select one when clicking on endpoint checkbox', fakeAsync(() => {
      let {cdss,ems} = setupSupplierSpies();
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getEndpointRowCheckbox(1).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.endpointSelection.selected).toContain(cdss);
      expect(comp.endpointSelection.selected).not.toContain(ems);
    }));
  
    it('should deselect when clicking on endpoint row', fakeAsync(() => {
      let {cdss} = setupSupplierSpies();
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
  
      comp.endpointSelection.select(cdss);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getEndpointRow(1).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.endpointSelection.selected).toEqual([]);
    }));
  
    it('should deselect when clicking on endpoint checkbox', fakeAsync(() => {
      let {cdss} = setupSupplierSpies();
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
  
      comp.endpointSelection.select(cdss);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getEndpointRowCheckbox(1).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.endpointSelection.selected).toEqual([]);
    }));
  
    it('should deselect existing when selecting a different endpoint', fakeAsync(() => {
      let {cdss,ems} = setupSupplierSpies();
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
  
      comp.endpointSelection.select(cdss);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getEndpointRow(0).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.endpointSelection.selected).not.toContain(cdss);
      expect(comp.endpointSelection.selected).toContain(ems);
    }));
  });

  describe("Interaction Table Selection", () => {

    it('should select one when clicking on interaction row', fakeAsync(() => {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      let {encounter, sdSearch} = setupInteractionSpies();
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getInteractionRow(0).click();
  
      fixture.detectChanges(); // clicked
  
      expect(comp.interactionSelection.selected).toContain(encounter);
      expect(comp.interactionSelection.selected).not.toContain(sdSearch);
    }));
  
    it('should select one when clicking on interaction checkbox', fakeAsync(() => {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      let {encounter, sdSearch} = setupInteractionSpies();
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getInteractionRowCheckbox(0).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.interactionSelection.selected).toContain(encounter);
      expect(comp.interactionSelection.selected).not.toContain(sdSearch);
    }));
  
    it('should deselect when clicking on interaction row', fakeAsync(() => {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      let {encounter} = setupInteractionSpies();
  
      comp.interactionSelection.select(encounter);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getInteractionRow(0).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.interactionSelection.selected).toEqual([]);
    }));
  
    it('should deselect when clicking on interaction checkbox', fakeAsync(() => {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      let {encounter} = setupInteractionSpies();
  
      comp.interactionSelection.select(encounter);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getInteractionRowCheckbox(0).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.interactionSelection.selected).toEqual([]);
    }));
  
    it('should deselect existing when selecting a different interaction', fakeAsync(() => {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      let {encounter, sdSearch} = setupInteractionSpies();
  
      comp.interactionSelection.select(encounter);
  
      fixture.detectChanges(); // init
      tick();
      fixture.detectChanges();
  
      page.getInteractionRow(1).click();
  
      tick(10000); // we don't know why this is necessary; $event.stopPropagation is a possible suspect
      fixture.detectChanges(); // clicked
  
      expect(comp.interactionSelection.selected).not.toContain(encounter);
      expect(comp.interactionSelection.selected).toContain(sdSearch);
    }));
  });

  describe("Send Validation Report", () => {

    function initEmpty() {
      cdssServiceSpy.getCdssSuppliers.and.returnValue(of([]));
      emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([]));
      auditServiceSpy.getEncounterAudits.and.returnValue(Promise.resolve([]));
      auditServiceSpy.getServiceDefinitionSearchAudits.and.returnValue(Promise.resolve([]));
    }

    it('should send validation request to validation service', fakeAsync(() => {
      initEmpty();
  
      let selectedSupplier = new EmsSupplier();
      selectedSupplier.baseUrl = "this.is.a.fake";
      let selectedInteraction = new Interaction();
      selectedInteraction.id = "someguid";
      selectedInteraction.interactionType = InteractionType.ENCOUNTER;
      selectedInteraction.additionalProperties["caseId"] = "6";
  
      comp.endpointSelection.select(selectedSupplier);
      comp.interactionSelection.select(selectedInteraction);
  
      fixture.detectChanges(); //init
      tick();
      fixture.detectChanges();
  
      page.validateButton.click();
      fixture.detectChanges();
  
      expect(auditServiceSpy.sendValidationRequest).toHaveBeenCalledWith(
        jasmine.objectContaining({
          instanceBaseUrl: "this.is.a.fake",
          searchAuditId: "someguid",
          type: InteractionType.ENCOUNTER,
          caseId: "6"
        })
      );
    }));
    
    it('should disable button if both tables don\'t have selection', fakeAsync(() => {
      initEmpty();

      let selectedSupplier = new EmsSupplier();
      selectedSupplier.id = 5;

      comp.endpointSelection.select(selectedSupplier);

      fixture.detectChanges(); //init
      tick();
      fixture.detectChanges();

      expect(page.validateButton.disabled).toBeTruthy();
    }));

    it('should enable button if both tables have selection', fakeAsync(() => {
      initEmpty();

      let selectedSupplier = new EmsSupplier();
      selectedSupplier.id = 5;
      let selectedInteraction = new Interaction();
      selectedInteraction.id = "someguid";
      selectedInteraction.additionalProperties["caseId"] = "6";
  
      comp.endpointSelection.select(selectedSupplier);
      comp.interactionSelection.select(selectedInteraction);
  
      fixture.detectChanges(); //init
      tick();
      fixture.detectChanges();

      expect(page.validateButton.disabled).toBeFalsy();
    }));
  });
});
