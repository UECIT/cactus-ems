import { CdssService } from 'src/app/service/cdss.service';
import { ToastrService } from 'ngx-toastr';
import { AuthService } from './auth.service';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { asyncData } from '../testing/async-observable-helpers';

describe('CDSS Service', () => {

    const fakeAuthToken = "FAKE_AUTH_TOKEN";
    let httpClientSpy: { get: jasmine.Spy};
    let toastSpy: {error: jasmine.Spy};
    let cdssService: CdssService;

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
                CdssService,
            ]
        });
        cdssService = TestBed.get(CdssService);
        authSpy.getAuthToken.and.returnValue(fakeAuthToken);
    });

    it('should get image', () => {
        const expectedBlob = new Blob();

        httpClientSpy.get.and.returnValue(asyncData(expectedBlob));

        cdssService.getImage(5, "image.png")
            .then(res => expect(res).toEqual(expectedBlob))
            .catch(fail);

        expect(httpClientSpy.get).toHaveBeenCalledTimes(1);
        const url = httpClientSpy.get.calls.first().args[0];
        expect(url).toContain("/cdss/5/image/image.png");
        const headers: HttpHeaders = httpClientSpy.get.calls.first().args[1].headers;
        expect(headers.get('Authorization')).toBe(fakeAuthToken);
    });
})