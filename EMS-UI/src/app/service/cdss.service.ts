import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {CdssSupplier, ServiceDefinition} from '../model/cdssSupplier';
import {SessionStorage} from 'h5webstorage';
import {ToastrService} from 'ngx-toastr';
import {Observable} from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class CdssService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage, private toastr: ToastrService) {
  }

  getCdssSuppliers(): Observable<CdssSupplier[]> {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/cdss`;
      return this.http.get<CdssSupplier[]>(url, httpOptions);
    }
  }

  async listServiceDefinitions(cdssId: number): Promise<ServiceDefinition[]> {
    if (this.sessionStorage['auth_token'] != null) {
      try {
        httpOptions.headers = httpOptions.headers.set(
            'Authorization',
            this.sessionStorage['auth_token']
        );
        const url = `${environment.EMS_API}/cdss/${cdssId}/ServiceDefinition?_summary=true`;
        return await this.http.get<ServiceDefinition[]>(url, httpOptions).toPromise();
      } catch (err) {
        this.toastr.error(
            err.error.target.__zone_symbol__xhrURL + ' - ' +
            err.message);
      }
    }
  }

  getCdssSupplier(id: any) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/cdss/${id}`;
      return this.http.get<CdssSupplier>(url, httpOptions).toPromise();
    }
  }

  createCdssSupplier(cdssSupplier: CdssSupplier) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
          'Content-Type',
          'application/json'
      );
      const url = `${environment.EMS_API}/cdss`;
      return this.http.post<CdssSupplier>(
          url,
          JSON.stringify(cdssSupplier),
          httpOptions
      );
    }
  }

  updateCdssSupplier(cdssSupplier: CdssSupplier) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
          'Content-Type',
          'application/json'
      );
      const url = `${environment.EMS_API}/cdss`;
      return this.http.put(url, JSON.stringify(cdssSupplier), httpOptions);
    }
  }

  deleteCdssSupplier(cdssSupplierId: String) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
          'Content-Type',
          'application/json'
      );
      const url = `${environment.EMS_API}/cdss/${cdssSupplierId}`;
      return this.http.delete<CdssSupplier>(url, httpOptions);
    }
  }
}
