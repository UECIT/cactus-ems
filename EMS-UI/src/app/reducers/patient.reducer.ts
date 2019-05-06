import { Patient } from '../model/patient';
import * as PatientActions from '../actions/patient.actions';

const initalState: Patient = {
  address1: null,
  address2: null,
  address3: null,
  dateOfBirth: null,
  deceasedDateTime: null,
  firstName: null,
  gender: null,
  id: null,
  lastName: null,
  nhsNumber: null,
  phone: null,
  postcode: null,
  title: null,
  testCaseSummary: null
};

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
