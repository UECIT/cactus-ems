package uk.nhs.ctp.transform;

import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import org.thymeleaf.util.StringUtils;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.ReferenceService;
import uk.nhs.ctp.service.dto.PatientDTO;

@Component
@AllArgsConstructor
public class PatientTransformer {

  private static final String NHS_NUMBER_SYSTEM = "https://fhir.nhs.uk/Id/nhs-number";

  private final ReferenceService referenceService;

  public PatientDTO transform(Patient patient) {

    String nhsNumber = patient.getIdentifier().stream()
        .filter(identifier -> NHS_NUMBER_SYSTEM.equals(identifier.getSystem()))
        .findFirst()
        .map(Identifier::getValue)
        .orElse(null);

    PatientDTO patientDTO = new PatientDTO();
    patientDTO.setId(patient.getId());
    patientDTO.setTitle(patient.getNameFirstRep().getPrefixAsSingleString());
    patientDTO.setFirstName(patient.getNameFirstRep().getGivenAsSingleString());
    patientDTO.setLastName(patient.getNameFirstRep().getFamily());
    patientDTO.setDateOfBirth(formatForUi(patient.getBirthDate()));
    patientDTO.setGender(patient.getGender().getDisplay());
    patientDTO.setNhsNumber(nhsNumber);
    //TODO: other fields
    return patientDTO;
  }

  public PatientDTO transform(PatientEntity patient) {
    PatientDTO patientDTO = new PatientDTO();
    patientDTO.setId(referenceService.buildId(ResourceType.Patient, patient.getId()));
    patientDTO.setTitle(patient.getTitle());
    patientDTO.setFirstName(patient.getFirstName());
    patientDTO.setLastName(patient.getLastName());
    patientDTO.setDateOfBirth(formatForUi(patient.getDateOfBirth()));
    patientDTO.setGender(StringUtils.capitalize(patient.getGender()));
    patientDTO.setNhsNumber(patient.getNhsNumber());
    //TODO: other fields
    return patientDTO;
  }

  private String formatForUi(Date date) {
    return new SimpleDateFormat("yyyy-MM-dd")
        .format(date);
  }

}
