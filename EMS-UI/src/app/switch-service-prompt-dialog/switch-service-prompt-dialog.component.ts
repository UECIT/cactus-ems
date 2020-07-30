import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {DialogData} from '../triage/triage.component';
import {ServiceDefinitionService} from '../service';
import {ToastrService} from 'ngx-toastr';

@Component({
  selector: 'app-switch-service-prompt-dialog',
  templateUrl: './switch-service-prompt-dialog.component.html',
  styleUrls: ['./switch-service-prompt-dialog.component.css']
})
export class SwitchServicePromptDialogComponent implements OnInit {
  oldServiceDefinition: any;
  newServiceDefinition: any;

  constructor(
      public dialogRef: MatDialogRef<SwitchServicePromptDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: DialogData,
      private serviceDefinitionService: ServiceDefinitionService,
      private toastr: ToastrService
  ) {
  }

  async ngOnInit() {
    try {
      this.oldServiceDefinition = await this.serviceDefinitionService
        .getServiceDefinition(this.data.cdssSupplierId, this.data.oldServiceDefinition)
        .toPromise();
      this.newServiceDefinition = await this.serviceDefinitionService
        .getServiceDefinition(this.data.cdssSupplierId, this.data.newServiceDefinition)
        .toPromise();
    } catch (err) {
      this.toastr.error(err.error.target.__zone_symbol__xhrURL + ' - ' + err.message);
    }
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
