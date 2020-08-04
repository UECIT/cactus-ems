import { CdssService } from './../../../../service/cdss.service';
import { QuestionResponse, TriageQuestion, Coordinates } from './../../../../model/questionnaire';
import { Component, OnInit, Input } from '@angular/core';
import { AnswerService } from 'src/app/service/answer.service';

@Component({
  selector: 'imagemap-question',
  templateUrl: './imagemap-question.component.html',
  styleUrls: ['../../questionnaire.component.css']
})
export class ImagemapQuestionComponent implements OnInit {

  @Input() answerSelected: QuestionResponse[];
  @Input() triageQuestion: TriageQuestion;
  @Input() cdssSupplierId: number;
  @Input() disabled: boolean;

  selectedCoordinates: Coordinates;
  imgSrc: any;
  error: any;

  constructor(
    private answerService: AnswerService,
    private cdssService: CdssService
  ) { }

  ngOnInit() {
    this.getImageUrl(this.triageQuestion.question);
  }

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

  private async getImageUrl(question: String) {
    const image = question.match(/!\[.*?\]\((.*?)\)/)[1];
    this.cdssService.getImage(this.cdssSupplierId, image)
      .then(res => this.createImageFromBlob(res))
      .catch(err => this.error = err);
  }

  private createImageFromBlob(image: Blob) {
    let reader = new FileReader();
    reader.addEventListener("load", e => {
      let target = e.target as FileReader;
      this.imgSrc = target.result;
      this.error = null;
    }, false);
    reader.readAsDataURL(image);
  }
}
