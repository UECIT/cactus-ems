import { Interaction } from '../model';
import { HttpClient } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { AuditService } from './audit.service';
import { asyncData } from '../testing/async-observable-helpers';
import { SESSION_STORAGE_OBJECT, SessionStorage, SERDES_OBJECT, STORAGE_OPTIONS } from 'h5webstorage';
import * as moment from "moment";

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

    it('should get interaction audits', () => {
        const expectedInteraction: Interaction = {
            interactionId: "someId",
            type: "Encounter",
            startedAt: moment.unix(835222942)
        };

        httpClientSpy.get.and.returnValue(asyncData(expectedInteraction));

        auditService.getAudits()
            .then(interactionArray =>
                expect(interactionArray).toContain(expectedInteraction, 'expected interaction'))
            .catch(fail);
        expect(httpClientSpy.get.calls.count()).toBe(1, 'one call');
        //TODO: complete CDSCT-336
    });
})