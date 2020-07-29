import { EncounterReportInput } from '../../model';
import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { ReportService } from 'src/app/service';


@Component({
  selector: 'app-report-search-dialog',
  templateUrl: './report-search-dialog.component.html'
})
export class ReportSearchDialogComponent {

  nhsNumber: string;
  reportsFound: EncounterReportInput[];

  constructor(
    public dialogRef: MatDialogRef<ReportSearchDialogComponent>,
    private reportService: ReportService 
  ) {}

  cancel() {
    this.dialogRef.close();
  }

  search() {
    this.reportService.searchByPatient(this.nhsNumber)
      .then(result => this.reportsFound = result);
  }

  handover(report: EncounterReportInput) {
    this.dialogRef.close(report.encounterId);
  }
}
