import { ToastrService } from 'ngx-toastr';
import { AuthService } from './auth.service';
import { ServiceDefinitionService } from './service-definition.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { asyncData } from '../testing/async-observable-helpers';

describe('Service Definition Service', () => {

    const fakeAuthToken = "FAKE_AUTH_TOKEN";
    let httpClientSpy: { get: jasmine.Spy};
    let toastSpy: {error: jasmine.Spy};
    let serviceDefService: ServiceDefinitionService;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
        toastSpy = jasmine.createSpyObj('ToastrService', ['error']);
        const authSpy: {getAuthToken: jasmine.Spy} = 
            jasmine.createSpyObj('AuthService', ['getAuthToken']);
        TestBed.configureTestingModule({
            providers: [
                {provide: HttpClient, useValue: httpClientSpy},
                {provide: AuthService, useValue: authSpy},
                {provide: ToastrService, useValue: toastSpy},
                ServiceDefinitionService,
            ]
        });
        serviceDefService = TestBed.get(ServiceDefinitionService);
        authSpy.getAuthToken.and.returnValue(fakeAuthToken);
    });

    it('should get service definition', () => {
        const expectedServiceDef = {
            id: "sd",
            name: "Service Def Name",
            description: "It does a thing"
        };

        httpClientSpy.get.and.returnValue(asyncData(expectedServiceDef));

        serviceDefService.getServiceDefinition(5, "sd").toPromise()
            .then(res => {
                expect(res).toEqual(expectedServiceDef);
            })
            .catch(fail);

        expect(httpClientSpy.get).toHaveBeenCalledTimes(1);
        const url = httpClientSpy.get.calls.first().args[0];
        expect(url).toContain("/cdss/5/sd");
        const headers: HttpHeaders = httpClientSpy.get.calls.first().args[1].headers;
        expect(headers.get('Authorization')).toBe(fakeAuthToken);
    });
})