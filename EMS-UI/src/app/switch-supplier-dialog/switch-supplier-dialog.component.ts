import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {DialogData} from '../triage/case/case.component';
import {CdssService} from '../service/cdss.service';
import {CdssSupplier, ServiceDefinition} from '../model/cdssSupplier';

@Component({
  selector: 'app-switch-supplier-dialog',
  templateUrl: './switch-supplier-dialog.component.html',
  styleUrls: ['./switch-supplier-dialog.component.css']
})
export class SwitchSupplierDialogComponent implements OnInit {
  cdssSuppliers: CdssSupplier[];
  serviceDefinitions: ServiceDefinition[];
  selectedCdssSupplier: CdssSupplier = null;
  selectedServiceDefinition: string;

  constructor(
      public dialogRef: MatDialogRef<SwitchSupplierDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: DialogData,
      private cdssService: CdssService
  ) {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  // get service definitions/suppliers
  async ngOnInit() {
    this.cdssSuppliers = await this.cdssService.getCdssSuppliers().toPromise();
  }

  async updateServiceDefinitions() {
    console.log('Updating Service Definitions for ', this.data.cdssSupplier.name);
    this.serviceDefinitions = await this.cdssService.listServiceDefinitions(this.data.cdssSupplier.id);
  }
}
