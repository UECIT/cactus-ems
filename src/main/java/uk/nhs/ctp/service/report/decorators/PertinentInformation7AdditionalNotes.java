package uk.nhs.ctp.service.report.decorators;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation8;

@Component
public class PertinentInformation7AdditionalNotes implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		List<REPCMT200001GB02PertinentInformation8> additionalNotes = document.getPertinentInformation7();
		// TODO Populate data.
		
		document.getPertinentInformation7().addAll(additionalNotes);
	}

}
