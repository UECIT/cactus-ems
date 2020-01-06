import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { ReferralRequest } from 'src/app/model/questionnaire';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class DosService {
  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {}

  getDosResponse(referralRequest: ReferralRequest) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      httpOptions.headers = httpOptions.headers.set(
        'Content-Type',
        'application/json'
      );
      const url = `${environment.EMS_API}/dos`;
      return this.http.post<Object>(
        url,
        JSON.stringify(referralRequest.resourceId),
        httpOptions
      );
    }
  }
}
