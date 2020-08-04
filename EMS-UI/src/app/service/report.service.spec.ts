import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { asyncData } from '../testing/async-observable-helpers';
import {configureSessionProviders} from "../testing/session-helper";
import {ReportService} from "./report.service";
import {EncounterReportInput} from "../model";

describe('Report Service', () => {

    const fakeAuthToken = "FAKE_AUTH_TOKEN";
    let httpClientSpy: { get: jasmine.Spy};
    let reportService: ReportService;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
        const authSpy: {getAuthToken: jasmine.Spy} =
            jasmine.createSpyObj('AuthService', ['getAuthToken']);
        configureSessionProviders(); //TODO: CDSCT-336 remove
        TestBed.configureTestingModule({
            providers: [
                {provide: HttpClient, useValue: httpClientSpy},
                {provide: AuthService, useValue: authSpy},
                ReportService,
            ]
        });
        reportService = TestBed.get(ReportService);
        authSpy.getAuthToken.and.returnValue(fakeAuthToken);
    });

    it('should get encounter report', () => {
        const expectedReport = buildReport("validEncounterId", "validPatientId");

        httpClientSpy.get.and.returnValue(asyncData(expectedReport));

        reportService.getEncounterReport("validEncounterId")
            .then(res => expect(res).toEqual(expectedReport))
            .catch(fail);

        expect(httpClientSpy.get).toHaveBeenCalledTimes(1);
        const url = httpClientSpy.get.calls.first().args[0];
        expect(url).toContain("report/encounter?encounterId=validEncounterId");
        const headers: HttpHeaders = httpClientSpy.get.calls.first().args[1].headers;
        expect(headers.get('Authorization')).toBe(fakeAuthToken);
    });

    it('should search for reports by patient', () => {
        const expectedReports = [
            buildReport("validEncounterId1", "validPatientId"),
            buildReport("validEncounterId2", "validPatientId")
        ];

        httpClientSpy.get.and.callFake((url: string) => {
            if (url.includes("/report/search?nhsNumber=validNhsNumber")) {
                return asyncData(expectedReports.map(er => er.encounterId));
            }Promise.resolve()
            if (url.includes("report/encounter?encounterId=validEncounterId1")) {
                return asyncData(expectedReports[0]);
            }
            if (url.includes("report/encounter?encounterId=validEncounterId2")) {
                return asyncData(expectedReports[1]);
            }
        });

        reportService.searchByPatient("validNhsNumber")
        .then(res => expect(res).toEqual(expectedReports))
        .catch(fail);

        for (const call of httpClientSpy.get.calls.all()) {
            const headers: HttpHeaders = httpClientSpy.get.calls.first().args[1].headers;
            expect(headers.get('Authorization')).toBe(fakeAuthToken);
        }
    });

    function buildReport(encounterId: string, patientId: string): EncounterReportInput {
        return {
            encounterId: encounterId,
            patientId: patientId,
            encounterStart: "",
            encounterEnd: "",
            observations: []
        };
    }
})