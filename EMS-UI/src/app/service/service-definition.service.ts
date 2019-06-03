import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { Token } from '../model/token';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class ServiceDefinitionService {
  tokenInfo: Token;

  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  async getCdssSupplierUrl(cdssId: Number) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/cdss/${cdssId}`;
      const CdssUrl = await this.http.get<any>(url, httpOptions).toPromise();
      return CdssUrl.baseUrl;
    }
  }

  getServiceDefinition(cdssUrl: string) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
    }
    return this.http.get<any>(cdssUrl, httpOptions);
  }

  getServiceDefinitionByQuery(
    cdssUrl: string,
    status: string,
    experimental: boolean,
    effectiveTo: string,
    effectiveFrom: string,
    useContextCode: string,
    useContextValueConcept: string,
    jurisdiction: string,
    triggerType: string,
    triggerEventDataType: string,
    triggerEventDataProfile: string,
    triggerEventDataValueCoding: string) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
    }

    // {{BASE_URL}}
    // status=active
    // &experimental=false
    // &effective=ge{{TODAY}}
    // &effective=le{{TODAY}}
    // &useContext-code=gender  NEW
    // &useContext-valueconcept=http://hl7.org/fhir/administrative-gender|female NEW
    // &jurisdiction=urn:iso:std:iso:3166|ENG
    // &trigger-type=data-added
    // &trigger-eventdata-type=Observation
    // &trigger-eventdata-profile=[profile name]
    // &trigger-eventdata-valuecoding=[code]

    const url = cdssUrl +
    'status=' + status +
    '&experimental=' + experimental +
    '&effective=ge' + effectiveTo +
    '&effective=le' + effectiveFrom +
    '&useContext-code=' + useContextCode +
    '&useContext-valueconcept=https://www.hl7.org/fhir/party.html|' + useContextValueConcept +
    '&jurisdiction=urn:iso:std:iso:3166|' + jurisdiction +
    '&trigger-type=' + triggerType +
    '&trigger-eventdata-type=' + triggerEventDataType +
    '&trigger-eventdata-profile=' + triggerEventDataProfile +
    '&trigger-eventdata-valuecoding=' + triggerEventDataValueCoding;

    return this.http.get<any>(encodeURI(url), httpOptions);
  }
}
