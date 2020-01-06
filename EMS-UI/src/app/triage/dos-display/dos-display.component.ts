import { Component, Input } from '@angular/core';
import { ReferralRequest } from 'src/app/model/questionnaire';
import { DosService } from 'src/app/service/dos.service';

@Component({
  selector: 'app-dos-display',
  templateUrl: './dos-display.component.html',
  styleUrls: ['./dos-display.component.css']
})
export class DosDisplayComponent {
  @Input() referralRequest: ReferralRequest;

  response: object;
  error: object;
  constructor(private dosService: DosService) {
  }

  async getDosResponse() {
    this.response = null;
    this.error = null;
    await this.dosService
      .getDosResponse(this.referralRequest)
      .toPromise()
      .then(
        response => {
          this.response = response;
        },
        error => {
          this.error = error;
        }
      );
  }
}
