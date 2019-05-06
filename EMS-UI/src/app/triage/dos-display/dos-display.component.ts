import { Component, OnInit, Input } from '@angular/core';
import { ReferralRequest } from 'src/app/model/questionnaire';
import { DosService } from 'src/app/service/dos.service';
import { CheckCapacitySummary } from 'src/app/model/checkCapacitySummary';
import { Store } from '@ngrx/store';
import { AppState } from 'src/app/app.state';
import { Patient } from 'src/app/model/patient';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dos-display',
  templateUrl: './dos-display.component.html',
  styleUrls: ['./dos-display.component.css']
})
export class DosDisplayComponent implements OnInit {
  @Input() referralRequest: ReferralRequest;

  checkCapacitySummaryResponse: object;
  checkCapacitySummaryError: object;
  checkCapacitySummary: CheckCapacitySummary = new CheckCapacitySummary();
  state: Observable<Patient>;

  constructor(private dosService: DosService, private store: Store<AppState>) {
    this.state = this.store.select('patient');
  }

  ngOnInit() {
    this.state.subscribe(res => {
      if (res.gender === 'female') {
        this.checkCapacitySummary.gender = 'F';
      } else if (res.gender === 'male') {
        this.checkCapacitySummary.gender = 'M';
      } else {
        this.checkCapacitySummary.gender = 'I';
      }
    });

    this.checkCapacitySummary.postcode = null;
    this.checkCapacitySummary.disposition = null;
    this.checkCapacitySummary.symptomGroup = null;
    this.checkCapacitySummary.symptomDiscriminatorInt = null;
    this.checkCapacitySummary.searchDistance = null;
    this.checkCapacitySummary.service = 'rest';

    this.referralRequest.serviceRequested.forEach(element => {
      if (element.serviceRequestedSystem === 'DX') {
        this.checkCapacitySummary.disposition = element.serviceRequestedCode;
      }

      if (element.serviceRequestedSystem === 'SG') {
        this.checkCapacitySummary.symptomGroup = element.serviceRequestedCode;
      }

      if (element.serviceRequestedSystem === 'SD') {
        this.checkCapacitySummary.symptomDiscriminatorInt =
          element.serviceRequestedCode;
      }
    });
  }

  nullifyCheckCapacitySummaryResponse() {
    this.checkCapacitySummaryResponse = null;
  }

  async getDosResponse() {
    this.checkCapacitySummaryResponse = null;
    this.checkCapacitySummaryError = null;
    await this.dosService
      .getDosResponse(this.checkCapacitySummary)
      .toPromise()
      .then(
        response => {
          this.checkCapacitySummaryResponse = response;
        },
        error => {
          this.checkCapacitySummaryError = error;
        }
      );
  }
}
