package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.AuditDataDecorator;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class ObservationBundleDecorator extends BundleDecorator<Observation, AuditEntry> implements AuditDataDecorator<Observation> {

	@Autowired
	private IParser fhirParser;

	public Observation decorate(Bundle bundle, AuditEntry auditEntry)  {
		Observation observation = null;
		
		if (auditEntry.getContained() != null) {
			Bundle containedBundle = fhirParser.parseResource(Bundle.class, auditEntry.getContained());
			Parameters parameters = ResourceProviderUtils.getResource(containedBundle, Parameters.class);
			if (parameters != null) {
				observation = (Observation)parameters.getParameterFirstRep().getResource();
				addToBundle(bundle, observation);
			}
		}
		
		return observation;
	}
}
