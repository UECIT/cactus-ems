package uk.nhs.ctp.service.report.decorator.mapping.template.textsection;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocItem;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocList;
import uk.nhs.ctp.service.report.org.hl7.v3.StrucDocText;

@Component
public class ReferralRequestToIntegratedUrgentCareTextSectionTemplateMapper extends AbstractIntegratedUrgentCareTextSectionTemplateMapper<ReferralRequest> {

	@Override
	public void map(ReferralRequest referralRequest, COCDTP146246GB01Section1 section) {
		StrucDocText text = new StrucDocText();
		
		referralRequest.getNote().stream().forEach(note -> {
			StrucDocList list = new StrucDocList();
			StrucDocItem item = new StrucDocItem();
			item.getContent().add(note.getText());
			list.getItem().add(item);

			text.getContent().add(new JAXBElement<StrucDocList>(
					new QName("urn:hl7-org:v3", "list"), StrucDocList.class, list));
		});
		
		section.setTitle("Other Notes");
		section.setText(text);
	}

	@Override
	public Class<ReferralRequest> getResourceClass() {
		return ReferralRequest.class;
	}

}
