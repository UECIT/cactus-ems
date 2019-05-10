package uk.nhs.ctp.service.report.decorators;

import java.util.List;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation10;

@Component
public class PertinentInformation9ClinicalDiscriminator implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		List<REPCMT200001GB02PertinentInformation10> clinicalDiscriminators = document.getPertinentInformation9();
		// TODO Populate data.
		
		document.getPertinentInformation9().addAll(clinicalDiscriminators);
	}

}
