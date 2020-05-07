import { Environment } from './model/environment';
import { Component, OnInit } from '@angular/core';
import { Settings, Code } from './model';
import { SessionStorage } from 'h5webstorage';
import { EnvironmentService } from './service/environment.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent  implements OnInit {
  title = 'EMS Test Harness';
  settings: Settings = new Settings();
  env: Environment;

  constructor(
    private sessionStorage: SessionStorage, 
    private environmentService: EnvironmentService) { }

  async ngOnInit() {
    await this.environmentService.getVariables()
        .then(env => this.sessionStorage.setItem("properties", JSON.stringify(env)));

    this.env = this.sessionStorage['properties'];

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
