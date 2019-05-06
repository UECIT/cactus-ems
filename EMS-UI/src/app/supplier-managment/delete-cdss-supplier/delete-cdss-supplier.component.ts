import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from 'src/app/service/login.service';
import { CdssService } from 'src/app/service/cdss.service';
import { CdssSupplier } from 'src/app/model/cdssSupplier';

@Component({
  selector: 'app-delete-cdss-supplier',
  templateUrl: './delete-cdss-supplier.component.html',
  styleUrls: ['./delete-cdss-supplier.component.css']
})
export class DeleteCdssSupplierComponent implements OnInit, OnDestroy {
  title: String = 'Delete Supplier';
  sub: any;
  supplierId: string;
  loaded: boolean;
  supplier: CdssSupplier;
  warning: boolean;
  warningMessage: string;
  error: boolean;
  errorMessage: string;
  errorObject: any;

  constructor(
    private cdssService: CdssService,
    private route: ActivatedRoute,
    private router: Router,
    private loginService: LoginService
  ) {}

  ngOnInit() {
    this.sub = this.route.queryParams.subscribe(params => {
      this.supplierId = params['supplierId'];
    });
    this.getSupplier(this.supplierId);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

  async getSupplier(supplierId: string) {
    this.supplier = this.supplier = await this.cdssService.getCdssSupplier(
      supplierId
    );
    this.loaded = true;
  }

  deleteSupplier() {
    this.cdssService.deleteCdssSupplier(this.supplierId).subscribe(
      resp => {
        this.router.navigate(['/suppliers']);
      },
      error => {
        this.error = true;
        if (error.status === 401) {
          this.loginService.logout(null);
        } else {
          this.errorMessage = 'Error deleting supplier.';
          this.errorObject = error;
        }
      }
    );
  }
}
