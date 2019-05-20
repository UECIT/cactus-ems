package uk.nhs.ctp.service.report.decorator;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;

public interface OneOneOneDecorator {

	void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request);
}
