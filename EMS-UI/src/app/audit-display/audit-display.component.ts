import {
  Component,
  OnInit,
  Input
} from '@angular/core';
import { AuditService } from '../service/audit.service';

@Component({
  selector: 'app-audit-display',
  templateUrl: './audit-display.component.html',
  styleUrls: ['./audit-display.component.css']
})
export class AuditDisplayComponent implements OnInit {
  audit: any;
  auditString: string;
  _caseId: number;

  constructor(private auditService: AuditService) {}

  @Input()
  set caseId(caseId: number) {
    if (caseId) {
      this._caseId = caseId;
      this.updateAudit(this._caseId);
    }
  }

  async updateAudit(caseId: number) {
    this.audit = await this.auditService.getAudit(caseId);
    this.auditString = JSON.stringify(this.audit);
  }

  async ngOnInit() {}
}
