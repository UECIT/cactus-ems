import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Questionnaire } from '../model/questionnaire';
import { LaunchTriage } from '../model/launchTriage';
import { ProcessTriage } from '../model/processTriage';
import { environment } from '../../environments/environment';
import 'rxjs/operators/map';
import {SessionStorage} from 'h5webstorage';
import {SelectService} from "../model/selectService";
import {CdssSupplier, ServiceDefinition} from "../model/cdssSupplier";

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json'
  })
};

@Injectable({
  providedIn: 'root'
})
export class TriageService {
  launchTriage: LaunchTriage = new LaunchTriage();

  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getQuestionnaire(patientId: number): Observable<Questionnaire> {
    this.launchTriage.patientId = patientId;
    this.launchTriage.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
    this.launchTriage.cdssSupplierId = Number.parseInt(
      this.sessionStorage['cdssSupplierId']
    );
    this.launchTriage.settings = JSON.parse(sessionStorage['settings']);
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/case/`;
      return this.http.post<Questionnaire>(
        url,
        JSON.stringify(this.launchTriage),
        httpOptions
      );
    }
  }

  processTriage(triage: ProcessTriage, back: boolean) {
    if (this.sessionStorage['auth_token'] != null) {
      triage.settings = JSON.parse(sessionStorage['settings']);
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      let url = ``;
      let triageItems = this.sessionStorage['triageItems'];
      if (back) {
        url = `${environment.EMS_API}/case/back/`;
        // remove lastItem from memory
        triageItems = triageItems.filter(function(value, index, arr) {
          return value !== triage;
         });
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

  async selectServiceDefinitions(request: SelectService): Promise<CdssSupplier[]>{
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/case/serviceDefinitions`;
      return this.http.post<CdssSupplier[]>(
          url,
          JSON.stringify(request),
          httpOptions
      ).toPromise();
    }
  }
}
