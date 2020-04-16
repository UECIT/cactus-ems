import { AnswerService } from './../service/answer.service';
import { ErrorMessage } from './../model/questionnaire';
import { Component, OnInit } from '@angular/core';
import { TriageService } from '../service/triage.service';
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
import { ToastrService } from 'ngx-toastr';

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
  patientId: string;
  answerSelected: QuestionResponse[];
  triage = new ProcessTriage();
  questionnaireResponse = new QuestionnaireResponse();
  case: Case;
  isLoading = true;
  cdssSupplierName: string;
  errorMessage: ErrorMessage;
  ExternalProcessTriage: Function;
  oldServiceDefinition: string;
  newServiceDefinition: string;
  amendingPrevious = false;

  constructor(
    public router: Router,
    private triageService: TriageService,
    private caseService: CaseService,
    public dialog: MatDialog,
    private sessionStorage: SessionStorage,
    private toastr: ToastrService,
    store: Store<AppState>,
  ) {
    store.select('patient').subscribe(({ id }) => this.patientId = id);
  }

  async ngOnInit() {
    if (this.patientId) {
      await this.getQuestionnaire().catch(err => {
        this.toastr.error(
          err.error.target.__zone_symbol__xhrURL + ' - ' +
          err.message);
      });
    } else {
      this.router.navigate(['/main']);
    }

    this.ExternalProcessTriage = this.processTriage.bind(this);
  }

  async getQuestionnaire() {
    this.questionnaire = await this.triageService
      .getQuestionnaire(this.patientId)
      .toPromise();
    this.getCaseAndSupplierName(this.questionnaire.caseId);
    if (this.questionnaire.switchTrigger != null) {
      this.triage.caseId = this.questionnaire.caseId;
      this.redirect(false);
    }
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
      this.triage.questionResponse = [];

      // Add blank questions
      if (!this.answerSelected) {
        this.answerSelected = [];
      }
      this.questionnaire.triageQuestions.forEach(question => {
        this.triage.questionnaireId = question.questionnaireId;
        this.answerSelected.forEach(answer => {
          if (
            question !== answer.triageQuestion &&
            question.questionType !== 'GROUP'
          ) {
            const questionResponse = new QuestionResponse();
            const qrAnswer = new Options();
            qrAnswer.code = '260413007';
            qrAnswer.display = 'None';
            qrAnswer.extension = null;
            questionResponse.answer = qrAnswer;
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
        questionnaireResponse.extension = element.triageQuestion.extension;
        if (questionnaireResponse.questionType === 'STRING' || questionnaireResponse.questionType === 'TEXT') {
          questionnaireResponse.responseString = element.responseString;
        } else if (questionnaireResponse.questionType === 'INTEGER') {
          questionnaireResponse.responseInterger = element.responseInterger;
        } else if (questionnaireResponse.questionType === 'BOOLEAN') {
          questionnaireResponse.responseBoolean = element.responseBoolean;
        } else if (questionnaireResponse.questionType === 'DECIMAL') {
          questionnaireResponse.responseDecimal = element.responseDecimal;
        } else if (questionnaireResponse.questionType === 'DATE') {
          questionnaireResponse.responseDate = element.responseDate;
        } else if (questionnaireResponse.questionType === 'ATTACHMENT') {
          questionnaireResponse.responseAttachment = element.responseAttachment;
          questionnaireResponse.responseAttachmentType =
            element.responseAttachmentType;
        } else if (questionnaireResponse.questionType === 'REFERENCE' && element.triageQuestion.extension.code == 'imagemap') {
            questionnaireResponse.responseCoordinates = element.triageQuestion.responseCoordinates;
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
    this.triage.patientId = this.patientId;
    this.questionnaire = await this.triageService.processTriage(this.triage, back)
      .then(res => {
        var quesionnaire = res as Questionnaire;
        this.errorMessage = quesionnaire.errorMessage;
        return quesionnaire;
      })
      .catch(error => {
        this.errorMessage = {
          display: error.error.message,
          type: "error",
          diagnostic: error.error.errors[0]
        }
        return new Questionnaire();
      });

    if (this.errorMessage) {
      return;
    }

    if (this.questionnaire.switchTrigger != null) {
      this.redirect(back);
    }

    // reset the value.
    this.answerSelected = [];
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

  async redirect(back) {
      this.oldServiceDefinition = this.sessionStorage['serviceDefinitionId'];
      this.sessionStorage.setItem(
        'serviceDefinitionId',
        this.questionnaire.switchTrigger.split('/').pop()
      );
      this.newServiceDefinition = this.sessionStorage['serviceDefinitionId'];
      this.triage.questionResponse = null;
      this.triage.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
      this.triage.cdssSupplierId = Number.parseInt(
          this.questionnaire.switchTrigger.split('/').shift());
      this.questionnaire = await this.triageService.processTriage(
        this.triage,
        back
      );
      this.openDialog();
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
