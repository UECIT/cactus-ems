package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class QuestionnaireResponseBundleDecorator extends BundleDecorator<AuditEntry, QuestionnaireResponse> {

	@Autowired
	private IParser fhirParser;

	public void decorate(Bundle bundle, AuditEntry auditEntry) {
		
		if (auditEntry.getCdssServiceDefinitionRequest() != null) {
			Bundle containedBundle = fhirParser.parseResource(Bundle.class, auditEntry.getCdssServiceDefinitionRequest());
			Parameters parameters = ResourceProviderUtils.getResource(containedBundle, Parameters.class);
			parameters.getParameter().stream().forEach(param -> {
				if(param.hasResource() && param.getResource().getResourceType().equals(ResourceType.QuestionnaireResponse)) {
					addToBundle(bundle, (QuestionnaireResponse) param.getResource());
				}
			});
		}
	}
}
