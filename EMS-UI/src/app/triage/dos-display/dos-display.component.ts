import {MatDialogRef, MAT_DIALOG_DATA, MatDialog} from '@angular/material';
import {Component, Input, Inject} from '@angular/core';
import {ReferralRequest} from 'src/app/model/questionnaire';
import {DosService} from 'src/app/service/dos.service';
import {HealthcareService} from 'src/app/model/dos';
import {TriageService} from "../../service/triage.service";
import {ToastrService} from "ngx-toastr";

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
  error: object;

  constructor(
      private dosService: DosService,
      private triageService: TriageService,
      private toastr: ToastrService,
      public dialog: MatDialog) {
  }

  async getDosResponse() {
    this.response = null;
    this.error = null;
    await this.dosService
    .getDosResponse(this.referralRequest)
    .toPromise()
    .then(
        response => {
          this.response = response;
        },
        error => {
          this.error = error;
        }
    );
  }

  viewDetails(selected: HealthcareService) {
    this.dialog.open(HealthcareServiceDialog, {
      data: selected
    });
  }

  async invoke() {
    try {
      const encounterRef = this.referralRequest.contextReference;
      const url = `${this.selectedService.endpoint}?encounter=${encounterRef}`;

      // Service must be updated in referral request before invoking handover
      await this.triageService.updateSelectedService(this.caseId, this.selectedService);

      window.open(url);
    } catch (e) {
      this.toastr.error('Unable to update selected service for case - ' + e.message);
    }
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
