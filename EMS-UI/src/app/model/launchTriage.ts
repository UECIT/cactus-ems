import { Settings } from './settings';

export class LaunchTriage {
  patientId: number;
  cdssSupplierId?: number;
  serviceDefinitionId?: string;
  settings: Settings;
}
