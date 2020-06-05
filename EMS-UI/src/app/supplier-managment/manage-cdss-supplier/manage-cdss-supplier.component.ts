import { Component, OnInit } from '@angular/core';
import { CdssService } from 'src/app/service/cdss.service';
import { LoginService } from 'src/app/service/login.service';
import { ActivatedRoute } from '@angular/router';
import { CdssSupplier } from 'src/app/model/cdssSupplier';

@Component({
  selector: 'app-manage-cdss-supplier',
  templateUrl: './manage-cdss-supplier.component.html',
  styleUrls: ['./manage-cdss-supplier.component.css']
})
export class ManageCdssSupplierComponent implements OnInit {
  data: any = {};
  suppliers: CdssSupplier[];
  loaded = false;
  error = false;
  errorMessage: string;
  errorObject: any;

  constructor(
    private manageCdssService: CdssService,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.manageCdssService.getCdssSuppliers().subscribe(
      suppliers => {
        this.suppliers = suppliers;
        this.loaded = true;
      },
      error => {
        this.error = true;
        this.loaded = true;
        if (error.status === 401) {
          this.loginService.logout(null, null);
        } else {
          this.errorMessage = 'Error retrieving suppliers';
          this.errorObject = error;
        }
      }
    );
  }
}
