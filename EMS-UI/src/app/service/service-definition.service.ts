import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {Token} from '../model/token';
import {SessionStorage} from 'h5webstorage';
import {ToastrService} from 'ngx-toastr';
import {CdssSupplier} from '../model/cdssSupplier';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class ServiceDefinitionService {
  tokenInfo: Token;

  constructor(private http: HttpClient, private sessionStorage: SessionStorage, private toastr: ToastrService) {
  }

  async getCdssSupplierUrl(cdssId: number) {
    const cdssSupplier = await this.getCdssSupplier(cdssId);
    return cdssSupplier.baseUrl;
  }

  async getCdssSupplier(cdssId: number): Promise<CdssSupplier> {
    if (this.sessionStorage['auth_token'] != null) {
      try {
        httpOptions.headers = httpOptions.headers.set(
            'Authorization',
            this.sessionStorage['auth_token']
        );
        const url = `${environment.EMS_API}/cdss/${cdssId}`;
        return await this.http.get<CdssSupplier>(url, httpOptions).toPromise();
      } catch (err) {
        this.toastr.error(
            err.error.target.__zone_symbol__xhrURL + ' - ' +
            err.message);
      }
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
      triggerId: string) {
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
    // &trigger-eventdata-id={{data_req_id}}
    const url = cdssUrl +
        'status=' + status +
        '&experimental=' + experimental +
        '&effective=ge' + effectiveTo +
        '&effective=le' + effectiveFrom +
        '&useContext-code=' + useContextCode +
        '&useContext-valueconcept=https://www.hl7.org/fhir/party.html|' + useContextValueConcept +
        '&jurisdiction=urn:iso:std:iso:3166|' + jurisdiction +
        '&trigger-eventdata-id=' + triggerId;

    return this.http.get<any>(encodeURI(url), httpOptions);
  }

  getServiceDefinitionByQuery2(
      cdssUrl: string,
      status: string,
      experimental: boolean,
      effectiveTo: string,
      effectiveFrom: string) {
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
    const url = cdssUrl +
        'status=' + status +
        '&experimental=' + experimental +
        '&effective=ge' + effectiveTo +
        '&effective=le' + effectiveFrom;

    return this.http.get<any>(encodeURI(url), httpOptions);
  }
}
