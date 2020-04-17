import { QuestionniareType } from './question-types/questionniare-type.enum';
import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Options, Questionnaire, QuestionResponse, TriageQuestion} from '../../model/questionnaire';
import {QuestionnaireResponse} from '../../model/processTriage';
import {MatDialog} from '@angular/material';
import {ServiceDefinitionService} from '../../service/service-definition.service';

@Component({
  selector: 'app-questionnaire',
  templateUrl: './questionnaire.component.html',
  styleUrls: ['./questionnaire.component.css']
})
export class QuestionnaireComponent implements OnInit {
  @Input() questionnaire: Questionnaire;
  @Input() answerSelected: QuestionResponse[];
  @Input() questionnaireResponse: QuestionnaireResponse;
  @Output() answerSelectedChange = new EventEmitter<QuestionResponse[]>();

  freeText: Map<string, string>;
  url: Map<string, string> = new Map();
  enableWhen: Map<string, string[]> = new Map();
  supplierId: string;
  questionnaireType = QuestionniareType; 
  // attachmentError: boolean;

  constructor(public dialog: MatDialog,
              private serviceDefinitionService: ServiceDefinitionService) {
  }

  async ngOnInit() {

    if (this.questionnaire.triageQuestions != null) {
      const initialAttachQuestions = this.questionnaire.triageQuestions.filter(
          question => question.responseAttachmentInitial != null
      );
      for (let i = 0; i < initialAttachQuestions.length; i++) {
        this.url.set(
            initialAttachQuestions[i].questionId,
            initialAttachQuestions[i].responseAttachmentInitial
        );
      }
    }

    if (this.questionnaire.triageQuestions != null) {
      const enableWhenQuestions = this.questionnaire.triageQuestions.filter(
          question => question.enableWhenQuestionnaireId != null
      );
      for (let i = 0; i < enableWhenQuestions.length; i++) {
        const answer = [enableWhenQuestions[i].enableWhenQuestionnaireId, enableWhenQuestions[i].enableWhenAnswer.toString()];
        this.enableWhen.set(
            enableWhenQuestions[i].questionId,
            answer
        );
      }
    }

    this.freeText = new Map<string, string>();
    if (this.answerSelected == null) {
      this.answerSelected = [];
    } else if (this.questionnaire.triageQuestions != null) {
      const stringQuestions = this.questionnaire.triageQuestions.filter(
          question => question.questionType === 'STRING'
      );
      for (let i = 0; i < stringQuestions.length; i++) {
        const answers = this.answerSelected.filter(
            answer =>
                answer.triageQuestion.questionId === stringQuestions[i].questionId
        );
        if (answers.length > 0 && answers[0].answer != null) {
          // expect either a single answer or none for text questions
          this.freeText.set(
              stringQuestions[i].questionId,
              answers[0].answer.display
          );
        }
      }
    }
    this.supplierId = await this.serviceDefinitionService.getCdssSupplierUrl(this.questionnaire.cdssSupplierId);
  }

  getFreeText(questionId: string): string {
    return this.freeText.get(questionId);
  }

  getUrl(questionId: string): string {
    return this.url.get(questionId);
  }

  checkEnableWhen(triageQuestion: TriageQuestion): boolean {
    const enableOrNot = this.enableWhen.get(triageQuestion.questionId);
    if (enableOrNot != null) {
      const hasAnswer = this.answerSelected.filter(
          answer => answer.triageQuestion.questionId === enableOrNot[0]
      );
      if (hasAnswer.length > 0) {
        return enableOrNot[1] !== 'true';
      }
    }
    return false;
  }

  getType(triageQuestion: TriageQuestion) : QuestionniareType {
    if (triageQuestion.questionType == 'ATTACHMENT' && !this.hasInitialValue(triageQuestion)) {
      return QuestionniareType.ATTACHMENT;
    }
    if (triageQuestion.questionType == 'CHOICE') {
      return QuestionniareType.CHOICE;
    }
    return QuestionniareType.NOT_SUPPORTED
  }

