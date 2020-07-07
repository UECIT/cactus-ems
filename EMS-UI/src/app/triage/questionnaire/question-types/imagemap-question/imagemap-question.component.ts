import { AnswerService } from './../../../../service/answer.service';
import { QuestionResponse, TriageQuestion, Coordinates } from './../../../../model/questionnaire';
import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'imagemap-question',
  templateUrl: './imagemap-question.component.html',
  styleUrls: ['./imagemap-question.component.css']
})
export class ImagemapQuestionComponent {

  @Input() answerSelected: QuestionResponse[];
  @Input() triageQuestion: TriageQuestion;
  @Input() disabled: boolean;

  selectedCoordinates: Coordinates;

  constructor(private answerService: AnswerService) { }

  mouseClickOnImage(event: any, triageQuestion: TriageQuestion) {
    if (this.disabled) {
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
    this.answerService.selectAnswer(this.answerSelected);
    this.selectedCoordinates = questionResponse.triageQuestion.responseCoordinates;
  }

  get currentSelection() {
    if (!this.selectedCoordinates) {
      return "None";
    }
    return "(" + 
      this.selectedCoordinates.x + "," + 
      this.selectedCoordinates.y + ")";
  }

  getImageUrl(question: String) {
    const image = question.match(/!\[.*?\]\((.*?)\)/)[1];
    console.log(image);
  }
}
