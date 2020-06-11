import {Observable, of, Subscriber} from 'rxjs';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {ComponentFixture, TestBed} from '@angular/core/testing';
import {configureSessionProviders} from 'src/app/testing/session-helper';
import {EditEmsDialog, EmsSupplierComponent} from './ems-supplier.component';
import {Component, DebugElement, Input, Predicate} from '@angular/core';
import {MaterialModule} from 'src/app/material.module';
import {LoginService} from 'src/app/service/login.service';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {EmsService} from '../../service/ems.service';
import {BrowserDynamicTestingModule} from '@angular/platform-browser-dynamic/testing';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {By} from '@angular/platform-browser';
import {setInput} from '../../testing/angular-helper';

@Component({selector: 'app-error-display', template: ''})
class ErrorDisplayStub {
  @Input('errorObject')
  public errorObject: any;
}


class EmsEditDialogPage {
  fixture: ComponentFixture<EditEmsDialog>;

  constructor(fixture: ComponentFixture<EditEmsDialog>) {
    this.fixture = fixture;
  }

  get comp() {
    return this.fixture.componentInstance;
  }

  get nameInput() {
    return this.query<HTMLInputElement>(By.css('.emsName'));
  }

  get baseUrlInput() {
    return this.query<HTMLInputElement>(By.css('.emsBaseUrl'));
  }

  get authTokenInput() {
    return this.query<HTMLInputElement>(By.css('.emsAuthToken'));
  }

  get saveButton() {
    return this.query<HTMLButtonElement>(By.css('.saveEms'));
  }

  private query<T>(by: Predicate<DebugElement>): T {
    return this.fixture.debugElement.query(by).nativeElement;
  }
}


class EmsSupplierComponentPage {
  fixture: ComponentFixture<EmsSupplierComponent>;

  constructor(fixture: ComponentFixture<EmsSupplierComponent>) {
    this.fixture = fixture;
  }

  get comp() {
    return this.fixture.componentInstance;
  }

  get emsSupplierRows() {
    return this.queryAll(By.css('.emsSupplier'));
  }

  get emsSuppliers() {
    const rows = this.emsSupplierRows;
    return rows.map(row => {
      const name = row.query(By.css('.emsSupplierName')).nativeElement.innerText;
      const baseUrl = row.query(By.css('.emsSupplierBaseUrl')).nativeElement.innerText;

      return {name, baseUrl};
    });
  }

  private query<T>(by: Predicate<DebugElement>): T {
    return this.fixture.debugElement.query(by).nativeElement;
  }

  private queryAll(by: Predicate<DebugElement>): DebugElement[] {
    return this.fixture.debugElement.queryAll(by);
  }
}

export class MatDialogMock {
  dialogRef = new MatDialogRefMock();

  open(dialog, config) {
    return this.dialogRef;
  }
}

export class MatDialogRefMock {
  sub: Subscriber<any>;

  afterClosed() {
    return Observable.create((sub) => {
      this.sub = sub;
    });
  }

  close(value = '') {
    this.sub.next(value);
    this.sub.complete();
  }
}

describe('EMS Dialog Template', () => {

  let fixture: ComponentFixture<EditEmsDialog>;
  let emsEditDialogPage: EmsEditDialogPage;
  let dialogSpy: { close: jasmine.Spy };

  beforeEach(() => {

    const routerSpy = jasmine.createSpyObj('Router', ['navigate']); // router stub
    const loginServiceSpy = jasmine.createSpyObj('LoginService', ['logout']);

    dialogSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    configureSessionProviders();
    TestBed.configureTestingModule({
      // Angular requires all these as imports for some reason
      imports: [MaterialModule, ReactiveFormsModule, FormsModule, RouterModule, BrowserAnimationsModule],
      declarations: [EditEmsDialog, ErrorDisplayStub],
      providers: [
        // Provide activated route because angular wants it even though it's not used
        {provide: ActivatedRoute, useValue: {params: of({id: 123})}},
        {provide: LoginService, useValue: loginServiceSpy},
        {provide: Router, useValue: routerSpy},
        {provide: MatDialogRef, useValue: dialogSpy},
        {provide: MAT_DIALOG_DATA, useValue: {name: 'supplier1', baseUrl: 'baseUrl1'}},
      ]
    })
    .overrideModule(BrowserDynamicTestingModule, {set: {entryComponents: [EditEmsDialog]}});
    fixture = TestBed.createComponent(EditEmsDialog);
    emsEditDialogPage = new EmsEditDialogPage(fixture);

    fixture.detectChanges();
  });

  it('should create a new ems supplier with defaults', () => {
    setInput(emsEditDialogPage.nameInput, 'test name');
    setInput(emsEditDialogPage.baseUrlInput, 'test base url');
    fixture.detectChanges();

    emsEditDialogPage.saveButton.click();
    fixture.detectChanges();

    expect(dialogSpy.close)
    .toHaveBeenCalledWith(jasmine.objectContaining({
      name: 'test name',
      baseUrl: 'test base url'
    }));
  });

  it('should create a new ems supplier with authToken', () => {
    setInput(emsEditDialogPage.nameInput, 'test name');
    setInput(emsEditDialogPage.baseUrlInput, 'test base url');
    setInput(emsEditDialogPage.authTokenInput, 'token');
    fixture.detectChanges();

    emsEditDialogPage.saveButton.click();
    fixture.detectChanges();

    expect(dialogSpy.close)
    .toHaveBeenCalledWith(jasmine.objectContaining({
      authToken: 'token',
    }));
  });

  it('should display existing supplier for editing', done => {
    setTimeout(() => {
      expect(emsEditDialogPage.nameInput.value).toEqual('supplier1');
      expect(emsEditDialogPage.baseUrlInput.value).toEqual('baseUrl1');
      done();
    });
  });
});

