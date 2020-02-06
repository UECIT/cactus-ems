package uk.nhs.ctp.transform;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.service.ReferenceService;
import uk.nhs.ctp.service.dto.PatientDTO;

@Component
@AllArgsConstructor
public class PatientTransformer {

  private final ReferenceService referenceService;

  public PatientDTO transform(Patient patient) {
    PatientDTO patientDTO = new PatientDTO();
    patientDTO.setId(patient.getId());
    patientDTO.setTitle(patient.getNameFirstRep().getPrefixAsSingleString());
    patientDTO.setFirstName(patient.getNameFirstRep().getGivenAsSingleString());
    patientDTO.setLastName(patient.getNameFirstRep().getFamily());
    patientDTO.setDateOfBirth(patient.getBirthDate());
    //TODO: other fields
    return patientDTO;
  }

  public PatientDTO transform(PatientEntity patient) {
    PatientDTO patientDTO = new PatientDTO();
    patientDTO.setId(referenceService.buildId(ResourceType.Patient, patient.getId()));
    patientDTO.setTitle(patient.getTitle());
    patientDTO.setFirstName(patient.getFirstName());
    patientDTO.setLastName(patient.getLastName());
    patientDTO.setDateOfBirth(patient.getDateOfBirth());
    //TODO: other fields
    return patientDTO;
  }

}
