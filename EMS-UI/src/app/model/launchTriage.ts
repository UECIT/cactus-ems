import { Settings } from './settings';

export class LaunchTriage {
  patientId: number;
  cdssSupplierId?: number;
  serviceDefinitionId?: string;
  settings: Settings;
  encounterId?: String;
}

export class EncounterReportInput {
  encounterId: string;
  observations: string[];
  patientId: string;
  patientName?: string;
  patientAddress?: string;
}
