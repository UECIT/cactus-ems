import { Component, Input } from '@angular/core';
import { ProgressTriageRequest } from 'src/app/model/progressTriageRequest';
import {StorageProperty} from 'h5webstorage';

@Component({
  selector: 'app-questions-display',
  templateUrl: './questions-display.component.html',
  styleUrls: ['./questions-display.component.css']
})
export class QuestionsDisplayComponent {
  @StorageProperty({ storageKey: 'triageItems', storage: 'Session'})
  public triageQuestions: ProgressTriageRequest[] = null;
  @Input() amendingPrevious: boolean;
  @Input() ExternalProgressTriage: (
    switchCdss: boolean,
    back: boolean,
    selectedTriage: ProgressTriageRequest
  ) => boolean;

  async selectQuestion(triageQuestion) {
    console.log(triageQuestion);
    console.log(this.amendingPrevious);
    if (!this.amendingPrevious) {
      await this.ExternalProgressTriage(false, true, triageQuestion);
    }
  }

  coordinatesString(response): string {
    return response.question.replace(/!\[.*?\]\((.*?)\)/g, "") + ": " 
      + response.responseCoordinates.x + "-" 
      + response.responseCoordinates.y;
  }

}
