package uk.nhs.ctp.service.report.decorators.author.mapping;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;

@Component
public class AuthorDataResolver<RESOURCE extends Resource> {

	@Autowired
	private List<AuthorDataMapper<?, RESOURCE>> authorDataMappers;
	
	public void resolve(ReferralRequest referralRequest, POCDMT200001GB02Author author) {
		Resource agentResource = (Resource)referralRequest.getRequester().getAgent().getResource();
		
		Optional<AuthorDataMapper<?, RESOURCE>> optional = authorDataMappers.stream().filter(
				mapper -> mapper.getResourceClass().equals(agentResource.getClass())).findFirst();
		
		if (optional.isPresent()) {
			AuthorDataMapper<?, RESOURCE> mapper = optional.get();
			Organization organization = (Organization)referralRequest.getRequester().getOnBehalfOf().getResource();
			Object dataObject = mapper.map(optional.get().getResourceClass().cast(agentResource), organization);
			mapper.mappingFunction().accept(author, dataObject);
		}
	}
}
