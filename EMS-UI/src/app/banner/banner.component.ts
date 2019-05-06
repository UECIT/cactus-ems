import { Component, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { AppState } from '../app.state';
import { Observable } from 'rxjs';
import { Patient } from '../model/patient';

@Component({
  selector: 'app-banner',
  templateUrl: './banner.component.html',
  styleUrls: ['./banner.component.css']
})
export class BannerComponent implements OnInit {
  state: Observable<Patient>;
  constructor(private store: Store<AppState>) {
    this.state = this.store.select('patient');
  }

  ngOnInit() {}
}
