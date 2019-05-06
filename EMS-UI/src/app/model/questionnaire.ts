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
  responceBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentInitial: string;
  enableWhenAnswer: boolean;
  enableWhenQuestionnaireId: string;
  subQuestions: any;
}

export class Options {
  code: string;
  display: string;
  extension: Extension;
}

export class Extension {
  url: string;
  value: string;
}

export class QuestionResponse {
  triageQuestion: TriageQuestion;
  answer: Options;
  responseString: string;
  responseInterger: number;
  responseDecimal: number;
  responceBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentType: string;
}
