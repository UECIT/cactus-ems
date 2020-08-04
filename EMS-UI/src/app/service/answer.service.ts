import { QuestionResponse } from './../model/questionnaire';
import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({providedIn: 'root'})
export class AnswerService {

  private answer = new Subject<QuestionResponse[]>();

  answerSelected$ = this.answer.asObservable();

  selectAnswer(answer: QuestionResponse[]) {
    this.answer.next(answer);
  }
}
