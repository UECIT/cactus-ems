package uk.nhs.ctp.service.factory;

import org.hl7.fhir.dstu3.model.Bundle.BundleType;
import org.springframework.stereotype.Component;

@Component
public class DocumentBundleFactory extends BundleFactory {
	
	@Override
	protected BundleType getBundleType() {
		return BundleType.DOCUMENT;
	}
}
