package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import resources.CareConnectPatient;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class PatientBundleDecorator extends BundleDecorator<CareConnectPatient, AuditEntry> {

	@Autowired
	private IParser fhirParser;
	
	public CareConnectPatient decorate(Bundle bundle, AuditEntry auditEntry) {
		Parameters parameters = ResourceProviderUtils.getResource(fhirParser.parseResource(
				Bundle.class, auditEntry.getCdssServiceDefinitionRequest()), Parameters.class);
		
		CareConnectPatient patient = (CareConnectPatient)ResourceProviderUtils
				.getParameterByName(parameters.getParameter(), "patient").getResource();
		
		addToBundle(bundle, patient);
		
		return patient;
	}
}
