import { Action } from '@ngrx/store';
import { Patient } from '../model/patient';

export const ADD_PATIENT = '[PATIENT] Add';
export const REMOVE_PATIENT = '[PATIENT] Remove';

export class AddPatient implements Action {
  readonly type = ADD_PATIENT;

  constructor(public payload: Patient) {}
}

export class RemovePatient implements Action {
  readonly type = REMOVE_PATIENT;

  constructor(public payload: number) {}
}

export type Actions = AddPatient | RemovePatient;
