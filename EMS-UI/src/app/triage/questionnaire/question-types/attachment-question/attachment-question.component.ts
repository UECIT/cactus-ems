import { AnswerService } from './../../../../service/answer.service';
import { Component, OnInit, Input, EventEmitter, Output } from '@angular/core';
import { QuestionResponse, TriageQuestion } from 'src/app/model';

@Component({
  selector: 'attachment-question',
  templateUrl: './attachment-question.component.html',
  styleUrls: ['../../questionnaire.component.css']
})
export class AttachmentQuestionComponent {

  @Input() answerSelected: QuestionResponse[];
  @Output() answerSelectedChange = new EventEmitter<QuestionResponse[]>();

  @Input() triageQuestion: TriageQuestion;
  @Input() disabled: boolean;
  
  attachmentError: boolean;

  constructor(private answerService: AnswerService) {}

  onChange(event: any, triageQuestion: TriageQuestion) {
    if (event.target.files[0]) {
      this.answerSelected = this.answerSelected.filter(
          e => e.triageQuestion.questionId !== triageQuestion.questionId
      );
      const questionResponse: QuestionResponse = new QuestionResponse();
      questionResponse.triageQuestion = triageQuestion;
      const validTypes = ["image/gif", "image/jpeg", "image/png"];
      if (!validTypes.includes(event.target.files[0].type)) {
        this.attachmentError = true;
        this.answerSelected.pop();
        this.answerService.selectAnswer(this.answerSelected);
        return;
      }
      this.attachmentError = false;
      questionResponse.responseAttachmentType = event.target.files[0].type;

      let reader = new FileReader();
      reader.readAsText(event.target.files[0]);
      reader.onload = (readEvent: any) => {
        questionResponse.responseAttachment = readEvent.target.result;
        this.answerSelected.push(questionResponse);
        this.answerService.selectAnswer(this.answerSelected);
        // this.answerSelectedChange.emit(this.answerSelected);
      };
    }
  }

}
