import * as LoginActions from '../actions/login.actions';

const initalState: string = null;

export function tokenReducer(
  state: string = initalState,
  action: LoginActions.Actions
) {
  // Section 3
  switch (action.type) {
    case LoginActions.ADD_TOKEN:
      // return {...state, patient: action.payload};
      return action.payload;
    // return Object.assign({}, state, action.payload );
    case LoginActions.REMOVE_TOKEN:
      return null;
    default:
      return initalState;
  }
}
