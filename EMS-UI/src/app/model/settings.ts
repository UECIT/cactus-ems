import { Code, Practitioner } from '.';

export class Settings {
  initiatingPerson: Person;
  userType: Code;
  userLanguage: Code;
  userTaskContext: Code;
  receivingPerson: Person;
  recipientType: Code;
  recipientLanguage: Code;
  setting: Code;
  jurisdiction: Code;
  practitioner: Practitioner;
}

export class Person {
  name: string;
  telecom: string;
  gender: string;
  birthDate: string;
}
