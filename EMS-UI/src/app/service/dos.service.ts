import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient, HttpParams } from '@angular/common/http';
import { ReferralRequest } from 'src/app/model/questionnaire';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';
import { HealthcareService } from '../model/dos';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class DosService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getDosResponse(referralRequest: ReferralRequest, patientId: string) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      let params = new HttpParams()
      .set("referralRequestId", referralRequest.resourceId)
      .set("patientId", patientId);
      const url = `${environment.EMS_API}/dos?${params.toString()}`;
      return this.http.get<HealthcareService[]>(
        url,
        httpOptions
      );
    }
  }
}
