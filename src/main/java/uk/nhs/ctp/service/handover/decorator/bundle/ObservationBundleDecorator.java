package uk.nhs.ctp.service.handover.decorator.bundle;

import java.util.Optional;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class ObservationBundleDecorator extends BundleDecorator<AuditEntry, CareConnectObservation> {

	@Autowired
	private IParser fhirParser;

	public void decorate(Bundle bundle, AuditEntry auditEntry)  {
		if (auditEntry.getContained() != null) {
			Bundle containedBundle = fhirParser.parseResource(Bundle.class, auditEntry.getContained());
			Parameters parameters = ResourceProviderUtils.getResource(containedBundle, Parameters.class);
			if (parameters != null) {
				Optional<ParametersParameterComponent> optional = parameters.getParameter().stream().filter(param -> 
						param.getResource().getClass().equals(CareConnectObservation.class)).findFirst();
				
				if (optional.isPresent()) addToBundle(bundle, (CareConnectObservation)optional.get().getResource());
			}
		}
	}
}
