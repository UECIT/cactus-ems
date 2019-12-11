import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'triage-selection',
  templateUrl: './triage-selection.component.html',
  styleUrls: ['../main.component.css']
})
export class TriageSelectionComponent {

  @Input() title: String;
  @Input() buttonText: String;
  @Input() options: any[];
  @Output() onChange = new EventEmitter<any>();

  selected: any;

  constructor() { }

  ngOnChanges() {
    if (this.options) {
      this.change(this.options[0]);
    }
  }

  change(selection: any) {
    this.onChange.emit(selection);
    this.selected = selection;
  }

}
 