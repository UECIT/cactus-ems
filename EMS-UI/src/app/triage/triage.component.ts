import { Component, OnInit } from '@angular/core';
import { QuestionnaireService } from '../service/questionnaire.service';
import {
  Questionnaire,
  Options,
  QuestionResponse
} from '../model/questionnaire';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AppState } from '../app.state';
import { Observable } from 'rxjs';
import { Patient } from '../model/patient';
import { ProcessTriage, QuestionnaireResponse } from '../model/processTriage';
import { CaseService } from '../service/case.service';
import { Case } from '../model/case';
import 'rxjs/operators/map';
import 'rxjs/add/operator/finally';
import { MatDialog } from '@angular/material';
import { SwitchServicePromptDialogComponent } from '../switch-service-prompt-dialog/switch-service-prompt-dialog.component';
import { SessionStorage } from 'h5webstorage';

export interface DialogData {
  cdssSupplierId: number;
  oldServiceDefinition: string;
  newServiceDefinition: string;
}

@Component({
  selector: 'app-triage',
  templateUrl: './triage.component.html',
  styleUrls: ['./triage.component.css']
})
export class TriageComponent implements OnInit {
  questionnaire: Questionnaire;
  state: Observable<Patient>;
  patientId: number;
  answerSelected: QuestionResponse[];
  triage: ProcessTriage = new ProcessTriage();
  questionnaireResponse: QuestionnaireResponse = new QuestionnaireResponse();
  case: Case;
  isLoading = true;
  cdssSupplierName: string;
  error = false;
  errorMessage: string;
  errorObject: any;
  ExternalProcessTriage: Function;
  oldServiceDefinition: string;
  newServiceDefinition: string;
  amendingPrevious = false;

  constructor(
    public router: Router,
    private questionnaireService: QuestionnaireService,
    private store: Store<AppState>,
    private caseService: CaseService,
    public dialog: MatDialog,
    private sessionStorage: SessionStorage
  ) {
    this.state = this.store.select('patient');
    this.state.subscribe(res => {
      this.patientId = res.id;
    });
  }

  async ngOnInit() {
    if (this.patientId) {
      await this.getQuestionnaire();
    } else {
      this.router.navigate(['/main']);
    }

    this.ExternalProcessTriage = this.processTriage.bind(this);
  }

  async getQuestionnaire() {
    this.questionnaire = await this.questionnaireService
      .getQuestionnaire(this.patientId)
      .toPromise();
    this.getCaseAndSupplierName(this.questionnaire.caseId);
  }

