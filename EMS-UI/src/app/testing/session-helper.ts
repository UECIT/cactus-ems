import { SessionStorage, SESSION_STORAGE_OBJECT, SERDES_OBJECT, STORAGE_OPTIONS } from "h5webstorage"
import { TestBed } from "@angular/core/testing"

export function configureSessionProviders() {
    TestBed.configureTestingModule({
        providers: [
            {provide: SESSION_STORAGE_OBJECT, useValue: sessionStorage},
            {provide: SERDES_OBJECT, useValue: {stringify: JSON.stringify, parse: JSON.parse}}, //Raise a tech debt: move it to a common before step
            {provide: STORAGE_OPTIONS, useValue: {}},
            SessionStorage
        ]
    });
}