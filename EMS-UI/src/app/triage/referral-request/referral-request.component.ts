import { Component, OnInit, Input, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material';
import { ReferralRequest, Condition } from '../../model/questionnaire';

@Component({
  selector: 'app-referral-request',
  templateUrl: './referral-request.component.html'
})
export class ReferralRequestComponent implements OnInit {
  @Input() referralRequest: ReferralRequest;
  constructor(public dialog: MatDialog) { }

  ngOnInit() {
  }

  viewDetails(condition: Condition) {
    this.dialog.open(ConditionDialog, {
      data: condition
    });
  }

}

@Component({
  selector: 'condition-dialog',
  templateUrl: 'condition-dialog.html'
})
export class ConditionDialog {
  constructor(public dialogRef: MatDialogRef<ConditionDialog>,
    @Inject(MAT_DIALOG_DATA) public condition: Condition) {}

    close(): void {
      this.dialogRef.close();
    }
}
