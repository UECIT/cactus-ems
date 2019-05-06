import { Options } from './questionnaire';
import { Settings } from './settings';

export class ProcessTriage {
  caseId: number;
  cdssSupplierId: number;
  questionResponse: QuestionnaireResponse[];
  serviceDefinitionId: String;
  settings: Settings;
  amendingPrevious: boolean;
}

export class QuestionnaireResponse {
  questionnaireId: string;
  question: string;
  questionId: string;
  response: Options;
  questionType: string;
  responseString: string;
  responseInterger: number;
  responseDecimal: number;
  responceBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentType: string;
}