describe('EMS Supplier Component', () => {

  let emsServiceSpy: { createOrUpdateEms: jasmine.Spy, getAllEmsSuppliers: jasmine.Spy };
  let fixture: ComponentFixture<EmsSupplierComponent>;
  let emsSuppliersPage: EmsSupplierComponentPage;

  beforeEach(() => {

    emsServiceSpy = jasmine.createSpyObj('EmsService', ['createOrUpdateEms', 'getAllEmsSuppliers']);
    const routerSpy = jasmine.createSpyObj('Router', ['navigate']); // router stub
    const loginServiceSpy = jasmine.createSpyObj('LoginService', ['logout']);

    emsServiceSpy.getAllEmsSuppliers.and.returnValue(of([
      {
        name: 'supplier1',
        baseUrl: 'baseUrl1'
      }, {
        name: 'supplier2',
        baseUrl: 'baseUrl2'
      }
    ]));

    configureSessionProviders();
    TestBed.configureTestingModule({
      // Angular requires all these as imports for some reason
      imports: [MaterialModule, ReactiveFormsModule, FormsModule, RouterModule, BrowserAnimationsModule],
      declarations: [EmsSupplierComponent, EditEmsDialog, ErrorDisplayStub],
      providers: [
        // Provide activated route because angular wants it even though it's not used
        {provide: ActivatedRoute, useValue: {params: of({id: 123})}},
        {provide: EmsService, useValue: emsServiceSpy},
        {provide: LoginService, useValue: loginServiceSpy},
        {provide: Router, useValue: routerSpy},
        {provide: MatDialog, useClass: MatDialogMock}
      ]
    })
    .overrideModule(BrowserDynamicTestingModule, {set: {entryComponents: [EditEmsDialog]}});
    fixture = TestBed.createComponent(EmsSupplierComponent);
    emsSuppliersPage = new EmsSupplierComponentPage(fixture);

    fixture.detectChanges();
  });

  it('should list registered suppliers', () => {

    expect(emsServiceSpy.getAllEmsSuppliers.calls.count()).toEqual(1);

    const suppliers = emsSuppliersPage.emsSuppliers;
    expect(suppliers.length).toEqual(2);
    expect(suppliers).toContain({name: 'supplier1', baseUrl: 'baseUrl1'});
    expect(suppliers).toContain({name: 'supplier2', baseUrl: 'baseUrl2'});
  });

  it('should create a new ems supplier', done => {
    emsSuppliersPage.comp.edit(null);
    fixture.detectChanges();

    emsServiceSpy.createOrUpdateEms.and.returnValue(of().toPromise());
    const dialog = TestBed.get(MatDialog);
    const dialogRef = dialog.dialogRef;
    dialogRef.close({
      name: 'test name',
      baseUrl: 'test base url',
      authToken: '',
    });
    fixture.detectChanges();

    setTimeout(() => {
      expect(emsServiceSpy.createOrUpdateEms.calls.count()).toEqual(1);
      expect(emsServiceSpy.createOrUpdateEms)
      .toHaveBeenCalledWith(jasmine.objectContaining({
        name: 'test name',
        baseUrl: 'test base url',
        authToken: '',
      }));
      done();
    });
  });
});


