package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.CompositionService;

@Component
@RequiredArgsConstructor
public class CompositionProvider implements IResourceProvider {

  private final CompositionService compositionService;

  @Search
  public List<Composition> getCompositionByEncounter(@RequiredParam(name= Composition.SP_ENCOUNTER)
      ReferenceParam encounterParam) {
    String resourceType = encounterParam.getResourceType();
    if (!resourceType.equals(ResourceType.Encounter.name())) {
      throw new InvalidRequestException("Resource type for 'encounter' must be 'Encounter'");
    }

    return compositionService.getAllByEncounter(encounterParam.getIdPartAsLong());
  }

  @Read
  public Composition get(@IdParam IdType id) {
    return compositionService.get(id.getIdPartAsLong());
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Composition.class;
  }

}
