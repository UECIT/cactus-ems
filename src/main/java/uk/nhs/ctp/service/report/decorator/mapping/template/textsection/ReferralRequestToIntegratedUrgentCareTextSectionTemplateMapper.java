package uk.nhs.ctp.service.report.decorator.mapping.template.textsection;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocParagraph;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;

@Component
public class ReferralRequestToIntegratedUrgentCareTextSectionTemplateMapper extends AbstractIntegratedUrgentCareTextSectionTemplateMapper<ReferralRequest> {

	@Override
	public void map(ReferralRequest referralRequest, COCDTP146246GB01Section1 section) {
		StrucDocText text = new StrucDocText();
		
		referralRequest.getNote().stream().forEach(note -> {
			StrucDocParagraph paragraph = new StrucDocParagraph();
			paragraph.getContent().add(note.getText());
			text.getContent().add(new JAXBElement<StrucDocParagraph>(
					new QName("urn:hl7-org:v3", "paragraph"), StrucDocParagraph.class, paragraph));
		});
		
		section.setText(text);
	}

	@Override
	public Class<ReferralRequest> getResourceClass() {
		return ReferralRequest.class;
	}

}