package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.builder.CareConnectOrganizationBuilder;

@AllArgsConstructor
@Component
public class OrganizationProvider implements IResourceProvider {

  private final CareConnectOrganizationBuilder organizationBuilder;

  @Read
  public CareConnectOrganization getOrganization(@IdParam IdType id) {
    // TODO add to database
    return organizationBuilder.build();
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Organization.class;
  }
}
