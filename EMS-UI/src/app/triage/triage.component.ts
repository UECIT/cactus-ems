import { AnswerService, CaseService, TriageService } from '../service';
import {Component, OnDestroy, OnInit} from '@angular/core';
import {
  ErrorMessage,
  Case,
  ProgressTriageRequest,
  QuestionnaireResponse,
  Questionnaire,
  Options,
  QuestionResponse
} from '../model';
import { Store } from '@ngrx/store';
import { Router } from '@angular/router';
import { AppState } from '../app.state';
import { Subscription } from 'rxjs';
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
export class TriageComponent implements OnInit, OnDestroy {
  questionnaire = new Questionnaire();
  patientId: string;
  answerSelected: QuestionResponse[];
  triage = new ProgressTriageRequest();
  questionnaireResponse = new QuestionnaireResponse();
  case: Case;
  isLoading = true;
  cdssSupplierName: string;
  errorMessage: ErrorMessage;
  ExternalProgressTriage: Function;
  oldServiceDefinition: string;
  newServiceDefinition: string;
  amendingPrevious = false;
  answerSubscription: Subscription;

  constructor(
    public router: Router,
    private triageService: TriageService,
    private caseService: CaseService,
    public dialog: MatDialog,
    private sessionStorage: SessionStorage,
    private toastr: ToastrService,
    store: Store<AppState>,
    answerService: AnswerService
  ) {
    store.select('patient').subscribe(({ id }) => this.patientId = id);
    this.answerSubscription = answerService.answerSelected$.subscribe(qr => this.answerSelected = qr);
  }

  async ngOnInit() {
    if (this.patientId) {
      await this.launchTriage().catch(err => {
        this.toastr.error(
          err.error.target.__zone_symbol__xhrURL + ' - ' +
          err.message);
      });
    } else {
      await this.router.navigate(['/main']);
    }

    this.ExternalProgressTriage = this.progressTriage.bind(this);
  }

  async launchTriage() {
    this.questionnaire.caseId = await this.triageService
      .launchTriage(this.patientId)
      .toPromise();

    await this.getCaseAndSupplierName(this.questionnaire.caseId);
    await this.progressTriage(false, false, null);
    if (this.questionnaire.switchTrigger != null) {
      this.triage.caseId = this.questionnaire.caseId;
      await this.redirect(false);
    }
  }

  async progressTriage(switchCdss: boolean, back: boolean, selectedTriage: ProgressTriageRequest) {
    this.isLoading = true;

    if (selectedTriage) {
      this.triage = selectedTriage;
    }

    if (this.questionnaire.careAdvice) {
      this.triage.carePlanIds = this.questionnaire.careAdvice.map(cp => cp.id);
    }
    this.triage.caseId = this.questionnaire.caseId;
    if (switchCdss) {
      this.triage.questionResponse = null;
    } else if (!back && this.questionnaire.triageQuestions) {
      this.triage.questionResponse = this.buildQuestionnaireResponses();
    }

    this.triage.serviceDefinitionId = this.sessionStorage['serviceDefinitionId'];
    this.triage.cdssSupplierId = Number.parseInt(
      this.sessionStorage['cdssSupplierId']
    );
    this.triage.amendingPrevious = this.amendingPrevious;
    this.triage.patientId = this.patientId;
    this.questionnaire = await this.triageService.progressTriage(this.triage, back)
      .then(questionnaire => {
        this.errorMessage = questionnaire.errorMessage;
        return questionnaire;
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
      await this.redirect(back);
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

  private buildQuestionnaireResponses() {
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

          if (this.answerSelected.some(e => e.triageQuestion === questionResponse.triageQuestion)) {
            this.answerSelected.push(questionResponse);
          }
        }
      });
    });

    // loop over each answer and build it into a response
    return this.answerSelected.map(element => {
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
        questionnaireResponse.responseInteger = element.responseInteger;
      } else if (questionnaireResponse.questionType === 'BOOLEAN') {
        questionnaireResponse.responseBoolean = element.responseBoolean;
      } else if (questionnaireResponse.questionType === 'DECIMAL') {
        questionnaireResponse.responseDecimal = element.responseDecimal;
      } else if (questionnaireResponse.questionType === 'DATE' || questionnaireResponse.questionType === 'DATETIME') {
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
      return questionnaireResponse;
    });
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
      this.questionnaire = await this.triageService.progressTriage(
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
    this.dialog.open(SwitchServicePromptDialogComponent, {
      width: '520px',
      data: {
        cdssSupplierId: this.questionnaire.cdssSupplierId,
        oldServiceDefinition: this.oldServiceDefinition,
        newServiceDefinition: this.newServiceDefinition
      }
    });
  }

  ngOnDestroy() {
    this.answerSubscription.unsubscribe();
  }
}
