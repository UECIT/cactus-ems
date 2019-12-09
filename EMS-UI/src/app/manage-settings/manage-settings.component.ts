import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Settings, Person } from '../model/settings';
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
      initiatingPersonName: new FormControl(oldSettings.initiatingPerson.name, [
        Validators.required
      ]),
      initiatingPersonTelecom: new FormControl(oldSettings.initiatingPerson.telecom, [
        Validators.required
      ]),
      initiatingPersonGender: new FormControl(oldSettings.initiatingPerson.gender, [Validators.required]),
      initiatingPersonBirthDate: new FormControl(oldSettings.initiatingPerson.birthDate, [
        Validators.required
      ]),
      userLanguage: new FormControl(oldSettings.userLanguage.display, [Validators.required]),
      userTaskContext: new FormControl(oldSettings.userTaskContext.display, [Validators.required]),
      receivingPersonName: new FormControl(oldSettings.receivingPerson.name, [
        Validators.required
      ]),
      receivingPersonTelecom: new FormControl(oldSettings.receivingPerson.telecom, [
        Validators.required
      ]),
      receivingPersonGender: new FormControl(oldSettings.receivingPerson.gender, [Validators.required]),
      receivingPersonBirthDate: new FormControl(oldSettings.receivingPerson.birthDate, [
        Validators.required
      ]),
      recipientType: new FormControl(oldSettings.recipientType.display, [Validators.required]),
      recipientLanguage: new FormControl(oldSettings.recipientLanguage.display, [Validators.required]),
    });
    this.loaded = true;
    this.saved = false;
  }

  get initiatingPersonName() {
    return this.formData.get('initiatingPersonName');
  }
  get initiatingPersonTelecom() {
    return this.formData.get('initiatingPersonTelecom');
  }
  get initiatingPersonGender() {
    return this.formData.get('initiatingPersonGender');
  }
  get initiatingPersonBirthDate() {
    return this.formData.get('initiatingPersonBirthDate');
  }
  get userLanguage() {
    return this.formData.get('userLanguage');
  }
  get userTaskContext() {
    return this.formData.get('userTaskContext');
  }
  get receivingPersonName() {
    return this.formData.get('receivingPersonName');
  }
  get receivingPersonTelecom() {
    return this.formData.get('receivingPersonTelecom');
  }
  get receivingPersonGender() {
    return this.formData.get('receivingPersonGender');
  }
  get receivingPersonBirthDate() {
    return this.formData.get('receivingPersonBirthDate');
  }
  get recipientType() {
    return this.formData.get('recipientType');
  }
  get recipientLanguage() {
    return this.formData.get('recipientLanguage');
  }

  updateSettings(data) {
    this.settings.initiatingPerson = new Person();
    this.settings.initiatingPerson.name = data.initiatingPersonName;
    this.settings.initiatingPerson.telecom = data.initiatingPersonTelecom;
    this.settings.initiatingPerson.gender = data.initiatingPersonGender;
    this.settings.initiatingPerson.birthDate = data.initiatingPersonBirthDate;

    this.settings.receivingPerson = new Person();
    this.settings.receivingPerson.name = data.receivingPersonName;
    this.settings.receivingPerson.telecom = data.receivingPersonTelecom;
    this.settings.receivingPerson.gender = data.receivingPersonGender;
    this.settings.receivingPerson.birthDate = data.receivingPersonBirthDate;

    this.settings.userLanguage = new Code();
    this.settings.userTaskContext = new Code();
    this.settings.recipientType = new Code();
    this.settings.recipientLanguage = new Code();

    this.settings.userLanguage.code = 'en';
    this.settings.userLanguage.display = data.userLanguage;

    this.settings.userTaskContext.code = data.userTaskContext;
    this.settings.userTaskContext.display = data.userTaskContext;

    this.settings.recipientType.code = '116154003';
    this.settings.recipientType.display = data.recipientType;

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
