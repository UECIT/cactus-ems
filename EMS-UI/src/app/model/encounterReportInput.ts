export class EncounterReportInput {
  encounterId: string;
  encounterStart: string;
  encounterEnd: string;
  observations: string[];
  patientId: string;
  patientName?: string;
  patientAddress?: string;
}