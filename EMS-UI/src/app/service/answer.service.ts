import { Subject } from 'rxjs';
import { Injectable } from '@angular/core';
import { QuestionResponse } from '../model';

@Injectable({
  providedIn: 'root'
})
export class AnswerService {

  private answer = new Subject<QuestionResponse[]>();

  answerSelected$ = this.answer.asObservable();

  selectAnswer(answer: QuestionResponse[]) {
    this.answer.next(answer);
  }
}
