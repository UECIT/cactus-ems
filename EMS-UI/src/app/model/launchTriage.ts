import { Settings } from './settings';

export class LaunchTriage {
  patientId: string;
  cdssSupplierId?: number;
  serviceDefinitionId?: string;
  settings: Settings;
  encounterId?: String;
}

export class EncounterReportInput {
  encounterId: string;
  encounterStart: string;
  encounterEnd: string;
  observations: string[];
  patientId: string;
  patientName?: string;
  patientAddress?: string;
}
