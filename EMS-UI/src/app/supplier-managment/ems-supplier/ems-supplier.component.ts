import { EmsService } from './../../service/ems.service';
import { EmsSupplier } from './../../model/emsSupplier';
import { Component, OnInit, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-ems-supplier',
  templateUrl: './ems-supplier.component.html',
  styleUrls: ['./ems-supplier.component.css']
})
export class EmsSupplierComponent implements OnInit {

  loaded = false;
  suppliers: EmsSupplier[];
  error: any;

  constructor(private emsService: EmsService, public dialog: MatDialog) { }

  ngOnInit() {
   this.refreshSuppliers();
  }

  refreshSuppliers() {
    this.emsService.getAllEmsSuppliers()
      .subscribe(
        suppliers => {
          this.suppliers = suppliers;
          this.loaded = true;
        },
        error => {
          this.loaded = true;
          this.error = error;
        }
    )
  }

  remove(supplier: EmsSupplier) {
    this.emsService.deleteEms(supplier.id)
      .then(() => this.refreshSuppliers());
  }

  edit(supplier?: EmsSupplier) {
    const dialogRef = this.dialog.open(EditEmsDialog, {
      height: '300px',
      width: '600px',
      data: supplier || new EmsSupplier()
    });

    dialogRef.afterClosed().toPromise()
      .then(result => {
        if (result) {
          this.emsService.createOrUpdateEms(result)
            .then(() => this.refreshSuppliers());
        }
      });
  }

}

@Component({
  selector: 'edit-ems-dialog',
  templateUrl: 'edit-supplier.component.html'
})
export class EditEmsDialog implements OnInit {

  updatedSupplier: EmsSupplier;

  constructor(public dialogRef: MatDialogRef<EditEmsDialog>,
              @Inject(MAT_DIALOG_DATA) public supplier: EmsSupplier) {
      
  }
  ngOnInit() {
    this.updatedSupplier = {
      id: this.supplier.id,
      name:  this.supplier.name,
      baseUrl: this.supplier.baseUrl
    };
  }

  save() {
    this.dialogRef.close(this.updatedSupplier);
  }

  cancel() {
    this.dialogRef.close();
  }
}
