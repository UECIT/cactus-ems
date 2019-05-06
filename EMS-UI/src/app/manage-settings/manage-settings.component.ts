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
  title: String = 'Update Settings';

  constructor(private sessionStorage: SessionStorage) {}

  ngOnInit() {
    this.formData = new FormGroup({
      initiatingPersonName: new FormControl('Joe Bloggs', [
        Validators.required
      ]),
      initiatingPersonTelecom: new FormControl('0123 123 1234', [
        Validators.required
      ]),
      initiatingPersonGender: new FormControl('male', [Validators.required]),
      initiatingPersonBirthDate: new FormControl('2011-09-07', [
        Validators.required
      ]),
      userType: new FormControl('Call Handler', [Validators.required]),
      userLanguage: new FormControl('English', [Validators.required]),
      userTaskContext: new FormControl('Triage', [Validators.required]),
      receivingPersonName: new FormControl('Jane Bloggs', [
        Validators.required
      ]),
      receivingPersonTelecom: new FormControl('0123 123 1234', [
        Validators.required
      ]),
      receivingPersonGender: new FormControl('female', [Validators.required]),
      receivingPersonBirthDate: new FormControl('2011-09-07', [
        Validators.required
      ]),
      recipientType: new FormControl('Patient', [Validators.required]),
      recipientLanguage: new FormControl('English', [Validators.required]),
      setting: new FormControl('111', [Validators.required])
    });
    this.loaded = true;
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
  get userType() {
    return this.formData.get('userType');
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
  get setting() {
    return this.formData.get('setting');
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

    this.settings.userType = new Code();
    this.settings.userLanguage = new Code();
    this.settings.userTaskContext = new Code();
    this.settings.recipientType = new Code();
    this.settings.recipientLanguage = new Code();
    this.settings.setting = new Code();

    this.settings.userType.code = '158974003';
    this.settings.userType.display = data.userType;

    this.settings.userLanguage.code = 'en';
    this.settings.userLanguage.display = data.userLanguage;

    this.settings.userTaskContext.code = data.userTaskContext;
    this.settings.userTaskContext.display = data.userTaskContext;

    this.settings.recipientType.code = '116154003';
    this.settings.recipientType.display = data.recipientType;

    this.settings.recipientLanguage.code = 'en';
    this.settings.recipientLanguage.display = data.recipientLanguage;

    this.settings.setting.code = data.setting;
    this.settings.setting.display = data.setting;
    this.sessionStorage.setItem('settings', JSON.stringify(this.settings));
  }
}
