import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-error-display',
  templateUrl: './error-display.component.html'
})
export class ErrorDisplayComponent implements OnInit {
  @Input() errorObject: any;

  constructor() {}

  ngOnInit() {}
}
