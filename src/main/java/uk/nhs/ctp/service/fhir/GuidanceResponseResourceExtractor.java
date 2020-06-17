package uk.nhs.ctp.service.fhir;

import ca.uhn.fhir.context.FhirContext;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
@AllArgsConstructor
public class GuidanceResponseResourceExtractor {

  private FhirContext fhirContext;
  private ReferenceService referenceService;

  public List<Resource> extractResources(
      GuidanceResponse guidanceResponse, CdssSupplier cdssSupplier) {
    List<Resource> resources = new ArrayList<>();

    if (guidanceResponse.hasResult()) {
      String baseUrl = cdssSupplier.getBaseUrl();
      Reference result = guidanceResponse.getResult();

      referenceService.resolve(baseUrl, result);

      RequestGroup requestGroup;
      if (result.getResource() instanceof RequestGroup) {
        requestGroup = (RequestGroup) result.getResource();
      } else {
        requestGroup = ResourceProviderUtils.getResource(fhirContext,
            result.getReferenceElement().getBaseUrl(), RequestGroup.class, result.getReference());
      }

      var requestBaseUrl = requestGroup.getIdElement().getBaseUrl();
      referenceService.resolveRelative(requestBaseUrl, requestGroup);

      for (var child : requestGroup.getAction()) {
        String reference = child.getResource().getReference();
        String resourceTypeName = new IdType(reference).getResourceType();
        Class<? extends IBaseResource> resourceClass =
            fhirContext.getResourceDefinition(resourceTypeName).getImplementingClass();
        IBaseResource resource = ResourceProviderUtils
            .getResource(fhirContext, requestBaseUrl, resourceClass, reference);
        referenceService.resolveRelative(requestBaseUrl, resource);
        resources.add((Resource) resource);
      }
    }

    if (guidanceResponse.hasOutputParameters()) {
      try {
        Parameters parameters = ResourceProviderUtils.getResource(
            fhirContext, cdssSupplier.getBaseUrl(), Parameters.class,
            guidanceResponse.getOutputParameters().getReference());

//				resources.add(parameters);
        guidanceResponse.getOutputParameters().setResource(parameters);
      } catch (Exception e) {
      }
    }

    return resources;
  }

  public GuidanceResponse extractGuidanceResponse(GuidanceResponse resource) {
    return resource;
  }

}
