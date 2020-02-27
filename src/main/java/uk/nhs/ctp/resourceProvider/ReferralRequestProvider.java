package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import java.util.Collection;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.EncounterService;
import uk.nhs.ctp.service.ReferralRequestService;

@Service
@AllArgsConstructor
public class ReferralRequestProvider implements IResourceProvider {

  private ReferralRequestService referralRequestService;
  private EncounterService encounterService;

  @Read
  public ReferralRequest get(@IdParam IdType id) {
    return referralRequestService.get(id.getIdPartAsLong());
  }

  @Search
  public Collection<ReferralRequest> findByEncounterContext(@RequiredParam(name= ReferralRequest.SP_CONTEXT)
      ReferenceParam contextParam) {

    String resourceType = contextParam.getResourceType();
    if (!resourceType.equals(ResourceType.Encounter.name())) {
      throw new InvalidRequestException("Resource type for 'context' must be 'Encounter'");
    }

    return encounterService
        .getReferralRequestForEncounter(contextParam.getIdPartAsLong()).stream()
        .collect(Collectors.toUnmodifiableList());

  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return ReferralRequest.class;
  }
}
