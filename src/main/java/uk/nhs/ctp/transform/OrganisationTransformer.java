package uk.nhs.ctp.transform;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.Organisation;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@RequiredArgsConstructor
public class OrganisationTransformer implements Transformer<Organisation, CareConnectOrganization> {

  private final ReferenceService referenceService;
  private final IdentifierTransformer identifierTransformer;

  @Override
  public CareConnectOrganization transform(Organisation from) {
    var organisation = new CareConnectOrganization();

    organisation.setId(referenceService.buildId(ResourceType.Organization, from.getId()));
    organisation.setName(from.getName());

    // calling "organisation::addIdentifier" specifically as it's the only one
    // implemented by the CareConnectOrganization profile for now
    identifierTransformer.transform(from.getIdentifiers()).forEach(organisation::addIdentifier);

    return organisation;
  }
}
