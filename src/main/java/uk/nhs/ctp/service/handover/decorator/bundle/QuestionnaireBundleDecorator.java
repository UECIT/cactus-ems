package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.service.handover.decorator.AuditDataDecorator;

@Component
public class QuestionnaireBundleDecorator extends BundleDecorator<Questionnaire, AuditEntry> implements AuditDataDecorator<Questionnaire> {

	@Autowired
	private IParser fhirParser;

	public Questionnaire decorate(Bundle bundle, AuditEntry auditEntry) {
		Questionnaire questionnaire = null;
		
		if (auditEntry.getCdssQuestionnaireResponse() != null) {
			questionnaire = (Questionnaire)fhirParser.parseResource(auditEntry.getCdssQuestionnaireResponse());
			addToBundle(bundle, questionnaire);
		}
		
		return questionnaire;
	}
}
