import { Injectable } from "@angular/core";
import { SessionStorage } from "h5webstorage";

/**
 * Abstract away retrieving the session storage from the h5 session for testability.
 */
@Injectable({providedIn: 'root'})
export class AuthService {

    constructor(private sessionStorage: SessionStorage) {}

    getAuthToken() {
        return this.sessionStorage['auth_token'];
    }

}