package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.builder.CareConnectPatientBuilder;

@AllArgsConstructor
@Component
public class PatientProvider implements IResourceProvider {

  private final PatientRepository patientRepository;
  private final CareConnectPatientBuilder patientBuilder;

  @Read
  public CareConnectPatient getPatient(@IdParam IdType id) {
    PatientEntity patientEntity = patientRepository.findById(id.getIdPartAsLong());
    return patientBuilder.build(patientEntity);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Patient.class;
  }
}
