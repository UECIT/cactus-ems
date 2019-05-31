package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.springframework.stereotype.Component;

@Component
public class PatientBundleDecorator extends BundleDecorator<CareConnectPatient, CareConnectPatient> {
	
	@Override
	public void decorate(Bundle bundle, CareConnectPatient patient) {
		addToBundle(bundle, patient);
	}
	
}
