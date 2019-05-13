package uk.nhs.ctp.service.factory;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleType;

public abstract class BundleFactory {
	
	public Bundle create(String id, boolean contained) {
		Bundle bundle = new Bundle();
		bundle.setType(getBundleType());
		bundle.setId(contained ? "#" : "" + id);
		
		return bundle;
	}
	
	protected abstract BundleType getBundleType();
}
