import { environment } from './../environments/environment.prod';
import { Environment } from './model/environment';
import { AppComponent } from './app.component';
import { SessionStorage, SESSION_STORAGE_OBJECT, SERDES_OBJECT, STORAGE_OPTIONS } from "h5webstorage"
import { EnvironmentService } from "./service/environment.service"
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Component, Predicate, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';

@Component({selector: 'app-navigation', template: ''})
class NavigationComponentStub {}

@Component({selector: 'router-outlet', template: ''})
class RouterOutletStub {}

let comp: AppComponent;
let fixture: ComponentFixture<AppComponent>;
let page: Page;

class Page {
    constructor(fixture: ComponentFixture<AppComponent>) {}
    
    get title() {return this.query<HTMLHeadingElement>('h5')}
    get environment() {return this.queryCss<HTMLElement>(By.css(".buildinfo")).querySelectorAll('p')}
    get envAppVersion() {return this.environment[0]}
    get envName() {return this.environment[1]}
    get envApiVersion() {return this.environment[2]}

    private query<T>(selector: string): T {
        return fixture.nativeElement.querySelector(selector);
    }

    private queryCss<T>(by: Predicate<DebugElement>): T {
        return fixture.debugElement.query(by).nativeElement;
    }
}

describe('App Component', () => {

    let envServiceSpy: { getVariables: jasmine.Spy};

    beforeEach(() => {
        envServiceSpy = jasmine.createSpyObj("EnvironmentService", ['getVariables']);
        TestBed.configureTestingModule({
            declarations: [
                AppComponent,
                NavigationComponentStub,
                RouterOutletStub
            ],
            providers: [
                {provide: EnvironmentService, useValue: envServiceSpy},
                {provide: SESSION_STORAGE_OBJECT, useValue: sessionStorage},
                {provide: SERDES_OBJECT, useValue: {stringify: JSON.stringify, parse: JSON.parse}}, //Raise a tech debt: move it to a common before step
                {provide: STORAGE_OPTIONS, useValue: {}},
                SessionStorage,
                AppComponent
            ]
        });
        fixture = TestBed.createComponent(AppComponent);
        comp = fixture.componentInstance;
        page = new Page(fixture);
    });

    it('should contain ems title', () => {
        expect(page.title.textContent).toEqual('EMS Test Harness');
    });

    it('should fetch and render build info', fakeAsync(() => {
        const env = {name: "envname", apiVersion: "1.2.3", appVersion: "3.2.1"}
        envServiceSpy.getVariables.and.returnValue(Promise.resolve(env));

        fixture.detectChanges(); // invoke onInit()
        tick(); // wait for async call to environment service
        fixture.detectChanges();

        expect(page.envName.textContent).toContain(env.name);
        expect(page.envApiVersion.textContent).toContain(env.apiVersion);
        expect(page.envAppVersion.textContent).toContain(env.appVersion);
    }));
})