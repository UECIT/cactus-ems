import { Environment } from './../model/environment';
import { EnvironmentService } from './environment.service';
import { asyncData } from '../testing/async-observable-helpers';

describe('Environment Service', () => {

    let httpClientSpy: { get: jasmine.Spy};
    let envService: EnvironmentService;

    beforeEach(() => {
        httpClientSpy = jasmine.createSpyObj('HttpClient', ['get']);
        envService = new EnvironmentService(<any> httpClientSpy)
    })

    it('expects the service to fetch environment properties', () => {
        const expectedProps: Environment = {
            name: "envName",
            apiVersion: "1.2.3",
            appVersion: "3.2.1"
        };
        httpClientSpy.get.and.returnValue(asyncData(expectedProps));

        envService.getVariables()
        .then(env => {
            expect(env).toEqual(expectedProps, 'expected properties');
        })
        .catch(fail);
        expect(httpClientSpy.get.calls.count()).toBe(1, 'one call');
    })
})