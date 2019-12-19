import { Options, Coordinates, QuestionExtension } from './questionnaire';
import { Settings } from './settings';

export class ProcessTriage {
  questionnaireId: string;
  caseId: number;
  cdssSupplierId: number;
  questionResponse: QuestionnaireResponse[];
  serviceDefinitionId: String;
  settings: Settings;
  amendingPrevious: boolean;
  patientId: number;
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
  responseBoolean: boolean;
  responseDate: string;
  responseAttachment: string;
  responseAttachmentType: string;
  responseCoordinates: Coordinates;
  extension: QuestionExtension;
}
