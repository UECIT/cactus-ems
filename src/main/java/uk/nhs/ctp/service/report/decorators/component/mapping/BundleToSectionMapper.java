package uk.nhs.ctp.service.report.decorators.component.mapping;

import java.util.List;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleToSectionMapper extends AbstractSectionMapper<Bundle> {
	
	@Override
	public void map(Bundle resourceBundle, COCDTP146246GB01Section1 section) {
		List<QuestionnaireResponse> questionnaireResponses = 
				ResourceProviderUtils.getResources(resourceBundle, QuestionnaireResponse.class);
		
		StrucDocText text = new StrucDocText();
		text.setID("13123123123");
		
		questionnaireResponses.stream().forEach(response -> {
			response.getItem().stream().forEach(item -> {
				text.getContent().add(item.getText() + " - " + item.getAnswerFirstRep().getValue().toString());
			});
		});
		
		section.setText(text);
	}

}
