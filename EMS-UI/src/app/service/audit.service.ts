import { Observable, of } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Token } from '../model/token';
import { SessionStorage } from 'h5webstorage';
import { Interaction } from '../model';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class AuditService {
  tokenInfo: Token;
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getAudit(id: any) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/audit/${id}`;
      return this.http.get<any>(url, httpOptions).toPromise();
    }
  }

  getEncounterAudits(): Observable<Interaction[]> {
    let int1: Interaction = {createdDate: "2020-06-23", caseId: 4, requestOrigin: "https://cdss.com/fhir/ServiceDefinition/initial.initial/$evaluate..."};
    return of([int1]);
  }

  getServiceDefinitionSearchAudits(): Observable<Interaction[]> {
    let int1: Interaction = {createdDate: "2020-06-23", requestOrigin: "https://cdss.com/fhir/ServiceDefinition?..."};
    return of([int1]);
  }

  searchAudits(fromDate: any, toDate: any, pageNumber: any, pageSize: any, includeClosed: boolean, includeIncomplete: boolean) {
    const searchParams = {
      from: '2019-03-02T00:00:00.474Z',
      includeClosed: true,
      includeIncomplete: true,
      pageNumber: 0,
      to: '2019-03-08T00:00:00.474Z',
      pageSize: 10,
      sorts: [
        {
          direction: 'ASC',
          sortField: 'DATE_CREATED'
        },
        {
          direction: 'ASC',
          sortField: 'LAST_NAME'
        }
      ]
     };

     searchParams.from = fromDate;
     searchParams.to = toDate;
     searchParams.pageNumber = pageNumber;
     if (pageSize > 10) {
      searchParams.pageSize = pageSize;
     } else {
      searchParams.pageSize = 10;
     }
     searchParams.includeClosed = includeClosed;
     searchParams.includeIncomplete = includeIncomplete;

    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/audit`;
      return this.http.post<any>(url, searchParams, httpOptions).toPromise();
    }
  }
}
