import { of } from 'rxjs';
import { Router, ActivatedRoute, RouterModule } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { configureSessionProviders } from 'src/app/testing/session-helper';
import { CreateCdssSupplierComponent } from './create-cdss-supplier.component';
import { CdssService } from 'src/app/service/cdss.service';
import { DebugElement, Predicate, Component, Input } from '@angular/core';
import { By } from '@angular/platform-browser';
import { MaterialModule } from 'src/app/material.module';
import { LoginService } from 'src/app/service/login.service';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { setInput } from 'src/app/testing/angular-helper';
import { ResourceReferenceType } from 'src/app/model';

@Component({selector: 'app-error-display', template: ''})
class ErrorDisplayStub {
    @Input('errorObject')
    public errorObject: any;
}

let comp: CreateCdssSupplierComponent;
let fixture: ComponentFixture<CreateCdssSupplierComponent>;
let page: Page;

class Page {
    get nameInput() {return this.query<HTMLInputElement>(By.css('#cdssNameInput')); }
    get baseUrlInput() {return this.query<HTMLInputElement>(By.css('#cdssBaseUrlInput')); }
    get supportedApiVersionDropdown() {return this.query<HTMLSelectElement>(By.css('#supportedVersion')); }
    get saveButton() {return this.query<HTMLButtonElement>(By.css('#saveCdssSupplier')); }
    get authToken() {return this.query<HTMLInputElement>(By.css('#authToken')); }

    supportedApiVersionOptions(option: number): HTMLOptionElement {
        return this.queryAll(By.css('.versionOption'))[option].nativeElement;
    }

    private query<T>(by: Predicate<DebugElement>): T {
        return fixture.debugElement.query(by).nativeElement;
    }

    private queryAll(by: Predicate<DebugElement>): DebugElement[] {
        return fixture.debugElement.queryAll(by);
    }
}

describe('Create CDSS Supplier Component', () => {

    let cdssServiceSpy: {createCdssSupplier: jasmine.Spy};

    beforeEach(() => {
        cdssServiceSpy = jasmine.createSpyObj('CdssService', ['createCdssSupplier']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']); // router stub
        const loginServiceSpy = jasmine.createSpyObj('LoginService', ['logout']);

        configureSessionProviders();
        TestBed.configureTestingModule({
            // Angular requires all these as imports for some reason
            imports: [MaterialModule, ReactiveFormsModule, FormsModule, RouterModule, BrowserAnimationsModule],
            declarations: [CreateCdssSupplierComponent, ErrorDisplayStub],
            providers: [
                // Provide activated route because angular wants it even though it's not used
                {provide: ActivatedRoute, useValue: {params: of({id: 123})}},
                {provide: CdssService, useValue: cdssServiceSpy},
                {provide: LoginService, useValue: loginServiceSpy},
                {provide: Router, useValue: routerSpy}
            ]
        });
        fixture = TestBed.createComponent(CreateCdssSupplierComponent);
        comp = fixture.componentInstance;
        fixture.detectChanges();
        page = new Page();
    });

    it('should display supported api versions dropdown', () => {
        page.supportedApiVersionDropdown.click(); // have to click the dropdown to add options to DOM
        fixture.detectChanges();

        expect(page.supportedApiVersionOptions(0).textContent).toBe('1.1');
        expect(page.supportedApiVersionOptions(1).textContent).toBe('2.0');
    });

    it('should create a new cdss supplier with defaults', () => {
        setInput(page.nameInput, 'test name');
        setInput(page.baseUrlInput, 'test base url');
        fixture.detectChanges();

        cdssServiceSpy.createCdssSupplier.and.returnValue(of());
        page.saveButton.click();

        expect(cdssServiceSpy.createCdssSupplier.calls.count()).toEqual(1);
        expect(cdssServiceSpy.createCdssSupplier)
            .toHaveBeenCalledWith(jasmine.objectContaining({
                name: 'test name',
                baseUrl: 'test base url',
                inputDataRefType: ResourceReferenceType.ByReference,
                inputParamsRefType: ResourceReferenceType.ByReference,
                supportedVersion: '1.1',
                authToken: '',
            }));
    });

    it('should create a new cdss supplier with changed supported api version', () => {
        setInput(page.nameInput, 'test name');
        setInput(page.baseUrlInput, 'test base url');
        page.supportedApiVersionDropdown.click(); // have to click the dropdown to add options to DOM
        fixture.detectChanges();
        page.supportedApiVersionOptions(1).click();
        fixture.detectChanges();

        cdssServiceSpy.createCdssSupplier.and.returnValue(of());
        page.saveButton.click();

        expect(cdssServiceSpy.createCdssSupplier.calls.count()).toEqual(1);
        expect(cdssServiceSpy.createCdssSupplier)
            .toHaveBeenCalledWith(jasmine.objectContaining({supportedVersion: '2.0'}));
    });

    it('should create a new cdss supplier with authToken', () => {
        setInput(page.nameInput, 'test name');
        setInput(page.baseUrlInput, 'test base url');
        setInput(page.authToken, 'token');
        fixture.detectChanges();

        cdssServiceSpy.createCdssSupplier.and.returnValue(of());
        page.saveButton.click();

        expect(cdssServiceSpy.createCdssSupplier.calls.count()).toEqual(1);
        expect(cdssServiceSpy.createCdssSupplier)
        .toHaveBeenCalledWith(jasmine.objectContaining({
            authToken: 'token',
        }));
    });

});
