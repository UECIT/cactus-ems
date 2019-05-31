package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.springframework.stereotype.Component;

@Component
public class CarePlanBundleDecorator extends BundleDecorator<CareConnectCarePlan, CareConnectCarePlan> {

	@Override
	public void decorate(Bundle documentBundle, CareConnectCarePlan carePlan) {
		addToBundle(documentBundle, carePlan);
		
		addToBundle(documentBundle, buildCareAdvice(
				"Result", "You should follow this advice within the next 12 hour period"));
		addToBundle(documentBundle, buildCareAdvice(
				"Before you go", "take all your current medicines with you"));
		addToBundle(documentBundle, buildCareAdvice(
				"What you can do in the meantime", "Call 999 if you're symptoms are getting worse."));
	}

	private CareConnectCarePlan buildCareAdvice(String title, String description) {
		return (CareConnectCarePlan)new CareConnectCarePlan().setTitle(title).setDescription(description);
	}
}
