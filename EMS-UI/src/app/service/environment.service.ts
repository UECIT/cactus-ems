import { Environment } from './../model/environment';
import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {

  constructor(private http: HttpClient) {}

  getVariables(): Promise<Environment> {
    const url = `${environment.EMS_API}/environment/properties`;
    return this.http.get<Environment>(url).toPromise();
  }
}
