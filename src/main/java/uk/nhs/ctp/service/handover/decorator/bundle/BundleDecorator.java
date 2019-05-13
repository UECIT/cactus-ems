package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Resource;

public abstract class BundleDecorator<R extends Resource, V> {

	protected void addToBundle(Bundle bundle, R resource) {
		BundleEntryComponent entry = new BundleEntryComponent();
		entry.setResource(resource);
		bundle.addEntry(entry);
	}
	
	public abstract R decorate(Bundle bundle, V dataObject); 
}
