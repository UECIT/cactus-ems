import { Patient } from './model/patient';

export interface AppState {
    patient: Patient;
    authToken: string;
}
