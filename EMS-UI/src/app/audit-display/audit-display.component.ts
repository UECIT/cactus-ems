import {
  Component,
  OnInit,
  Input
} from '@angular/core';
import { AuditService } from '../service/audit.service';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-audit-display',
  templateUrl: './audit-display.component.html',
  styleUrls: ['./audit-display.component.css']
})
export class AuditDisplayComponent implements OnInit {
  audit: any;
  _caseId: number;

  constructor(private auditService: AuditService, private toastr: ToastrService) {}

  @Input()
  set caseId(caseId: number) {
    if (caseId) {
      this._caseId = caseId;
      this.updateAudit(this._caseId);
    }
  }

  async updateAudit(caseId: number) {
    this.audit = await this.auditService.getAudit(caseId)
    .catch(err => {
      this.toastr.error(
        err.error.target.__zone_symbol__xhrURL + ' - ' +
        err.message);
    });
  }

  async ngOnInit() {}
}
