package uk.nhs.ctp.service.report.decorator.mapping;

import java.util.Arrays;
import java.util.List;

import org.hl7.fhir.dstu3.model.Consent;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.CE;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146226GB02Consent.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.II;

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
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		id.setExtension("COCD_TP146226GB02#Consent");
		consent.setTemplateId(templateId);
		
		CE code = new CE();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setCode(fhirConsent.getAction().stream().anyMatch(action -> 
				consentCodes.contains(action.getCodingFirstRep().getCode())) ? 
						"319951000000105" : "320011000000108");
		
		consent.setCode(code);

		return consent;
	}

}
