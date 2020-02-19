import { Code, Practitioner } from '.';

export class Settings {
  userType: Code;
  userLanguage: Code;
  userTaskContext: Code;
  recipientLanguage: Code;
  setting: Code;
  jurisdiction: Code;
  practitioner: Practitioner;
}
