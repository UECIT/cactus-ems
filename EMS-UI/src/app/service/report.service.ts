import {EncounterReportInput} from '../model';
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {SessionStorage} from 'h5webstorage';
import {AuthService} from "./auth.service";

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class ReportService {

  constructor(
      private http: HttpClient,
      private sessionStorage: SessionStorage,
      private authService: AuthService) {
  }

  getEnabled(): Promise<boolean> {
    const token = this.authService.getAuthToken();
    if (token != null) {
      httpOptions.headers = httpOptions.headers.set('Authorization', token);
      const url = `${environment.EMS_API}/report/enabled`;
      return this.http.get<any>(url, httpOptions).toPromise();
    }
  }
  
  getEncounterReport(encounterId: string): Promise<EncounterReportInput> {
    const token = this.authService.getAuthToken();
    if (token != null) {
      httpOptions.headers = httpOptions.headers.set('Authorization', token);

      const url = `${environment.EMS_API}/report/encounter?encounterId=${encounterId}`;
      return this.http.get<EncounterReportInput>(url, httpOptions).toPromise();
    }
  }

  searchByPatient(nhsNumber: string): Promise<string[]> {
    const token = this.authService.getAuthToken();
    if (token != null) {
      httpOptions.headers = httpOptions.headers.set('Authorization', token);

      const encounterSearchUrl = `${environment.EMS_API}/report/search?nhsNumber=${nhsNumber}`;
      return this.http.get<string[]>(encounterSearchUrl, httpOptions).toPromise();
    }
  }

  generateReport(encounterId: string) {
    const token = this.authService.getAuthToken();
    if (token != null) {
      httpOptions.headers = httpOptions.headers.set('Authorization', token);

      const url = `${environment.EMS_API}/report/encounter`;
      return this.http.post<any>(url, encounterId, httpOptions).toPromise();
    }
  }

  validate111ReportV2(oneOneOneReportXml: any) {
    const httpOptions = {
      headers: new HttpHeaders(),
      responseType: 'text' as 'json'
    };
    httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/xml'
    );
    const url = `${environment.UECDI_VALIDATE_API}/validate/111v2`;
    return this.http.post<any>(url, oneOneOneReportXml, httpOptions).toPromise();
  }

  validate111ReportV3(oneOneOneReportXml: any) {
    const httpOptions = {
      headers: new HttpHeaders(),
      responseType: 'text' as 'json'
    };
    httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/xml'
    );
    const url = `${environment.UECDI_VALIDATE_API}/validate/111v3`;
    return this.http.post<any>(url, oneOneOneReportXml, httpOptions).toPromise();
  }

  validateAmbulanceRequestV2(oneOneOneReportXml: any) {
    const httpOptions = {
      headers: new HttpHeaders(),
      responseType: 'text' as 'json'
    };
    httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/xml'
    );
    const url = `${environment.UECDI_VALIDATE_API}/validate/ambulancev2`;
    return this.http.post<any>(url, oneOneOneReportXml, httpOptions).toPromise();
  }

  validateAmbulanceRequestV3(ambulanceRequestXml: any) {
    const httpOptions = {
      headers: new HttpHeaders(),
      responseType: 'text' as 'json'
    };
    httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/xml'
    );
    const url = `${environment.UECDI_VALIDATE_API}/validate/ambulancev3`;
    return this.http.post<any>(url, ambulanceRequestXml, httpOptions).toPromise();

  }
}
