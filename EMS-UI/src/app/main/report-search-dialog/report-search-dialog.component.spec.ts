import { EncounterReportInput } from './../../model/encounterReportInput';
import { FormsModule } from '@angular/forms';
import { MatDialogRef } from '@angular/material';
import { ReportSearchDialogComponent } from './report-search-dialog.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { configureSessionProviders } from 'src/app/testing/session-helper';
import { DebugElement, Predicate, Component, Input} from '@angular/core';
import { By } from '@angular/platform-browser';
import { MaterialModule } from 'src/app/material.module';
import { ReportService } from 'src/app/service';

@Component({selector: 'app-error-display', template: ''})
class ErrorDisplayStub {
    @Input('errorObject')
    public errorObject: any;
}

let comp: ReportSearchDialogComponent;
let fixture: ComponentFixture<ReportSearchDialogComponent>;
let page: Page;

class Page {

    get nhsSearchInput() {return this.query<HTMLInputElement>(By.css('#nhsNumber'));}
    get searchButton() {return this.query<HTMLButtonElement>(By.css('#search'));}
    get searchError() {
        const element = fixture.debugElement.query(By.css('#searchError'));
        return element.injector.get(ErrorDisplayStub) as ErrorDisplayStub;
    }
    get fetchReportError() {
        const element = fixture.debugElement.query(By.css('#fetchReportError'));
        return element.injector.get(ErrorDisplayStub) as ErrorDisplayStub;
    }
    get cancelSearch() {return this.query<HTMLButtonElement>(By.css('#cancel'));}
    get continueEncounter() {return this.query<HTMLButtonElement>(By.css('#continue'));}

    encounterHeader(encounterId: string): HTMLElement {
        return this.query<HTMLElement>(By.css('#' + encounterId));
    }

    private query<T>(by: Predicate<DebugElement>): T {
        return fixture.debugElement.query(by).nativeElement;
    }
}

fdescribe('Report Search Dialog Component', () => {

    let reportServiceSpy: {searchByPatient: jasmine.Spy, getEncounterReport: jasmine.Spy};
    let dialogRefSpy: {close: jasmine.Spy};

    beforeEach(() => {
        reportServiceSpy = 
            jasmine.createSpyObj('ReportService', ['searchByPatient', 'getEncounterReport']);
        dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

        configureSessionProviders();
        TestBed.configureTestingModule({
            imports: [MaterialModule, BrowserAnimationsModule, FormsModule],
            declarations: [ReportSearchDialogComponent, ErrorDisplayStub],
            providers: [
                {provide: ReportService, useValue: reportServiceSpy},
                {provide: MatDialogRef, useValue: dialogRefSpy}
            ]
        });
        fixture = TestBed.createComponent(ReportSearchDialogComponent);
        comp = fixture.componentInstance;
        fixture.detectChanges();
        page = new Page();
    });

    it('should close dialog on cancel', () => {
        expect(page.nhsSearchInput).toBeDefined();
        page.cancelSearch.click();
        expect(dialogRefSpy.close).toHaveBeenCalledWith();
    });

    it('should disable search when nhs number not provided', () => {
        expect(page.nhsSearchInput.textContent).toBe("");
        expect(page.searchButton.disabled).toBeTruthy();
        comp.nhsNumber = "1234567";
        fixture.detectChanges();
        expect(page.searchButton.disabled).toBeFalsy();
        comp.nhsNumber = null;
        fixture.detectChanges();  
        expect(page.searchButton.disabled).toBeTruthy();
    });

    it('should search for encounters and render results', fakeAsync(() => {
        comp.nhsNumber = "1234567";
        let foundEncounters = ["encounter1", "encounter2", "encounter3"]
        reportServiceSpy.searchByPatient.and.returnValue(Promise.resolve(foundEncounters));

        comp.search();
        tick();
        fixture.detectChanges();

        expect(page.encounterHeader("encounter1")).toBeDefined();
        expect(page.encounterHeader("encounter2")).toBeDefined();
        expect(page.encounterHeader("encounter3")).toBeDefined();
        expect(reportServiceSpy.searchByPatient).toHaveBeenCalledWith("1234567");
    }));

    it('should display error when search fails', fakeAsync(() => {
        comp.nhsNumber = "1234567";
        let err = "Some error object";
        reportServiceSpy.searchByPatient.and.returnValue(Promise.reject(err));

        comp.search();
        tick();
        fixture.detectChanges();

        expect(page.searchError.errorObject).toBe(err);
        expect(reportServiceSpy.searchByPatient).toHaveBeenCalledWith("1234567");
    }));

    it('should fetch and render encounter report', fakeAsync(() => {
        let encounterID = "encounter4";
        comp.encountersFound = [encounterID];
        let encounterReport: EncounterReportInput = {
            encounterId: "enctouner4",
            encounterStart: "14/11/2005",
            encounterEnd: "15/11/2005",
            patientId: "patientId",
            observations: []
        }
        reportServiceSpy.getEncounterReport.and.returnValue(Promise.resolve(encounterReport))
        fixture.detectChanges();

        page.encounterHeader(encounterID).click();
        tick();
        
        expect(reportServiceSpy.getEncounterReport).toHaveBeenCalledWith(encounterID);
        expect(comp.selectedReport).toBe(encounterReport);
    }));

    it('should display error when fetch encounter report fails', fakeAsync(() => {
        let encounterID = "encounter4";
        comp.encountersFound = [encounterID];
        let err = "some error object";
        reportServiceSpy.getEncounterReport.and.returnValue(Promise.reject(err))
        fixture.detectChanges();

        page.encounterHeader(encounterID).click();
        tick();
        fixture.detectChanges();
        
        expect(reportServiceSpy.getEncounterReport).toHaveBeenCalledWith(encounterID);
        expect(page.fetchReportError.errorObject).toBe(err);
    }));

    it('should close dialog with encounter ID on handover', fakeAsync(() => {
        let encounterID = "encounter5";
        comp.encountersFound = [encounterID];
        let encounterReport: EncounterReportInput = {
            encounterId: encounterID,
            encounterStart: "14/11/2005",
            encounterEnd: "15/11/2005",
            patientId: "patientId",
            observations: []
        }
        reportServiceSpy.getEncounterReport.and.returnValue(Promise.resolve(encounterReport))
        fixture.detectChanges();

        page.encounterHeader(encounterID).click();
        tick();
        fixture.detectChanges();
        page.continueEncounter.click();

        expect(dialogRefSpy.close).toHaveBeenCalledWith(encounterID);
    }));
});
