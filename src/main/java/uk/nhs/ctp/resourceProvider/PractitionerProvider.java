package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.PractitionerService;

@AllArgsConstructor
@Component
public class PractitionerProvider implements IResourceProvider {

  private final PractitionerService practitionerService;

  @Read
  public CareConnectPractitioner getPractitioner(@IdParam IdType id) {
    return practitionerService.get(id);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Practitioner.class;
  }
}
