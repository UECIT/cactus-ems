package uk.nhs.ctp.service.resolver;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleResponseResolver extends AbstractResponseResolver<Bundle> {

	@Override
	public Class<Bundle> getResourceClass() {
		return Bundle.class;
	}

	@Override
	protected GuidanceResponse extractGuidanceResponse(Resource resource) {
		return ResourceProviderUtils.getResource((Bundle)resource, GuidanceResponse.class);
	}

	@Override
	protected List<Resource> extractResources(Resource resource, CdssSupplier cdssSupplier) {
		return ((Bundle)resource).getEntry().stream().map(entry -> 
				entry.getResource()).filter(r -> 
					ResourceProviderUtils.getResource(r, GuidanceResponse.class) == null)
						.collect(Collectors.toList());
	}

}
