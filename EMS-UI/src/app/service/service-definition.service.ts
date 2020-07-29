import { AuthService } from './auth.service';
import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {ToastrService} from 'ngx-toastr';
import {CdssSupplier} from '../model';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({providedIn: 'root'})
export class ServiceDefinitionService {

  constructor(
    private http: HttpClient, 
    private authService: AuthService,
    private toastr: ToastrService) {}

  async getCdssSupplierUrl(cdssId: number) {
    const cdssSupplier = await this.getCdssSupplier(cdssId);
    return cdssSupplier.baseUrl;
  }

  // TODO: remove - this is the same as the method in cdss.service.ts
  async getCdssSupplier(cdssId: number): Promise<CdssSupplier> {
    const token = this.authService.getAuthToken();
    if (token != null) {
      try {
        httpOptions.headers = httpOptions.headers.set('Authorization', token);
        const url = `${environment.EMS_API}/cdss/${cdssId}`;
        return await this.http.get<CdssSupplier>(url, httpOptions).toPromise();
      } catch (err) {
        this.toastr.error(
            err.error.target.__zone_symbol__xhrURL + ' - ' +
            err.message);
      }
    }
  }

  getServiceDefinition(cdssSupplierId: number, serviceDefId: string) {
    const token = this.authService.getAuthToken();
    if (token != null) {
      httpOptions.headers = httpOptions.headers.set('Authorization', token);
    }
    const url = `${environment.EMS_API}/cdss/${cdssSupplierId}/${serviceDefId}`;
    return this.http.get<any>(url, httpOptions);
  }
}
