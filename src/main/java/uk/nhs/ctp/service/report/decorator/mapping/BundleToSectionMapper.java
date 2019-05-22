package uk.nhs.ctp.service.report.decorator.mapping;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocBr;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocParagraph;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleToSectionMapper extends AbstractSectionMapper<Bundle> {
	
	@Override
	public void map(Bundle resourceBundle, COCDTP146246GB01Section1 section) {
		List<QuestionnaireResponse> questionnaireResponses = 
				ResourceProviderUtils.getResources(resourceBundle, QuestionnaireResponse.class);
		
		StrucDocText text = new StrucDocText();
		text.setID(UUID.randomUUID().toString());
		
		questionnaireResponses.stream().forEach(response -> {
			text.getContent().add(createText(response));
		});
		
		section.setText(text);
	}

	private JAXBElement<StrucDocParagraph> createText(QuestionnaireResponse response) {
		StrucDocParagraph paragraph = new StrucDocParagraph();
		
		response.getItem().stream().forEach(item -> {
			paragraph.getContent().add(item.getText());
			JAXBElement<StrucDocBr> br = new JAXBElement<>(new QName("br"), StrucDocBr.class, new StrucDocBr());
			paragraph.getContent().add(br);
			paragraph.getContent().add(item.getAnswerFirstRep().getValueCoding().getDisplay());
		});
		
		return new JAXBElement<StrucDocParagraph>(new QName("paragraph"), StrucDocParagraph.class, paragraph);
	}
}
