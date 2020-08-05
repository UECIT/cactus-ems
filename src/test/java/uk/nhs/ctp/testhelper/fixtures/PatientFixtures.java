package uk.nhs.ctp.testhelper.fixtures;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.NHSNumberIdentifier;
import org.hl7.fhir.dstu3.model.Patient;

@UtilityClass
public class PatientFixtures {

  public Patient phillipKDick() {
    Date birthDate = Date.from(LocalDate.of(1928, Month.DECEMBER, 16)
        .atStartOfDay()
        .toInstant(ZoneOffset.UTC));
    return new Patient()
        .addAddress(new Address()
            .addLine("3028 Quartz Lane")
            .setCity("Fullerton")
            .setState("California")
            .setCountry("USA"))
        .addName(new HumanName()
            .addGiven("Phillip")
            .addGiven("Kindred")
            .setFamily("Dick"))
        .setBirthDate(birthDate)
        .addIdentifier(new NHSNumberIdentifier().setValue("123456"))
        .setGender(AdministrativeGender.MALE);
  }

  public Patient minimumPatient() {
    return new Patient();
  }

}
