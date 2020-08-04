import { AnswerService } from '../../service';
import {
  Component,
  OnInit,
  Input, OnDestroy
} from '@angular/core';
import {
  Case,
  CdssSupplier,
  QuestionResponse,
  Questionnaire,
  ProgressTriageRequest,
  Settings
} from '../../model';
import { MatDialog } from '@angular/material';
import { SwitchSupplierDialogComponent } from '../../switch-supplier-dialog/switch-supplier-dialog.component';
import { Router } from '@angular/router';
import { SessionStorage } from 'h5webstorage';
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
export class CaseComponent implements OnInit, OnDestroy {

  //TODO: CDSCT-35 Remove this property once all question types using answer service
  @Input() answerSelected: QuestionResponse[];
  @Input() case: Case;
  @Input() cdssSupplierName: string;
  @Input() questionnaire: Questionnaire;
  @Input() amendingPrevious: boolean;
  @Input() ExternalProgressTriage: (
    switchCdss: boolean,
    back: boolean,
    selectedTriage: ProgressTriageRequest
  ) => boolean;

  caseId: Number;
  role: String;

  cdssSupplier: CdssSupplier;
  serviceDefinition: string;
  answerSubscription: Subscription;

  constructor(
    public dialog: MatDialog,
    public router: Router,
    private sessionStorage: SessionStorage,
    private answerService: AnswerService
  ) 
  {
    this.answerSubscription = this.answerService.answerSelected$
      .subscribe(answer => this.answerSelected = answer);
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
      this.caseId = this.case.id;
    }

    var settings: Settings = this.sessionStorage['settings'];

    if (settings) {
      this.role = settings.userType.description;
    }

  }

  async continue(switchCdss: boolean) {
    const isValid = await this.ExternalProgressTriage(switchCdss, false, null);
    if (isValid) {
      this.caseId = this.case.id;
    }
  }

  async switchSupplier(cdssSupplierId: string, serviceDefinitionId: string) {
    this.sessionStorage.setItem('cdssSupplierId', cdssSupplierId);
    this.sessionStorage.setItem('serviceDefinitionId', serviceDefinitionId);
    // carry on with triage process
    await this.continue(true);
  }

  async endTriage() {
    // call audit close endpoint
    // redirect to the homepage
    await this.router.navigate(['/main']);
  }

  checkAllRequiredQuestionsAreAnswered(): boolean {
    let requiredComplete = false;
    let requiredQuestions: Boolean = false;

    if (!this.questionnaire || !this.questionnaire.triageQuestions) {
      return false;
    }

    this.questionnaire.triageQuestions.forEach(question => {
      requiredQuestions = question.required;
    });

    if (!requiredQuestions) {
      return true;
    }

    this.questionnaire.triageQuestions.forEach(question => {
      if (question.required && this.answerSelected) {
        for (let index = 0; index < this.answerSelected.length; index++) {
          const answer = this.answerSelected[index];
          if (answer.triageQuestion.questionId === question.questionId) {
            requiredComplete = true;
            break;
          } else {
            requiredComplete = false;
          }
        }
      }
    });
    return requiredComplete;
  }

  ngOnDestroy() {
    this.answerSubscription.unsubscribe();
  }
}
