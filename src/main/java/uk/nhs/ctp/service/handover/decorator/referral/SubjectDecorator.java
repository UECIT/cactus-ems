package uk.nhs.ctp.service.handover.decorator.referral;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import resources.CareConnectPatient;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
@Order(value=Ordered.HIGHEST_PRECEDENCE)
public class SubjectDecorator implements ResourceDecorator<ReferralRequest, AuditEntry> {
	
	@Autowired
	private IParser fhirParser;
	
	public void decorate(ReferralRequest referralRequest, AuditEntry auditEntry) {
		
		Parameters parameters = ResourceProviderUtils.getResource(fhirParser.parseResource(
				Bundle.class, auditEntry.getCdssServiceDefinitionRequest()), Parameters.class);
		
		CareConnectPatient patient = (CareConnectPatient)ResourceProviderUtils
				.getParameterByName(parameters.getParameter(), "patient").getResource();
		
		referralRequest.setSubject(new Reference(patient));
		referralRequest.addContained(patient);
	}

}
