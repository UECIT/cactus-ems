import { ReportSearchDialogComponent } from './../report-search-dialog/report-search-dialog.component';
import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Patient } from 'src/app/model/patient';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'patient-selection',
  templateUrl: './patient-selection.component.html',
  styleUrls: ['../main.component.css']
})
export class PatientSelectionComponent {

  @Input() patients: Patient[];
  @Input() disabled: boolean;
  @Output() onChange = new EventEmitter<Patient>();

  @Input() selectedPatient: Patient;

  constructor(public router: Router, public dialog: MatDialog) { }

  change(selection: Patient) {
    this.onChange.emit(selection);
    this.selectedPatient = selection;
  }

  openSearch() {
    const dialogRef = this.dialog.open(ReportSearchDialogComponent, {
      height: '95vh',
      width: '95vw',
    });

    dialogRef.afterClosed().toPromise()
      .then(result => {
        this.router.navigate(['/main'], {
          queryParams: {
            encounterId: result
          }
        })
      });
  }

}
