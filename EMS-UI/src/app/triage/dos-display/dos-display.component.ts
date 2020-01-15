import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { Component, Input, Inject } from '@angular/core';
import { ReferralRequest } from 'src/app/model/questionnaire';
import { DosService } from 'src/app/service/dos.service';
import { HealthcareService } from 'src/app/model/dos';

@Component({
  selector: 'app-dos-display',
  templateUrl: './dos-display.component.html',
  styleUrls: ['./dos-display.component.css']
})
export class DosDisplayComponent {
  @Input() referralRequest: ReferralRequest;

  response: HealthcareService[];
  selectedService: HealthcareService;
  error: object;
  constructor(private dosService: DosService, public dialog: MatDialog) {
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
    console.log(selected);
    this.dialog.open(HealthcareServiceDialog, {
      data: selected
    });
  }

  invoke() {
    console.log(this.selectedService.endpoint);
  }
}

@Component({
  selector: 'healthcare-service-dialog',
  templateUrl: 'healthcare-service-dialog.html'
})
export class HealthcareServiceDialog {
  constructor(public dialogRef: MatDialogRef<HealthcareServiceDialog>,
    @Inject(MAT_DIALOG_DATA) public service: HealthcareService) {}

    close(): void {
      this.dialogRef.close();
    }
}
