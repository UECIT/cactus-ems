package uk.nhs.ctp.service.builder;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Address.AddressType;
import org.hl7.fhir.dstu3.model.Address.AddressUse;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointUse;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.HumanName.NameUse;
import org.hl7.fhir.dstu3.model.NHSNumberIdentifier;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.ReferenceService;

@Component
@AllArgsConstructor
@Slf4j
public class CareConnectPatientBuilder {

  private final ReferenceService referenceService;

  public CareConnectPatient build(PatientEntity patientEntity) {

    CareConnectPatient patient = new CareConnectPatient();
    patient.setId(referenceService.buildId(ResourceType.Patient, patientEntity.getId()));

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

    addGP(patient, patientEntity);
    addPharmacy(patient, patientEntity);

    return patient;
  }

  private void addGP(CareConnectPatient patient, PatientEntity patientEntity) {
    // TODO add to entity
    patient.addGeneralPractitioner(referenceService.buildRef(ResourceType.Practitioner, 1));
  }

  private void addPharmacy(CareConnectPatient patient,
      PatientEntity patientEntity) {
    // TODO add to entity
    patient.setNominatedPharmacy(referenceService.buildRef(ResourceType.Organization, 1));
  }

  private void addTelecom(CareConnectPatient patient, PatientEntity patientEntity) {
    int rank = 1;
    if (StringUtils.isNotEmpty(patientEntity.getHomePhone())) {
      patient.addTelecom()
          .setSystem(ContactPointSystem.PHONE)
          .setValue(patientEntity.getHomePhone())
          .setUse(ContactPointUse.HOME)
          .setRank(rank++)
          .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
    }

    if (StringUtils.isNotEmpty(patientEntity.getMobile())) {
      patient.addTelecom()
          .setSystem(ContactPointSystem.PHONE)
          .setValue(patientEntity.getMobile())
          .setUse(ContactPointUse.MOBILE)
          .setRank(rank++)
          .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
    }

    if (StringUtils.isNotEmpty(patientEntity.getEmail())) {
      patient.addTelecom()
          .setSystem(ContactPointSystem.EMAIL)
          .setValue(patientEntity.getEmail())
          .setUse(ContactPointUse.HOME)
          .setRank(rank++)
          .setPeriod(new Period().setStart(new Date()).setEnd(new Date()));
    }
  }

  private void addAddress(CareConnectPatient patient, PatientEntity patientEntity) {
    Address address = patient.addAddress()
        .setUse(AddressUse.HOME)
        .setType(AddressType.BOTH)
        .setPeriod(new Period().setStart(new Date()).setEnd(new Date()))
        .setCity(patientEntity.getCity())
        .setPostalCode(patientEntity.getPostalCode());

    if (StringUtils.isNotEmpty(patientEntity.getAddress())) {
      for (String line : patientEntity.getAddress().split("\\s*,\\s*")) {
        address.addLine(line);
      }
    }
  }

  private void addLanguage(CareConnectPatient patient, PatientEntity patientEntity) {
    String languageCode = patientEntity.getLanguage();
    if (StringUtils.isNotEmpty(languageCode)) {
      try {
        CareConnectLanguage language = CareConnectLanguage.valueOf(languageCode);
        patient.addCommunication()
            .setLanguage(language.toCodeableConcept());
      } catch (IllegalArgumentException e) {
        log.warn("Unrecognised language code {}", languageCode);
      }
    }
  }

  private void addNHSId(CareConnectPatient patient, PatientEntity patientEntity) {
    String nhsNumber = patientEntity.getNhsNumber();

    NHSNumberIdentifier nhsIdentifier = new NHSNumberIdentifier();
    nhsIdentifier.setValue(nhsNumber);
    nhsIdentifier.setSystem(
        "https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-NHSNumberVerificationStatus-1");

    if (StringUtils.isNotEmpty(nhsNumber)) {
      nhsIdentifier.setNhsNumberVerificationStatus(new CodeableConcept().addCoding(new Coding()
          .setSystem(
              "https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-NHSNumberVerificationStatus-1")
          .setDisplay("Number present and verified")
          .setCode("01")));
    } else {
      nhsIdentifier.setNhsNumberVerificationStatus(new CodeableConcept().addCoding(new Coding()
          .setSystem(
              "https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-NHSNumberVerificationStatus-1")
          .setDisplay("Number not present and trace not required")
          .setCode("07")));
    }
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
