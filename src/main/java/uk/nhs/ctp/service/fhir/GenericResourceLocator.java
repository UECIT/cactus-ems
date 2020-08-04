package uk.nhs.ctp.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@RequiredArgsConstructor
public class GenericResourceLocator implements IResourceLocator {

  private final FhirContext fhirContext;

  @SuppressWarnings("unchecked")
  @Override
  public <T extends IBaseResource> T findResource(IIdType idType) {
    String baseUrl = idType.getBaseUrl();
    return (T) RetryUtils.retry(() -> fhirContext.newRestfulGenericClient(baseUrl)
        .read()
        .resource(idType.getResourceType())
        .withId(idType)
        .execute(),
        baseUrl);
  }
}
