package uk.nhs.ctp.utils;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.dstu3.model.Flag;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;

@Component
public class FlagUtil {
	
	public static Boolean getFlagStatus(ReportRequestDTO request, String flagCode) {
		List<Flag> fhirFlags = ResourceProviderUtils.getResources(request.getReferralRequest().getContained(),
				Flag.class);

		Optional<Flag> optional = fhirFlags.stream()
				.filter(flag -> flag.getCode().getCodingFirstRep().getCode().equalsIgnoreCase(flagCode)).findFirst();
		return optional.isPresent();
	}

}
