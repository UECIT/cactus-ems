package uk.nhs.ctp.service.report.decorator;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Reason;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Reason.SeperatableInd;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TriageOutcome;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TriageOutcome.Code;

@Component
public class ReasonDocumentDecorator implements AmbulanceDecorator {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		REPCMT200001GB02Reason reason = new REPCMT200001GB02Reason();
		reason.setTypeCode(reason.getTypeCode());
		
		SeperatableInd seperatableInd = new SeperatableInd();
		seperatableInd.setValue(false);
		reason.setSeperatableInd(seperatableInd);
		
		REPCMT200001GB02TriageOutcome triageOutcome = new REPCMT200001GB02TriageOutcome();
		triageOutcome.setClassCode(triageOutcome.getClassCode());
		triageOutcome.setMoodCode(triageOutcome.getMoodCode());
		
		Code code = new Code();
		code.setCode("TO");
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.328");
		triageOutcome.setCode(code);
		
		CV codedValue = new CV();
		codedValue.setCode("G2");
		codedValue.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.329");
		codedValue.setDisplayName("Green 2");
		triageOutcome.setValue(codedValue);
		
		reason.setJustifyingTriageOutcome(triageOutcome);
		
		document.setReason(reason);
	}

}
