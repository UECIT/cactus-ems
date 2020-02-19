import { Component, OnInit } from '@angular/core';
import { environment } from '../environments/environment';
import { Settings, Code } from './model';
import { SessionStorage } from 'h5webstorage';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent  implements OnInit {
  title = 'EMS Test Harness';
  version = environment.version;

  settings: Settings = new Settings();

  constructor(private sessionStorage: SessionStorage, private toastr: ToastrService) { }

  ngOnInit() {
    var oldSettings: Settings = this.sessionStorage['settings'];

    if (!oldSettings) {
      this.settings.userLanguage = new Code();
      this.settings.userTaskContext = new Code();
      this.settings.recipientLanguage = new Code();
  
      this.settings.userLanguage.code = 'en';
      this.settings.userLanguage.display = 'English';
  
      this.settings.userTaskContext.code = 'example';
      this.settings.userTaskContext.display = 'Triage';
  
      this.settings.recipientLanguage.code = 'en';
      this.settings.recipientLanguage.display = 'English';
      this.sessionStorage.setItem('settings', JSON.stringify(this.settings));
    }
  }
}
