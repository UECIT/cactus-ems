import { AnswerService } from './../../service/answer.service';
import { ProcessTriage } from 'src/app/model/processTriage';
import {
  Component,
  OnInit,
  Input
} from '@angular/core';
import {
  QuestionResponse,
  Questionnaire
} from '../../model/questionnaire';
import { Case } from '../../model/case';
import { MatDialog } from '@angular/material';
import { SwitchSupplierDialogComponent } from 'src/app/switch-supplier-dialog/switch-supplier-dialog.component';
import { CdssSupplier } from 'src/app/model/cdssSupplier';
import { Router } from '@angular/router';
import { SessionStorage } from 'h5webstorage';
import { Settings } from 'src/app/model/settings';
import { Subscription } from 'rxjs';

export interface DialogData {
  cdssSupplier: CdssSupplier;
  serviceDefinition: string;
}

@Component({
  selector: 'app-case',
  templateUrl: './case.component.html',
  styleUrls: ['./case.component.css']
})
export class CaseComponent implements OnInit {
  @Input() answerSelected: QuestionResponse[];
  @Input() case: Case;
  @Input() cdssSupplierName: string;
  @Input() questionnaire: Questionnaire;
  @Input() amendingPrevious: boolean;
  @Input() ExternalProcessTriage: (
    switchCdss: boolean,
    back: boolean,
    selectedTriage: ProcessTriage
  ) => boolean;

  caseId: Number;
  role: String;

  cdssSupplier: CdssSupplier;
  serviceDefinition: string;
  answerSubscription: Subscription;
  validToProceed: boolean;

  constructor(
    public dialog: MatDialog,
    public router: Router,
    private sessionStorage: SessionStorage,
    private answerService: AnswerService
  ) 
  {
    this.answerSubscription = this.answerService.answerSelected$.subscribe(
      answer => this.answerSelected = answer
    )
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(SwitchSupplierDialogComponent, {
      width: '520px',
      data: {
        cdssSupplier: this.cdssSupplier,
        serviceDefinition: this.serviceDefinition
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.cdssSupplier = result.cdssSupplier;
      this.serviceDefinition = result.serviceDefinition;
      this.cdssSupplierName = result.cdssSupplier.name;
      this.switchSupplier(result.cdssSupplier.id, result.serviceDefinition);
    });
  }

  ngOnInit() {
    if (this.case) {
      this.caseId = new Number(this.case.id);
    }

    var settings: Settings = this.sessionStorage['settings'];

    if (settings) {
      this.role = settings.userType.description;
    }

  }

  async continue(switchCdss: boolean) {
    const isValid = await this.ExternalProcessTriage(switchCdss, false, null);
    if (isValid) {
      this.caseId = new Number(this.case.id);
    }
  }

  async switchSupplier(cdssSupplierId: string, serviceDefinitionId: string) {
    this.sessionStorage.setItem('cdssSupplierId', cdssSupplierId);
    this.sessionStorage.setItem('serviceDefinitionId', serviceDefinitionId);
    // carry on with triage process
    this.continue(true);
  }

  endTriage() {
    // call audit close endpoint
    // redirect to the homepage
    this.router.navigate(['/main']);
  }

  checkAllRequiredQuestionsAreAnswered(): boolean {
    let requieredComplete = false;
    let requieredQuestions: Boolean = false;

    if (!this.questionnaire || !this.questionnaire.triageQuestions) {
      return false;
    }

    this.questionnaire.triageQuestions.forEach(question => {
      requieredQuestions = question.required;
    });

    if (!requieredQuestions) {
      return true;
    }

    this.questionnaire.triageQuestions.forEach(question => {
      if (question.required && this.answerSelected) {
        for (let index = 0; index < this.answerSelected.length; index++) {
          const answer = this.answerSelected[index];
          if (answer.triageQuestion.questionId === question.questionId) {
            requieredComplete = true;
            break;
          } else {
            requieredComplete = false;
          }
        }
      }
    });
    return requieredComplete;
  }

  ngOnDestroy() {
    this.answerSubscription.unsubscribe();
  }
}
