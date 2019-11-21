import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { CdssSupplier, ReferencingTypes } from '../model/cdssSupplier';
import { Token } from '../model/token';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class CdssService {
  tokenInfo: Token;

  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getCdssSuppliers() {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/cdss`;
      return this.http.get<CdssSupplier[]>(url, httpOptions);
    }
  }

  getCdssSuppliersUnfiltered() {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/cdss?admin=true`;
      return this.http.get<CdssSupplier[]>(url, httpOptions);
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
      const dto = {
        ...cdssSupplier,
        referencingType: ReferencingTypes.toOrdinal(cdssSupplier.referencingType)
      };
      const url = `${environment.EMS_API}/cdss`;
      return this.http.post<CdssSupplier>(
        url,
        JSON.stringify(dto),
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
