package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ListResource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.ListService;

@Component
@RequiredArgsConstructor
public class ListProvider implements IResourceProvider {

  private final ListService listService;

  @Read
  public ListResource getList(@IdParam IdType id) {
    return listService.buildFromCase(id.getIdPartAsLong()); // Build list from the case with the same ID.
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return ListResource.class;
  }
}
