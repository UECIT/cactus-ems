package uk.nhs.ctp.service.builder;

import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.NHSNumberIdentifier;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.StorageService;

@Component
public class CareConnectPatientBuilder {

  private CareConnectOrganizationBuilder careConnectOrganizationBuilder;
  private CareConnectPractitionerBuilder careConnectPractitionerBuilder;

  public CareConnectPatientBuilder(
      CareConnectOrganizationBuilder careConnectOrganizationBuilder,
      CareConnectPractitionerBuilder careConnectPractitionerBuilder) {
    this.careConnectOrganizationBuilder = careConnectOrganizationBuilder;
    this.careConnectPractitionerBuilder = careConnectPractitionerBuilder;
  }

  public CareConnectPatient build(PatientEntity patientEntity,
      StorageService storageService) {

    CareConnectPatient patient = new CareConnectPatient();
    addNHSId(patient, patientEntity);
    addName(patient, patientEntity);
    addAddress(patient, patientEntity);
    addTelecom(patient, patientEntity);

    String gender = patientEntity.getGender();
    if (StringUtils.isNotEmpty(gender)) {
      patient.setGender(Enumerations.AdministrativeGender.fromCode(gender));
    }
    patient.setBirthDate(patientEntity.getDateOfBirth());
    addLanguage(patient, patientEntity);

    addGP(patient, patientEntity, storageService);
    addPharmacy(patient, patientEntity, storageService);

    return patient;
  }

  private void addGP(CareConnectPatient patient, PatientEntity patientEntity,
      StorageService storageService) {
    // TODO add to entity
    CareConnectOrganization gp = careConnectOrganizationBuilder.build(patientEntity);
    var practitioner = careConnectPractitionerBuilder.build(gp);
    patient.addGeneralPractitioner(new Reference(storageService.storeExternal(practitioner)));
  }

  private CareConnectOrganization addPharmacy(CareConnectPatient patient,
      PatientEntity patientEntity, StorageService storageService) {
    // TODO add to entity
    CareConnectOrganization pharmacy = careConnectOrganizationBuilder.build(patientEntity);
    patient.setNominatedPharmacy(new Reference(storageService.storeExternal(pharmacy)));
    return pharmacy;
  }

  private void addTelecom(CareConnectPatient patient,
      PatientEntity patientEntity) {
    // TODO add to entity
    patient.addTelecom()
        .setSystem(ContactPointSystem.PHONE)
        .setValue("01231231234")
        .setUse(ContactPointUse.HOME)
        .setRank(1)
        .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));

    // TODO add to entity
    patient.addTelecom()
        .setSystem(ContactPointSystem.PHONE)
        .setValue("01234567899")
        .setUse(ContactPointUse.MOBILE)
        .setRank(2)
        .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));

    // TODO add email to entity
    patient.addTelecom()
        .setSystem(ContactPointSystem.EMAIL)
        .setValue("exmaple@example.com")
        .setUse(ContactPointUse.HOME)
        .setRank(3)
        .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
  }

  private void addAddress(CareConnectPatient patient, PatientEntity patientEntity) {
    // TODO extend address in entity to full details
    patient.addAddress()
        .setUse(AddressUse.HOME)
        .setType(AddressType.BOTH)
        .addLine(patientEntity.getAddress())
        .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
  }

  private void addLanguage(CareConnectPatient patient, PatientEntity patientEntity) {
    // TODO add to entity
    CodeableConcept language = new CodeableConcept();
    language.addCoding().setCode("en").setDisplay("English")
        .setSystem(
            "http://uecdi-tom-terminology.eu-west-2.elasticbeanstalk.com/fhir/CodeSystem/languages");
    patient.addCommunication(new PatientCommunicationComponent(language));
  }

  private void addNHSId(CareConnectPatient patient, PatientEntity patientEntity) {
    NHSNumberIdentifier nhsIdentifier = new NHSNumberIdentifier();
    nhsIdentifier.setValue(patientEntity.getNhsNumber());
    nhsIdentifier.setSystem(
        "https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1");
    nhsIdentifier.setNhsNumberVerificationStatus(new CodeableConcept().addCoding(new Coding()
        .setSystem(
            "https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-NHSNumberVerificationStatus-1")
        .setDisplay("Number present and verified")
        .setCode("21")));
    nhsIdentifier.setType(new CodeableConcept().addCoding(new Coding()
        .setSystem("http://hl7.org/fhir/ValueSet/identifier-type")
        .setDisplay("Passport number")
        .setCode("PPN")));
    patient.addIdentifier(nhsIdentifier);
  }

  private void addName(CareConnectPatient patient, PatientEntity patientEntity) {
    HumanName name = patient.addName()
        .setFamily(patientEntity.getLastName())
        .addGiven(patientEntity.getFirstName())
        .setUse(NameUse.OFFICIAL);

    if (StringUtils.isNotEmpty(patientEntity.getTitle())) {
      name.addPrefix(patientEntity.getTitle());
    }
  }
}
