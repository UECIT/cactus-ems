import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {environment} from '../../environments/environment';
import {SessionStorage} from 'h5webstorage';

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  constructor(private http: HttpClient, private sessionStorage: SessionStorage) {
  }

  getBackgroundColour(): Promise<string> {
    const httpOptions = {
      headers: new HttpHeaders(),
      responseType: 'text' as 'json'
    };
    if (this.sessionStorage['auth_token'] != null) {
      httpOptions.headers = httpOptions.headers.set(
          'Authorization',
          this.sessionStorage['auth_token']
      );
    }

    const url = `${environment.EMS_API}/environment/colour`;
    return this.http.get<string>(url, httpOptions).toPromise();
  }
}
