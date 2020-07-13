import { configureSessionProviders } from 'src/app/testing/session-helper';
import { TriageService } from './triage.service';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { asyncData } from '../testing/async-observable-helpers';

describe('Triage Service', () => {

    const fakeAuthToken = "FAKE_AUTH_TOKEN";
    let httpClientSpy: { post: jasmine.Spy};
    let triageService: TriageService;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['post']);
        const authSpy: {getAuthToken: jasmine.Spy} = 
            jasmine.createSpyObj('AuthService', ['getAuthToken']);
        configureSessionProviders(); //TODO: CDSCT-336 remove
        TestBed.configureTestingModule({
            providers: [
                {provide: HttpClient, useValue: httpClientSpy},
                {provide: AuthService, useValue: authSpy},
                TriageService,
            ]
        });
        triageService = TestBed.get(TriageService);
        authSpy.getAuthToken.and.returnValue(fakeAuthToken);
    });

    it('should pass patient to $isValid on the EMS', () => {
        const results = {
            "some.cdss.url": true,
            "another.cdss.url": false
        };

        httpClientSpy.post.and.returnValue(asyncData(results));

        triageService.invokeIsValid("some/patient/url")
            .then(res => expect(res).toEqual(results))
            .catch(fail);

        expect(httpClientSpy.post).toHaveBeenCalledTimes(1);
        const url = httpClientSpy.post.calls.first().args[0];
        expect(url).toContain("/cdss/isValid");
        const body: string = httpClientSpy.post.calls.first().args[1];
        expect(body).toBe("some/patient/url");
        const headers: HttpHeaders = httpClientSpy.post.calls.first().args[2].headers;
        expect(headers.get('Authorization')).toBe(fakeAuthToken);
    });
})