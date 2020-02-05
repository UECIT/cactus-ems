package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.OrganisationService;

@RequiredArgsConstructor
@Component
public class OrganizationProvider implements IResourceProvider {

  private final OrganisationService organisationService;

  @Read
  public CareConnectOrganization getOrganization(@IdParam IdType id) {
    return organisationService.get(id.getIdPart());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Organization.class;
  }
}
