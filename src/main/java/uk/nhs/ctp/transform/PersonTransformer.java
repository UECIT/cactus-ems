package uk.nhs.ctp.transform;

import static uk.nhs.ctp.SystemConstants.DATE_FORMAT;

import java.text.ParseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.ContactPoint.ContactPointSystem;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Person;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.dto.PersonDTO;

@Component
@AllArgsConstructor
@Slf4j
public class PersonTransformer {

  public Person transform(PersonDTO personDTO) {
    var person = new Person()
        .addName(new HumanName().setFamily(personDTO.getName().split(" ")[1])
            .addGiven(personDTO.getName().split(" ")[0]))
        .addTelecom(new ContactPoint().setSystem(ContactPointSystem.PHONE)
            .setValue(personDTO.getTelecom()))
        .setGender(AdministrativeGender.fromCode(personDTO.getGender()));

    try {
      person.setBirthDate(DATE_FORMAT.parse(personDTO.getBirthDate()));
    } catch (ParseException e) {
      log.error("Unable to parse birth date " + personDTO.getBirthDate(), e);
    }
    return person;
  }
}
