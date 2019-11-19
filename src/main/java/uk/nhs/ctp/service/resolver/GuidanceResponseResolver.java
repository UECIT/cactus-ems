package uk.nhs.ctp.service.resolver;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class GuidanceResponseResolver extends AbstractResponseResolver<GuidanceResponse> {

	@Autowired
	private FhirContext fhirContext;
	
	@Override
	public Class<GuidanceResponse> getResourceClass() {
		return GuidanceResponse.class;
	}

	@Override
	protected List<Resource> extractResources(Resource r, CdssSupplier cdssSupplier) {
		GuidanceResponse guidanceResponse = (GuidanceResponse)r;
		List<Resource> resources = new ArrayList<>();
		
		if (guidanceResponse.hasResult()) {
			String baseUrl = cdssSupplier.getBaseUrl();
			RequestGroup requestGroup = 
					ResourceProviderUtils.getResource(guidanceResponse.getContained(), RequestGroup.class);
			
			requestGroup = requestGroup == null ? ResourceProviderUtils.getResource(fhirContext,
					baseUrl, RequestGroup.class, guidanceResponse.getResult().getReference()) : requestGroup;

			final String requestBaseUrl = requestGroup.getIdElement().getBaseUrl();

			requestGroup.getAction().stream().forEach(child -> {
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

	@Override
	protected GuidanceResponse extractGuidanceResponse(Resource resource) {
		return (GuidanceResponse)resource;
	}
	
}
