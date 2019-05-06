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
    useContextParty: string,
    useContextSkillset: string,
    jurisdiction: string,
    trigger: string) {
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
    // &useContext:=https://www.hl7.org/fhir/party.html|3
    // &useContext:=https://www.hl7.org/fhir/skillset.html|111CH
    // &jurisdiction=urn:iso:std:iso:3166|ENG
    // &trigger=https://www.hl7.org/fhir/triggerdefinition.html|240091000000105

    const url = cdssUrl +
    'status=' + status +
    '&experimental=' + experimental +
    '&effective=ge' + effectiveTo +
    '&effective=le' + effectiveFrom +
    '&useContext:=https://www.hl7.org/fhir/party.html|' + useContextParty +
    '&useContext:=https://www.hl7.org/fhir/skillset.html|' + useContextSkillset +
    '&jurisdiction=urn:iso:std:iso:3166|' + jurisdiction +
    '&trigger=https://www.hl7.org/fhir/triggerdefinition.html|' + trigger;

    return this.http.get<any>(encodeURI(url), httpOptions);
  }
}
