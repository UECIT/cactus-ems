import { Component, OnInit, Input } from '@angular/core';
import { QuestionResponse, TriageQuestion } from 'src/app/model';
import { AnswerService } from 'src/app/service/answer.service';

@Component({
  selector: 'choice-question',
  templateUrl: './choice-question.component.html',
  styleUrls: ['../../questionnaire.component.css']
})
export class ChoiceQuestionComponent {

  @Input() answerSelected: QuestionResponse[];
  @Input() triageQuestion: TriageQuestion;
  @Input() disabled: boolean;

  constructor(private answerService: AnswerService) {}

  selectedAnswer(selectedOption: Options, triageQuestion: TriageQuestion) {
    if (this.disabled) {
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

}
