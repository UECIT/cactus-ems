import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { Settings, Person } from './model/settings';
import { Code } from './model/case';
import { SessionStorage } from 'h5webstorage';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent  implements OnInit {
  title = 'EMS Test Harness';
  version = environment.version;

  settings: Settings = new Settings();

  constructor(private sessionStorage: SessionStorage, private toastr: ToastrService) { }

  ngOnInit() {
    var oldSettings: Settings = this.sessionStorage['settings'];

    if (!oldSettings) {
      this.settings.initiatingPerson = new Person();
      this.settings.initiatingPerson.name = 'Joe Bloggs';
      this.settings.initiatingPerson.telecom = '0123 123 1234';
      this.settings.initiatingPerson.gender = 'male';
      this.settings.initiatingPerson.birthDate = '2011-09-07';
  
      this.settings.receivingPerson = new Person();
      this.settings.receivingPerson.name = 'Jane Bloggs';
      this.settings.receivingPerson.telecom = '0123 123 1234';
      this.settings.receivingPerson.gender = 'female';
      this.settings.receivingPerson.birthDate = '2011-09-07';
  
      this.settings.userLanguage = new Code();
      this.settings.userTaskContext = new Code();
      this.settings.recipientType = new Code();
      this.settings.recipientLanguage = new Code();
  
      this.settings.userLanguage.code = 'en';
      this.settings.userLanguage.display = 'English';
  
      this.settings.userTaskContext.code = 'example';
      this.settings.userTaskContext.display = 'Triage';
  
      this.settings.recipientType.code = '116154003';
      this.settings.recipientType.display = 'Patient';
  
      this.settings.recipientLanguage.code = 'en';
      this.settings.recipientLanguage.display = 'English';
      this.sessionStorage.setItem('settings', JSON.stringify(this.settings));
    }
  }
}
