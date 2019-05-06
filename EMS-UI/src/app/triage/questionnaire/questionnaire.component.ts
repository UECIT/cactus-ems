import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import {
  Questionnaire,
  Options,
  QuestionResponse,
  TriageQuestion
} from '../../model/questionnaire';
import { QuestionnaireResponse } from '../../model/processTriage';

@Component({
  selector: 'app-questionnaire',
  templateUrl: './questionnaire.component.html',
  styleUrls: ['./questionnaire.component.css']
})
export class QuestionnaireComponent implements OnInit {
  @Input() questionnaire: Questionnaire;
  @Input() answerSelected: QuestionResponse[];
  @Input() questionnaireResponse: QuestionnaireResponse;
  @Input() amendingPrevious: Boolean;
  @Output() answerSelectedChange = new EventEmitter<QuestionResponse[]>();

  freeText: Map<string, string>;
  url: Map<string, string> = new Map();
  enableWhen: Map<string, string[]> = new Map();

  constructor() {}

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
      this.answerSelected = new Array();
    } else if (
      this.answerSelected != null &&
      this.questionnaire.triageQuestions != null
    ) {
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

  mouseClickOnImage(event: any, triageQuestion: TriageQuestion) {
    if (this.checkEnableWhen(triageQuestion)) {
      return;
    }

    this.answerSelected = this.answerSelected.filter(
      e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responseString =
      'ImageCoordinatePosition:' + event.offsetX + ',' + event.offsetY;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(
      triageQuestion.questionId,
      questionResponse.responseString
    );
  }

  hasInitialValue(triageQuestion: TriageQuestion) {
    if (triageQuestion.responseAttachmentInitial != null) {
      return true;
    } else {
      return false;
    }
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
    responceBoolean: boolean,
    triageQuestion: TriageQuestion
  ) {
    this.answerSelected = this.answerSelected.filter(
      e => e.triageQuestion.questionId !== triageQuestion.questionId
    );
    const questionResponse: QuestionResponse = new QuestionResponse();
    questionResponse.triageQuestion = triageQuestion;
    questionResponse.responceBoolean = responceBoolean;
    this.answerSelected.push(questionResponse);
    this.answerSelectedChange.emit(this.answerSelected);
    this.freeText.set(
      triageQuestion.questionId,
      responceBoolean.valueOf.toString()
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

  onAttachmentAnswerChange(event: any, triageQuestion: TriageQuestion) {
    if (event.target.files[0]) {
      this.answerSelected = this.answerSelected.filter(
        e => e.triageQuestion.questionId !== triageQuestion.questionId
      );
      const questionResponse: QuestionResponse = new QuestionResponse();
      questionResponse.triageQuestion = triageQuestion;
      questionResponse.responseAttachmentType = event.target.files[0].type;

      let reader = new FileReader();
      reader.readAsText(event.target.files[0]);
      reader.onload = (readEvent: any) => {
        questionResponse.responseAttachment = readEvent.target.result;
        this.answerSelected.push(questionResponse);
        this.answerSelectedChange.emit(this.answerSelected);
        this.freeText.set(triageQuestion.questionId, readEvent.target.result);
      };

      reader = new FileReader();
      reader.readAsDataURL(event.target.files[0]);
      reader.onload = (urlEvent: any) => {
        this.url.set(triageQuestion.questionId, urlEvent.target.result);
      };
    }
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
}
