import { Component, OnInit, Input } from '@angular/core';
import { ProcessTriage } from 'src/app/model/processTriage';
import {SessionStorage, StorageProperty, LocalStorage} from 'h5webstorage';

@Component({
  selector: 'app-questions-display',
  templateUrl: './questions-display.component.html',
  styleUrls: ['./questions-display.component.css']
})
export class QuestionsDisplayComponent implements OnInit {
  @StorageProperty({ storageKey: 'triageItems', storage: 'Session'}) public triageQuestions: string = null;
  @Input() amendingPrevious: boolean;
  @Input() ExternalProcessTriage: (
    switchCdss: boolean,
    back: boolean,
    selectedTriage: ProcessTriage
  ) => boolean;

  constructor(private localStorage: LocalStorage, private sessionStorage: SessionStorage) {
   }

  ngOnInit() {
  }

  async selectQuestion(triageQuestion) {
    console.log(triageQuestion);
    console.log(this.amendingPrevious);
    if (!this.amendingPrevious) {
      await this.ExternalProcessTriage(false, true, triageQuestion);
    }
  }

  coordinatesString(response): string {
    return response.question.replace(/!\[.*?\]\((.*?)\)/g, "") + ": " 
      + response.responseCoordinates.x + "-" 
      + response.responseCoordinates.y;
  }

}