  mouseClickOnImage(event: any, triageQuestion: TriageQuestion) {
    if (this.checkEnableWhen(triageQuestion)) {
      return;
    }

    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.triageQuestion.responseCoordinates = {
      x: event.offsetX,
      y: event.offsetY
    };
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(
        triageQuestion.questionId,
        questionResponse.triageQuestion.responseCoordinates.x + ', ' + questionResponse.triageQuestion.responseCoordinates.y
    );
  }

  hasInitialValue(triageQuestion: TriageQuestion) {
    return triageQuestion.responseAttachmentInitial != null;
  }

  onStringAnswerChange(responseString: string, triageQuestion: TriageQuestion) {
    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseString = responseString;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(triageQuestion.questionId, responseString);
  }

  onIntegerAnswerChange(
      responseInterger: number,
      triageQuestion: TriageQuestion
  ) {
    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseInterger = responseInterger;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(triageQuestion.questionId, responseInterger.toString());
  }

  onDecimalAnswerChange(
      responseDecimal: number,
      triageQuestion: TriageQuestion
  ) {
    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseDecimal = responseDecimal;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(triageQuestion.questionId, responseDecimal.toString());
  }

  onBooleanAnswerChange(
      responseBoolean: boolean,
      triageQuestion: TriageQuestion
  ) {
    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseBoolean = responseBoolean;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(
        triageQuestion.questionId,
        responseBoolean.valueOf.toString()
    );
  }

  onDateAnswerChange(responseDate: Date, triageQuestion: TriageQuestion) {
    this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseDate = responseDate.toISOString();
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(triageQuestion.questionId, responseDate.toISOString());
  }

  // Get the answer that was selected.
  selectedAnswer(selectedOption: Options, triageQuestion: TriageQuestion) {
    if (this.checkEnableWhen(triageQuestion)) {
      return;
    }
    this.cleanupAnswersSelected(
        triageQuestion.questionId,
        selectedOption,
        triageQuestion.repeats
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.answer = selectedOption;

    if (
        this.answerSelected.filter(
            vendor => vendor.answer === questionResponse.answer
        ).length > 0
    ) {
      this.answerSelected = this.answerSelected.filter(
          e => e.answer !== selectedOption
      );
    } else {
      this.answerSelected.push(questionResponse);
    }

    this.answerSelectedChange.emit(this.answerSelected);
  }

  cleanupAnswersSelected(
      selectedQuestionId: string,
      selectedOption: Options,
      repeats: boolean
  ) {
    if (!repeats || selectedOption.extension) {
      this.answerSelected = this.answerSelected.filter(
          e => e.triageQuestion.questionId !== selectedQuestionId
      );
    } else if (!selectedOption.extension) {
      let i = 0;
      this.answerSelected.forEach(element => {
        if (
            element.answer.extension !== selectedOption.extension &&
            element.triageQuestion.questionId === selectedQuestionId
        ) {
          this.answerSelected.splice(i, 1);
        }
        i++;
      });
    }
  }

  selectedContains(
      selectedOption: Options,
      selectedQuestionId: TriageQuestion
  ): boolean {
    if (
        this.answerSelected.some(
            e =>
                e.answer != null &&
                e.answer.code === selectedOption.code &&
                e.answer.display === selectedOption.display &&
                e.triageQuestion.questionId === selectedQuestionId.questionId
        )
    ) {
      return true;
    }
  }

  hasDraftCareAdvice(careAdvice: any[]) {
    return careAdvice && careAdvice.find(ca => ca.status === 'draft')
  }

  hasDraftReferralRequest() {
    return this.questionnaire.referralRequest && this.questionnaire.referralRequest.status === 'Draft';
  }

  isImageMap(question: TriageQuestion) {
    return question.questionType == 'REFERENCE' && question.extension.code == 'imagemap';
  }

  getImageUrl(question: String) {
    if (this.supplierId) {
      return this.supplierId.replace('/fhir', '/image/') + question.match(/!\[.*?\]\((.*?)\)/)[1];
    }
    return 'Image not found';
  }

  formatQuestion(question: TriageQuestion) {
    return question.question.replace(/!\[.*?\]\((.*?)\)/g, '');
  }
}
