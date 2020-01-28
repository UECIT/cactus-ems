export class Questionnaire {
  caseId: number;
  cdssSupplierId: number;
  serviceDefinitionId: string;
  triageQuestions: TriageQuestion[];
  result: string;
  switchTrigger: string;
  referralRequest: ReferralRequest;
  careAdvice: any[];
  procedureRequest: any;
  errorMessage: ErrorMessage;
}

export class ErrorMessage {
  type: String;
  display: String;
  diagnostic: String;
}

export class ReferralRequest {
  status: string;
  priority: string;
  serviceRequestedCode: number;
  serviceRequestedSystem: string;
  serviceRequested: Code[];
  serviceDefinitionId: string;
  occurence: string;
  specialty: string;
  recipient: string;
  description: string;
  supportingInfo: string[];
  note: string;
  relevantHistory: string;
  resourceId: string;
  reasonReference: string;
  contextReference: string;
}

export class Code {
  serviceRequestedSystem: string;
  serviceRequestedCode: number;
  serviceRequestedDisplay: string;
}

export class TriageQuestion {
  questionnaireId: string;
  question: string;
  questionId: string;
  options: Options[];
  repeats: boolean;
  required: boolean;
  questionType: string;
  response: Options;
  responseString: string;
  responseInterger: number;
  responseDecimal: number;
  responseBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentInitial: string;
  responseCoordinates: Coordinates;
  enableWhenAnswer: boolean;
  enableWhenQuestionnaireId: string;
  subQuestions: any;
  extension: QuestionExtension;
}

export class Options {
  code: string;
  display: string;
  extension: Extension;
}

export class Extension { //TODO: remove this version of extension
  url: string;
  value: string;
}

export class QuestionExtension {
  code: string;
  display: string;
  system: string;
}

export class QuestionResponse {
  triageQuestion: TriageQuestion;
  answer: Options;
  responseString: string;
  responseInterger: number;
  responseDecimal: number;
  responseBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentType: string;
}

export class Coordinates {
  x: number;
  y: number;
}
