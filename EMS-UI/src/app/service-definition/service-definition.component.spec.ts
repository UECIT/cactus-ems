import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceDefinitionComponent } from './service-definition.component';
import { of } from 'rxjs';
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { configureSessionProviders } from 'src/app/testing/session-helper';
import { DebugElement, Predicate, SimpleChange, SimpleChanges} from '@angular/core';
import { By } from '@angular/platform-browser';
import { MaterialModule } from 'src/app/material.module';
import { ServiceDefinitionService } from '../service';
import { NgxJsonViewerModule } from 'ngx-json-viewer';
import { FindValueSubscriber } from 'rxjs/internal/operators/find';

let comp: ServiceDefinitionComponent;
let fixture: ComponentFixture<ServiceDefinitionComponent>;
let page: Page;

class Page {

    get serviceDefId() {return this.query<HTMLParagraphElement>(By.css('#serviceDefId'));}
    get serviceDefJson() {return this.query<HTMLElement>(By.css('#serviceDefJson'));}

    private query<T>(by: Predicate<DebugElement>): T {
        return fixture.debugElement.query(by).nativeElement;
    }
}

describe('Service Definition Component', () => {

    let serviceDefinitionServiceSpy: {getServiceDefinition: jasmine.Spy};

    beforeEach(() => {
        serviceDefinitionServiceSpy = 
            jasmine.createSpyObj('ServiceDefinitionService', ['getServiceDefinition']);

        configureSessionProviders();
        TestBed.configureTestingModule({
            imports: [MaterialModule, NgxJsonViewerModule, BrowserAnimationsModule],
            declarations: [ServiceDefinitionComponent],
            providers: [{provide: ServiceDefinitionService, useValue: serviceDefinitionServiceSpy}]
        });
        fixture = TestBed.createComponent(ServiceDefinitionComponent);
        comp = fixture.componentInstance;
        fixture.detectChanges();
        page = new Page();
    });

    it('should display service definition on change', fakeAsync(() => {
        const jsonString = JSON.stringify({prop: "value"});
        serviceDefinitionServiceSpy.getServiceDefinition.and.returnValue(of(jsonString));

        const changes: SimpleChanges = {
            selectedSupplier: new SimpleChange(null, 3, true),
            selectedServiceDefinition: new SimpleChange(null, "serviceId", true)
        };

        comp.ngOnChanges(changes);
        tick();
        fixture.detectChanges();

        expect(page.serviceDefId.textContent).toBe('serviceId');
        expect(page.serviceDefJson.textContent).toContain(jsonString);
        expect(serviceDefinitionServiceSpy.getServiceDefinition)
            .toHaveBeenCalledWith(3, "serviceId");
    }));

    it('should update service when supplier changed', fakeAsync(() => {
        const jsonString = JSON.stringify({prop: "value"});
        serviceDefinitionServiceSpy.getServiceDefinition.and.returnValue(of(jsonString));

        const changes: SimpleChanges = {
            selectedSupplier: new SimpleChange(3, 5, false),
        };

        comp.tempSelectedServiceDefinitionId = "unchanged";

        comp.ngOnChanges(changes);
        tick();
        fixture.detectChanges();

        expect(page.serviceDefId.textContent).toBe('unchanged');
        expect(page.serviceDefJson.textContent).toContain(jsonString);
        expect(serviceDefinitionServiceSpy.getServiceDefinition)
            .toHaveBeenCalledWith(5, "unchanged");
    }));

    it('should update service when service changed', fakeAsync(() => {
        const jsonString = JSON.stringify({prop: "value"});
        serviceDefinitionServiceSpy.getServiceDefinition.and.returnValue(of(jsonString));

        const changes: SimpleChanges = {
            selectedServiceDefinition: new SimpleChange("old", "new", FindValueSubscriber)
        };

        comp.cdssSupplierId = 9;

        comp.ngOnChanges(changes);
        tick();
        fixture.detectChanges();

        expect(page.serviceDefId.textContent).toBe('new');
        expect(page.serviceDefJson.textContent).toContain(jsonString);
        expect(serviceDefinitionServiceSpy.getServiceDefinition)
            .toHaveBeenCalledWith(9, "new");
    }));
});
