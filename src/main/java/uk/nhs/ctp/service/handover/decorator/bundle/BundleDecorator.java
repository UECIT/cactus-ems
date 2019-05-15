package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.handover.decorator.ResourceDecorator;

public abstract class BundleDecorator<SOURCE, TARGET extends Resource> implements ResourceDecorator<Bundle, SOURCE> {

	protected void addToBundle(Bundle bundle, TARGET target) {
		BundleEntryComponent entry = new BundleEntryComponent();
		entry.setResource(target);
		bundle.addEntry(entry);
	}
}
