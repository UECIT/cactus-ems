package uk.nhs.ctp.service.report.decorator;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;

public interface AmbulanceDecorator {

	void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request);
}
