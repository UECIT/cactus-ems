package uk.nhs.ctp.service.resolver;

import java.util.List;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;

@Component
@AllArgsConstructor
public class ResourceExtractor {

  private BundleResourceExtractor bundleResourceExtractor;
  private GuidanceResponseResourceExtractor guidanceResponseResourceExtractor;

  public GuidanceResponse extractGuidanceResponse(Resource resource) {
    switch (resource.getResourceType()) {
      case Bundle:
        return bundleResourceExtractor.extractGuidanceResponse((Bundle)resource);
      case GuidanceResponse:
        return guidanceResponseResourceExtractor.extractGuidanceResponse((GuidanceResponse)resource);
    }
    throw new UnsupportedOperationException();
  }

  public List<Resource> extractResources(Resource resource, CdssSupplier cdssSupplier) {
    switch (resource.getResourceType()) {
      case Bundle:
        return bundleResourceExtractor.extractResources((Bundle)resource);
      case GuidanceResponse:
        return guidanceResponseResourceExtractor.extractResources((GuidanceResponse)resource, cdssSupplier);
    }
    throw new UnsupportedOperationException();
  }

}
