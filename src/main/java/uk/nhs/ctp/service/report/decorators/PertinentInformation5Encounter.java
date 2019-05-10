package uk.nhs.ctp.service.report.decorators;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation;

@Component
public class PertinentInformation5Encounter implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02PertinentInformation encounter = document.getPertinentInformation5();
		// TODO Populate data.
		
		document.setPertinentInformation5(encounter);
	}

}
