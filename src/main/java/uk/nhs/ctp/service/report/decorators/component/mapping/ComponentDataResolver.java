package uk.nhs.ctp.service.report.decorators.component.mapping;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClassificationSection;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class ComponentDataResolver<RESOURCE extends Resource> {

	@Autowired
	private List<ComponentDataMapper<Observation>> componentDataMappers;
	
	public void resolve(Bundle resourceBundle, POCDMT200001GB02ClassificationSection classificationSection) {
		List<Observation> observationResources = ResourceProviderUtils.getResources(resourceBundle, Observation.class);
		
		Optional<ComponentDataMapper<Observation>> optional = componentDataMappers.stream().filter(
				mapper -> mapper.getResourceClass().equals(Observation.class)).findFirst();
		
		if (optional.isPresent()) {
			observationResources.stream().forEach(
					resource -> optional.get().map(resource, classificationSection));
		}
	}
}
