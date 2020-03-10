import { Component, OnInit, Inject, Directive} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
import { ResourceService } from '../../service/resource.service';
import { ReportService } from '../../service/report.service';
import { environment } from '../../../environments/environment';


export interface DialogData {
  htmlValidation: String;
  reports: any;
}

@Component({
  selector: 'app-handover-message-dialog',
  templateUrl: './handover-message-dialog.component.html',
  styleUrls: ['./handover-message-dialog.component.css'],
})
export class HandoverMessageDialogComponent implements OnInit {

  constructor(private resourceService: ResourceService, private reportService: ReportService,
     public dialogRef: MatDialogRef<HandoverMessageDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }

    emsUrl = environment.EMS_API;

    oneOneOneValidationReport: any;
    AmbulanceRequestValidationReport: any;

  async ngOnInit() {
  }

}
