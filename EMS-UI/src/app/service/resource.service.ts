import { Injectable } from '@angular/core';
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { SessionStorage } from 'h5webstorage';

const httpOptions = {
  headers: new HttpHeaders()
};

@Injectable({
  providedIn: 'root'
})
export class ResourceService {

  constructor(private http: HttpClient, private sessionStorage: SessionStorage) { }

  getResource(resourceUrl: string) {
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
        'Authorization',
        this.sessionStorage['auth_token']
      );
      const url = `${environment.EMS_API}/resource`;
      return this.http.post<any>(url, resourceUrl, httpOptions).toPromise();
    }
  }
}
