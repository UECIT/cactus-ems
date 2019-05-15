package uk.nhs.ctp.service.report.decorators.tracker.mapping;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class TrackerDataResolver<RESOURCE extends Resource> {

	@Autowired
	private List<TrackerDataMapper<RESOURCE>> trackerDataMappers;
	
	public void resolve(ReferralRequest referralRequest, POCDMT200001GB02Tracker tracker) {
		Resource practitionerResource = 
				ResourceProviderUtils.getResource(referralRequest.getContained(), CareConnectPractitioner.class);
		
		Optional<TrackerDataMapper<RESOURCE>> optional = trackerDataMappers.stream().filter(
				mapper -> mapper.getResourceClass().equals(practitionerResource.getClass())).findFirst();
		
		if (optional.isPresent()) {
			optional.get().map(optional.get().getResourceClass().cast(practitionerResource), tracker);
		}
	}
}
