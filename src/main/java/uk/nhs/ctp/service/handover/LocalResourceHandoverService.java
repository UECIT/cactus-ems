package uk.nhs.ctp.service.handover;

import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Service;

import uk.nhs.ctp.service.dto.HandoverRequestDTO;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Service
public class LocalResourceHandoverService extends HandoverService {

	@Override
	public <T extends Resource> T getResource(HandoverRequestDTO request, Class<T> resourceClass) {
		return ResourceProviderUtils.getResource(request.getResourceBundle(), resourceClass);
	}
}
