package uk.nhs.ctp.service.report.decorators;

import javax.xml.bind.JAXBElement;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation9;

@Component
public class PertinentInformation8PermissionToView implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		JAXBElement<REPCMT200001GB02PertinentInformation9> permissionToView = document.getPertinentInformation8();
		// TODO Populate data.
		
		document.setPertinentInformation8(permissionToView);
	}

}
