import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'triage-selection',
  templateUrl: './triage-selection.component.html',
  styleUrls: ['../main.component.css']
})
export class TriageSelectionComponent {

  @Input() title: String;
  @Input() options: any[];
  @Output() onChange = new EventEmitter<any>();

  selected: any;

  ngOnChanges() {
    if (this.options) {
      this.selected = this.options[0];
    }
  }

  change(selection: any) {
    this.onChange.emit(selection);
    this.selected = selection;
  }

}
 