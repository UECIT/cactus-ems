package uk.nhs.ctp.service.handover.decorator.bundle;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.springframework.stereotype.Component;

@Component
public class CarePlanBundleDecorator extends BundleDecorator<CarePlan, CarePlan> {

	@Override
	public void decorate(Bundle documentBundle, CarePlan carePlan) {
		addToBundle(documentBundle, carePlan);
		
		addToBundle(documentBundle, buildCareAdvice(
				"Result", "You should follow this advice within the next 12 hour period"));
		addToBundle(documentBundle, buildCareAdvice(
				"Before you go", "take all your current medicines with you"));
		addToBundle(documentBundle, buildCareAdvice(
				"What you can do in the meantime", "Call 999 if you're symptoms are getting worse."));
	}

	private CarePlan buildCareAdvice(String title, String description) {
		return new CarePlan().setTitle(title).setDescription(description);
	}
}
