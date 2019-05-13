package uk.nhs.ctp.service.report.decorators;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation7;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TriageDisposition;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02TriageDisposition.Code;

@Component
public class PertinentInformation6TriageDisposition implements AmbulanceDecorator  {

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		// TODO Populate data.
		REPCMT200001GB02PertinentInformation7 triageDisposition = new REPCMT200001GB02PertinentInformation7();
		
		triageDisposition.setTypeCode(triageDisposition.getTypeCode());
		REPCMT200001GB02PertinentInformation7.SeperatableInd seperatableInd = new REPCMT200001GB02PertinentInformation7.SeperatableInd();
		seperatableInd.setValue(false);
		triageDisposition.setSeperatableInd(seperatableInd);
		
		REPCMT200001GB02TriageDisposition pertinentTriageDisposition = new REPCMT200001GB02TriageDisposition();
		
		pertinentTriageDisposition.setClassCode(pertinentTriageDisposition.getClassCode());
		pertinentTriageDisposition.setMoodCode(pertinentTriageDisposition.getMoodCode());
		
		Code code = new Code();
		code.setCode("TD");
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.419");
		pertinentTriageDisposition.setCode(code);
		
		CV codedValue = new CV();
		codedValue.setCode(request.getReferralRequest().getReasonCodeFirstRep().getCodingFirstRep().getCode());
		codedValue.setCodeSystem(request.getReferralRequest().getReasonCodeFirstRep().getCodingFirstRep().getSystem());
		codedValue.setDisplayName(request.getReferralRequest().getReasonReferenceFirstRep().getDisplay());
		pertinentTriageDisposition.setValue(codedValue);
		
		triageDisposition.setPertinentTriageDisposition(pertinentTriageDisposition);
		document.setPertinentInformation6(new JAXBElement<REPCMT200001GB02PertinentInformation7>(new QName("pertinentInformation6"), REPCMT200001GB02PertinentInformation7.class, triageDisposition));
	}

}
