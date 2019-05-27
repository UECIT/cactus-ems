package uk.nhs.ctp.service.report.decorator.mapping.template.textsection;

import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Type;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocBr;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocParagraph;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class BundleToIntegratedUrgentCareTextSectionTemplateMapper extends AbstractIntegratedUrgentCareTextSectionTemplateMapper<Bundle> {
	
	@Override
	public void map(Bundle bundle, COCDTP146246GB01Section1 section) {
		StrucDocText text = new StrucDocText();
		
		List<QuestionnaireResponse> questionnaireResponses = 
				ResourceProviderUtils.getResources(bundle, QuestionnaireResponse.class);

		questionnaireResponses.stream().forEach(response -> {
			text.getContent().add(createText(response));
		});
		
		section.setText(text);
	}
	
	@Override
	public Class<Bundle> getResourceClass() {
		return Bundle.class;
	}

	private JAXBElement<StrucDocParagraph> createText(QuestionnaireResponse response) {
		StrucDocParagraph paragraph = new StrucDocParagraph();
		
		response.getItem().stream().forEach(item -> {
			paragraph.getContent().add(item.getText());
			JAXBElement<StrucDocBr> br = new JAXBElement<>(new QName("urn:hl7-org:v3", "br"), StrucDocBr.class, new StrucDocBr());
			paragraph.getContent().add(br);
			Type type = item.getAnswerFirstRep().getValue();

			paragraph.getContent().add(type instanceof Coding ? 
					((Coding)type).getDisplay() : type.primitiveValue());
		});
		
		return new JAXBElement<StrucDocParagraph>(
				new QName("urn:hl7-org:v3", "paragraph"), StrucDocParagraph.class, paragraph);
	}
}
