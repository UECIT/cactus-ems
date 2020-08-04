import {Settings} from '.';

export class LaunchTriageRequest {
  patientId: string;
  cdssSupplierId?: number;
  serviceDefinitionId?: string;
  settings: Settings;
  encounterId?: string;
}

