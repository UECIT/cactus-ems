package uk.nhs.ctp.service.report.decorator.mapping;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.Consent;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.CE;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.ED;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;

@Component
public class ConsentToConsentMapper extends AbstractMapper<COCDTP146226GB02Consent, Consent>{

	private List<String> consentCodes = Arrays.asList(new String[] {"access", "use", "disclose"});
	
	@Override
	public COCDTP146226GB02Consent map(Consent fhirConsent) {
		COCDTP146226GB02Consent consent = new COCDTP146226GB02Consent();
		consent.setClassCode(consent.getClassCode());
		consent.setMoodCode(consent.getMoodCode());
		consent.setStatusCode(consent.getStatusCode());

		II id = new II();
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.39");
		id.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST");
		consent.getId().add(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146226GB02#Consent");
		consent.setTemplateId(templateId);
	
		consent.setCode(getConsentCode(fhirConsent.getAction().stream().anyMatch(action -> 
				consentCodes.contains(action.getCodingFirstRep().getCode()))));

		return consent;
	}
	
	private CE getConsentCode(boolean consentGiven) {
		CE code = new CE();
		
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setCode(consentGiven ? "319951000000105" : "320011000000108");
		code.setDisplayName(consentGiven ? "Consent Given" : "Consent Declined");
		
		ED ed = new ED();
		TEL tel = new TEL();
		tel.setValue(MessageFormat.format(
				"{0} to share patient data with specified third party", 
					consentGiven ? "Consent given" : "Declined consent"));
		
		ed.setReference(tel);
		code.setOriginalText(ed);
		
		return code;
	}

}
