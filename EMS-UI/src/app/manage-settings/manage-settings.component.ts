import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Settings } from '../model/settings';
import { Code } from '../model/case';
import { SessionStorage } from 'h5webstorage';

@Component({
  selector: 'app-manage-settings',
  templateUrl: './manage-settings.component.html',
  styleUrls: ['./manage-settings.component.css']
})
export class ManageSettingsComponent implements OnInit {
  formData: FormGroup;
  settings: Settings = new Settings();
  loaded = false;
  saved: boolean;
  title: String = 'Update Settings';

  constructor(private sessionStorage: SessionStorage) {}

  ngOnInit() {
    var oldSettings: Settings = this.sessionStorage['settings'];
    this.formData = new FormGroup({
      userLanguage: new FormControl(oldSettings.userLanguage.display, [Validators.required]),
      userTaskContext: new FormControl(oldSettings.userTaskContext.display, [Validators.required]),
      recipientLanguage: new FormControl(oldSettings.recipientLanguage.display, [Validators.required]),
    });
    this.loaded = true;
    this.saved = false;
  }

  get userLanguage() {
    return this.formData.get('userLanguage');
  }
  get userTaskContext() {
    return this.formData.get('userTaskContext');
  }
  get recipientLanguage() {
    return this.formData.get('recipientLanguage');
  }

  updateSettings(data) {

    this.settings.userLanguage = new Code();
    this.settings.userTaskContext = new Code();
    this.settings.recipientLanguage = new Code();

    this.settings.userLanguage.code = 'en';
    this.settings.userLanguage.display = data.userLanguage;

    this.settings.userTaskContext.code = data.userTaskContext;
    this.settings.userTaskContext.display = data.userTaskContext;

    this.settings.recipientLanguage.code = 'en';
    this.settings.recipientLanguage.display = data.recipientLanguage;

    //TODO: makde sure we do not overwrite settings made elsewhere - we should work out a better way of saving settings.
    var oldSettings: Settings = this.sessionStorage['settings'];
    this.settings.userType = oldSettings.userType;
    this.settings.setting = oldSettings.setting;

    this.sessionStorage.setItem('settings', JSON.stringify(this.settings));
    this.saved = true;
  }
}
