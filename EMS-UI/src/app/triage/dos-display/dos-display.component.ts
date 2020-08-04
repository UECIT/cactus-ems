import {MatDialogRef, MAT_DIALOG_DATA, MatDialog} from '@angular/material';
import {Component, Input, Inject} from '@angular/core';
import {ReferralRequest} from 'src/app/model/questionnaire';
import {DosService} from 'src/app/service/dos.service';
import {HealthcareService} from 'src/app/model/dos';
import {TriageService} from "../../service/triage.service";
import {ReportService} from "../../service/report.service";
import {ToastrService} from "ngx-toastr";
import {AppState} from 'src/app/app.state';
import {Store} from '@ngrx/store';
import {HandoverMessageDialogComponent} from '../handover-message-dialog/handover-message-dialog.component';

@Component({
  selector: 'app-dos-display',
  templateUrl: './dos-display.component.html',
  styleUrls: ['./dos-display.component.css']
})
export class DosDisplayComponent {
  @Input() referralRequest: ReferralRequest;
  @Input() caseId: number;

  response: HealthcareService[];
  selectedService: HealthcareService;
  patientId: string;
  error: object;
  isReportEnabled: boolean;

  constructor(
      private dosService: DosService,
      private triageService: TriageService,
      private reportService: ReportService,
      private toastr: ToastrService,
      public dialog: MatDialog,
      store: Store<AppState>) {
    store.select('patient').subscribe(({ id }) => this.patientId = id);
  }

  async getDosResponse() {
    this.response = null;
    this.error = null;
    await this.dosService
    .getDosResponse(this.referralRequest, this.patientId)
    .toPromise()
      .then(
        response => this.response = response,
        error => this.error = error
      );
    this.isReportEnabled = await this.reportService.getEnabled();
  }

  viewDetails(selected: HealthcareService) {
    this.dialog.open(HealthcareServiceDialog, {
      data: selected
    });
  }

  invoke() {
    const encounterRef = this.referralRequest.contextReference;
    const url = `${this.selectedService.endpoint}?encounterId=${encounterRef}`;

    // Service must be updated in referral request before invoking handover
    this.selectService()
      .then(() => window.open(url))
      .catch(e => this.toastr.error('Unable to update selected service for case - ' + e.message));
  }

  async generateReport() {
    // Service must be updated in referral request before generating the report
    await this.selectService();

    await this.reportService.generateReport(this.referralRequest.contextReference)
        .then(result => {
          this.openDialog(result);
        })
        .catch(err => {
          this.toastr.error('Unable to generate reports - ' + err);
        });
  }

  private async selectService() {
    return await this.triageService.updateSelectedService(this.caseId, this.selectedService);
  }

  openDialog(reports) {
    this.dialog.open(HandoverMessageDialogComponent, {
      height: '95vh',
      width: '95vw',
      panelClass: 'report',
      backdropClass: 'report-backdrop',
      data: {
        reports: reports
      }
    });
  }

  get reportEnabled() {
    return this.isReportEnabled;
  }
}

@Component({
  selector: 'healthcare-service-dialog',
  templateUrl: 'healthcare-service-dialog.html'
})
export class HealthcareServiceDialog {
  constructor(public dialogRef: MatDialogRef<HealthcareServiceDialog>,
              @Inject(MAT_DIALOG_DATA) public service: HealthcareService) {
  }

  close(): void {
    this.dialogRef.close();
  }
}
