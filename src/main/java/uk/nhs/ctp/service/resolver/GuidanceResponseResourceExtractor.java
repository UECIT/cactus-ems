package uk.nhs.ctp.service.resolver;

import ca.uhn.fhir.context.FhirContext;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
@AllArgsConstructor
public class GuidanceResponseResourceExtractor {

	private FhirContext fhirContext;
	
	public List<Resource> extractResources(GuidanceResponse guidanceResponse, CdssSupplier cdssSupplier) {
		List<Resource> resources = new ArrayList<>();
		
		if (guidanceResponse.hasResult()) {
			String baseUrl = cdssSupplier.getBaseUrl();
			RequestGroup requestGroup = 
					ResourceProviderUtils.getResource(guidanceResponse.getContained(), RequestGroup.class);
			
			requestGroup = requestGroup == null ? ResourceProviderUtils.getResource(fhirContext,
					baseUrl, RequestGroup.class, guidanceResponse.getResult().getReference()) : requestGroup;

			final String requestBaseUrl = requestGroup.getIdElement().getBaseUrl();

			requestGroup.getAction().forEach(child -> {
				try {
					String reference = child.getResource().getReference();
					Class<? extends Resource> resourceClass = ResourceProviderUtils.getResourceType(reference);
					Resource resource = ResourceProviderUtils.getResource(fhirContext, requestBaseUrl, resourceClass, reference);
					resources.add(resource);
				} catch (Exception e) {
				}
			});
		}
		
		if(guidanceResponse.hasOutputParameters()) {
			try {
				Parameters parameters = ResourceProviderUtils.getResource(
						fhirContext, cdssSupplier.getBaseUrl(), Parameters.class, 
							guidanceResponse.getOutputParameters().getReference());
				
				resources.add(parameters);
			} catch (Exception e) {
			}
		}
		
		return resources;
	}

	public GuidanceResponse extractGuidanceResponse(GuidanceResponse resource) {
		return resource;
	}
	
}
