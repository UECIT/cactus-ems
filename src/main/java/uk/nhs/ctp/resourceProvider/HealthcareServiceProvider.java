package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.HealthcareServiceService;

@AllArgsConstructor
@Component
public class HealthcareServiceProvider implements IResourceProvider {

  private HealthcareServiceService healthcareServiceService;

  @Read
  public HealthcareService get(@IdParam IdType id) {
    return healthcareServiceService.get(id);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return HealthcareService.class;
  }
}
