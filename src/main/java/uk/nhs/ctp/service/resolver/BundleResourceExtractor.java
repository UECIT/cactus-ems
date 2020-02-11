package uk.nhs.ctp.service.resolver;

import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleResourceExtractor {

	public GuidanceResponse extractGuidanceResponse(Bundle resource) {
		return ResourceProviderUtils.getResource(resource, GuidanceResponse.class);
	}

	public List<Resource> extractResources(Bundle resource) {
		return resource.getEntry().stream()
				.map(BundleEntryComponent::getResource)
				.filter(r -> !(r instanceof GuidanceResponse))
				.collect(Collectors.toList());
	}

}
