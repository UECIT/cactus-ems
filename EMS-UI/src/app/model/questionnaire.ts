import { Extension } from "./extension";

export class Questionnaire {
  caseId: number;
  cdssSupplierId: number;
  serviceDefinitionId: string;
  triageQuestions: TriageQuestion[];
  result: string;
  switchTrigger: string;
  referralRequest: ReferralRequest;
  careAdvice: any[];
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
  occurrence: string;
  action: string;
  description: string;
  reasonReference: Condition;
  supportingInfo: Condition[];
  relevantHistory: string;
  contextReference: string;
  resourceId: string;
}

export class Condition {
  clinicalStatus: string;
  verificationStatus: string;
  condition: string;
  bodySite: string;
  onset: string;
  stageSummary: string;
  evidence: string[];
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
  system: string; // Code system or fhir type for primitive option types (See CDSCT-64).
  code: string;
  display: string;
  extension: Extension;
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
