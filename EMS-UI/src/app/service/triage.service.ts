import { AuthService } from './auth.service';
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {
  Questionnaire,
  LaunchTriageRequest,
  ProgressTriageRequest,
  SelectService,
  CdssSupplier,
  HealthcareService,
  Case } from '../model';
import {environment} from '../../environments/environment';
import 'rxjs/operators/map';
import {SessionStorage} from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class TriageService {
  launchTriageRequest = new LaunchTriageRequest();

  constructor(
    private http: HttpClient, 
    private sessionStorage: SessionStorage,
    private authService: AuthService) {
  }

  launchTriage(patientId: string): Observable<number> {
    this.launchTriageRequest.patientId = patientId;
    this.launchTriageRequest.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
    this.launchTriageRequest.cdssSupplierId = Number.parseInt(
        this.sessionStorage['cdssSupplierId']
    );
    this.launchTriageRequest.settings = JSON.parse(sessionStorage['settings']);
    let encounterHandover = this.sessionStorage['encounterHandover'];
    this.launchTriageRequest.encounterId = encounterHandover ? encounterHandover.encounterId : null;

    let authToken = this.authService.getAuthToken();
    if (authToken) {
      httpOptions.headers = httpOptions.headers.set('Authorization', authToken);
      const url = `${environment.EMS_API}/case/`;
      return this.http.post<number>(
          url,
          JSON.stringify(this.launchTriageRequest),
          httpOptions
      );
    }
  }

  progressTriage(triage: ProgressTriageRequest, back: boolean) {
    let authToken = this.authService.getAuthToken();
    if (authToken) {
      triage.settings = JSON.parse(sessionStorage['settings']);
      httpOptions.headers = httpOptions.headers.set('Authorization', authToken);
      let url: string;
      let triageItems = this.sessionStorage['triageItems'];
      if (back) {
        url = `${environment.EMS_API}/case/back/`;
        // remove lastItem from memory
        triageItems = triageItems.filter(value => value !== triage);
      } else {
        url = `${environment.EMS_API}/case/`;
        // store latest triage in memory
        triageItems.push(triage);
      }
      this.sessionStorage.setItem('triageItems', JSON.stringify(triageItems));
      return this.http
      .put<Questionnaire>(url, JSON.stringify(triage), httpOptions)
      .toPromise();
    }
  }

  async updateSelectedService(caseId: number, selectedService: HealthcareService) {
    let authToken = this.authService.getAuthToken();
    if (authToken) {
      httpOptions.headers = httpOptions.headers.set('Authorization', authToken);
      let url = `${environment.EMS_API}/case/selectedService`;
      let request = {
        caseId,
        selectedServiceId: selectedService.id,
        serviceTypes: selectedService.types
      };
      await this.http
      .put<Case>(url, JSON.stringify(request), httpOptions)
      .toPromise();
    }
  }

  async selectServiceDefinitions(request: SelectService): Promise<CdssSupplier[]> {
    let authToken = this.authService.getAuthToken();
    if (authToken) {
      httpOptions.headers = httpOptions.headers.set('Authorization', authToken);
      const url = `${environment.EMS_API}/case/serviceDefinitions`;
      return this.http.post<CdssSupplier[]>(
          url,
          JSON.stringify(request),
          httpOptions
      ).toPromise();
    }
  }

  invokeIsValid(patientId: string) {
    let authToken = this.authService.getAuthToken();
    if (authToken) {
      httpOptions.headers = httpOptions.headers.set('Authorization', authToken);
      const url = `${environment.EMS_API}/cdss/isValid`;
      return this.http.post(url, patientId, httpOptions).toPromise();
    }
  }
}
