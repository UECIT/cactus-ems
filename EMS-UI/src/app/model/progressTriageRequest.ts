import {Settings, QuestionnaireResponse} from '.';

export class ProgressTriageRequest {
  questionnaireId: string;
  caseId: number;
  cdssSupplierId: number;
  questionResponse: QuestionnaireResponse[];
  serviceDefinitionId: string;
  settings: Settings;
  amendingPrevious: boolean;
  patientId: string;
  carePlanIds: string[];
}