  async processTriage(switchCdss: boolean, back: boolean, selectedTriage: ProcessTriage) {
    this.isLoading = true;

    if (selectedTriage) {
      this.triage = selectedTriage;
    }

    this.triage.caseId = this.questionnaire.caseId;
    if (switchCdss) {
      this.triage.questionResponse = null;
    } else if (!back) {
      this.triage.questionResponse = new Array();

      // Add blank questions
      this.questionnaire.triageQuestions.forEach(question => {
        this.answerSelected.forEach(answer => {
          if (
            question !== answer.triageQuestion &&
            question.questionType !== 'GROUP'
          ) {
            const questionResponse = new QuestionResponse();
            const answer = new Options();
            answer.code = '260413007';
            answer.display = 'None';
            answer.extension = null;
            questionResponse.answer = answer;
            questionResponse.triageQuestion = question;

            if (
              this.answerSelected.filter(
                e => e.triageQuestion === questionResponse.triageQuestion
              ).length === 0
            ) {
              this.answerSelected.push(questionResponse);
            }
          }
        });
      });

      // loop over each answer and add it to the response. in the below if block.
      this.answerSelected.forEach(element => {
        const questionnaireResponse: QuestionnaireResponse = new QuestionnaireResponse();
        questionnaireResponse.questionnaireId =
          element.triageQuestion.questionnaireId;
        questionnaireResponse.question = element.triageQuestion.question;
        questionnaireResponse.questionId = element.triageQuestion.questionId;
        questionnaireResponse.questionType =
          element.triageQuestion.questionType;
        if (questionnaireResponse.questionType === 'STRING') {
          questionnaireResponse.responseString = element.responseString;
        } else if (questionnaireResponse.questionType === 'INTEGER') {
          questionnaireResponse.responseInterger = element.responseInterger;
        } else if (questionnaireResponse.questionType === 'BOOLEAN') {
          questionnaireResponse.responceBoolean = element.responceBoolean;
        } else if (questionnaireResponse.questionType === 'DECIMAL') {
          questionnaireResponse.responseDecimal = element.responseDecimal;
        } else if (questionnaireResponse.questionType === 'DATE') {
          questionnaireResponse.responseDate = element.responseDate;
        } else if (questionnaireResponse.questionType === 'ATTACHMENT') {
          questionnaireResponse.responseAttachment = element.responseAttachment;
          questionnaireResponse.responseAttachmentType =
            element.responseAttachmentType;
        } else {
          questionnaireResponse.response = element.answer;
        }
        this.triage.questionResponse.push(questionnaireResponse);
      });
    }

    this.triage.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
    this.triage.cdssSupplierId = Number.parseInt(
      this.sessionStorage['cdssSupplierId']
    );
    this.triage.amendingPrevious = this.amendingPrevious;
    this.questionnaire = await this.questionnaireService.processTriage(
      this.triage,
      back
    );

    if (this.questionnaire.switchTrigger != null) {
      this.oldServiceDefinition = this.sessionStorage['serviceDefinitionId'];
      this.sessionStorage.setItem(
        'serviceDefinitionId',
        this.questionnaire.switchTrigger.split('/').pop()
      );
      this.newServiceDefinition = this.sessionStorage['serviceDefinitionId'];
      this.triage.questionResponse = null;
      this.triage.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
      this.triage.cdssSupplierId = Number.parseInt(
        this.sessionStorage['cdssSupplierId']);
      this.questionnaire = await this.questionnaireService.processTriage(
        this.triage,
        back
      );
      this.openDialog();
    }

    // reset the value.
    this.answerSelected = new Array();
    this.amendingPrevious = false;
    this.isLoading = false;

    // if we are amending an answer then make sure the old answer is visible
    if (this.questionnaire.triageQuestions != null) {
      for (let i = 0; i < this.questionnaire.triageQuestions.length; i++) {
        if (
          this.questionnaire.triageQuestions[i].response != null ||
          this.questionnaire.triageQuestions[i].responseString != null
        ) {
          const previousAnswer = new QuestionResponse();

          if (this.questionnaire.triageQuestions[i].response != null) {
            previousAnswer.answer = this.questionnaire.triageQuestions[
              i
            ].response;
          } else if (
            this.questionnaire.triageQuestions[i].responseString != null
          ) {
            const answer = new Options();
            answer.display = this.questionnaire.triageQuestions[
              i
            ].responseString;
            previousAnswer.answer = answer;
          }
          previousAnswer.triageQuestion = this.questionnaire.triageQuestions[i];

          this.answerSelected.push(previousAnswer);

          this.amendingPrevious = true;
        }
      }
    } else {
      this.amendingPrevious = true;
    }
    return true;
  }

  // Get the case that was create when we started the triage process.
  async getCaseAndSupplierName(caseId: number) {
    this.case = await this.caseService.getCase(caseId).toPromise();
    this.isLoading = false;
    this.cdssSupplierName = this.sessionStorage['cdssSupplierName'];
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(SwitchServicePromptDialogComponent, {
      width: '520px',
      data: {
        cdssSupplierId: this.questionnaire.cdssSupplierId,
        oldServiceDefinition: this.oldServiceDefinition,
        newServiceDefinition: this.newServiceDefinition
      }
    });

    dialogRef.afterClosed().subscribe(result => {});
  }
}
