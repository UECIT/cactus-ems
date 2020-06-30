import { Interaction, InteractionType } from './../model/audit';
import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { AuditService } from './audit.service';
import { asyncData } from '../testing/async-observable-helpers';
import { SESSION_STORAGE_OBJECT, SessionStorage, SERDES_OBJECT, STORAGE_OPTIONS } from 'h5webstorage';

xdescribe('Audit Service', () => {

    let httpClientSpy: { get: jasmine.Spy};
    let auditService: AuditService;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
        TestBed.configureTestingModule({
            providers: [
                {provide: HttpClient, useValue: httpClientSpy},
                // TODO: this is not working, tests are disabled for now. CDSCT-336
                {provide: SESSION_STORAGE_OBJECT, useValue: {"auth_token": "testtoken"}},
                {provide: SERDES_OBJECT, useValue: {stringify: JSON.stringify, parse: JSON.parse}},
                {provide: STORAGE_OPTIONS, useValue: {}},
                SessionStorage,
                AuditService,
            ]
        });
        auditService = TestBed.get(AuditService);
    });

    it('should get encounter audits', () => {
        const expectedInteraction: Interaction = {
            requestId: "someid",
            interactionType: InteractionType.ENCOUNTER,
            createdDate: 835222942,
            additionalProperties: new Map([['caseId', '4']])
        };

        httpClientSpy.get.and.returnValue(asyncData(expectedInteraction));

        auditService.getEncounterAudits()
            .then(interactionArray => {
                expect(interactionArray)
                    .toContain(expectedInteraction, 'expected interaction');
            })
            .catch(fail);
        expect(httpClientSpy.get.calls.count()).toBe(1, 'one call');
        //TODO: complete CDSCT-336
    });

    it('should get service definition search audits', () => {
        const expectedInteraction: Interaction = {
            requestId: "someguid",
            interactionType: InteractionType.SERVICE_SEARCH,
            createdDate: 835222942,
            additionalProperties: new Map([])
        };

        httpClientSpy.get.and.returnValue(asyncData(expectedInteraction));

        auditService.getServiceDefinitionSearchAudits()
            .then(interactionArray => {
                expect(interactionArray)
                    .toContain(expectedInteraction, 'expected interaction');
            })
            .catch(fail);
        expect(httpClientSpy.get.calls.count()).toBe(1, 'one call');
        //TODO: complete CDSCT-336
    });
})