import { Action } from '@ngrx/store';

export const ADD_TOKEN = '[TOKEN] Add';
export const REMOVE_TOKEN = '[TOKEN] Remove';

export class AddAuthToken implements Action {
  readonly type = ADD_TOKEN;

  constructor(public payload: string) {}
}

export class RemoveAuthToken implements Action {
  readonly type = REMOVE_TOKEN;

  constructor(public payload: string) {}
}

export type Actions = AddAuthToken | RemoveAuthToken;
