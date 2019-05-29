package uk.nhs.ctp.service.report.decorator.mapping.template.consent;

import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.Consent;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.CE;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent.StatusCode;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Authorization;

@Component
public class ConsentToConsentTemplateMapper implements TemplateMapper<Consent, POCDMT200001GB02Authorization>{

	private List<String> consentCodes = Arrays.asList(new String[] {"access", "use", "disclose"});
	
	@Override
	public void map(Consent fhirConsent, POCDMT200001GB02Authorization container, ReportRequestDTO request) {
		COCDTP146226GB02Consent consent = new COCDTP146226GB02Consent();
		consent.setClassCode(consent.getClassCode());
		consent.setMoodCode(consent.getMoodCode());
		
		StatusCode statusCode = new StatusCode();
		statusCode.setCode("completed");
		consent.setStatusCode(statusCode);

		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		consent.setTemplateId(templateId);
		
		II id = new II();
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.39");
		id.setExtension("FGHRED");
		id.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST");
		consent.getId().add(id);

		consent.setCode(getConsentCode(fhirConsent.getAction().stream().anyMatch(action -> 
				consentCodes.contains(action.getCodingFirstRep().getCode()))));
		
		container.setCOCDTP146226GB02Consent(consent);
	}

	@Override
	public Class<Consent> getResourceClass() {
		return Consent.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP146226GB02#Consent";
	}
	
	private CE getConsentCode(boolean consentGiven) {
		CE code = new CE();
		
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setCode(consentGiven ? "319951000000105" : "320011000000108");
		code.setDisplayName(consentGiven ? "Consent Given" : "Consent Declined");
		
		return code;
	}

}
