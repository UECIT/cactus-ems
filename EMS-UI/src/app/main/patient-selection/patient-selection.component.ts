import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Patient } from 'src/app/model/patient';

@Component({
  selector: 'patient-selection',
  templateUrl: './patient-selection.component.html',
  styleUrls: ['../main.component.css']
})
export class PatientSelectionComponent {

  @Input() patients: Patient[];
  @Input() disabled: boolean;
  @Output() onChange = new EventEmitter<Patient>();

  @Input() selectedPatient: Patient;

  change(selection: Patient) {
    this.onChange.emit(selection);
    this.selectedPatient = selection;
  }

}
