package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;

@Component
public class ReferralRequestToSectionMapper extends AbstractSectionMapper<ReferralRequest> {

	@Override
	public void map(ReferralRequest referralRequest, COCDTP146246GB01Section1 section) {
		StrucDocText text = new StrucDocText();
		referralRequest.getNote().stream().forEach(note -> 
				text.getContent().add("<paragraph>" + note.getText() + "</paragraph>"));
		
		section.setText(text);
	}

}
