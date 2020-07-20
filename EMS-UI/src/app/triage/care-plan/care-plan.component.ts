import { Component, Input } from '@angular/core';
import { CarePlan } from '../../model';

@Component({
  selector: 'app-care-plan',
  templateUrl: './care-plan.component.html'
})
export class CarePlanComponent {
  @Input() carePlan: CarePlan;
}