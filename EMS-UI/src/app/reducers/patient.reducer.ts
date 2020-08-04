import { Patient } from '../model/patient';
import * as PatientActions from '../actions/patient.actions';

const initalState = new Patient();

export function patientReducer(
  state: Patient = initalState,
  action: PatientActions.Actions
) {
  switch (action.type) {
    case PatientActions.ADD_PATIENT:
      return Object.assign({}, state, action.payload);
    default:
      return initalState;
  }
}
