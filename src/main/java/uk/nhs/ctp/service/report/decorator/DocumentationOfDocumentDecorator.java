package uk.nhs.ctp.service.report.decorator;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146227GB02ServiceEvent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146227GB02ServiceEvent.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02DocumentationOf;

@Component
public class DocumentationOfDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02DocumentationOf documentationOf = new POCDMT200001GB02DocumentationOf();
		documentationOf.setTypeCode(documentationOf.getTypeCode());
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP146227GB02#ServiceEvent");
		documentationOf.setContentId(templateContent);
		
		COCDTP146227GB02ServiceEvent serviceEvent = new COCDTP146227GB02ServiceEvent();
		serviceEvent.setClassCode("OBSSER");
		serviceEvent.setMoodCode(serviceEvent.getMoodCode());
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146227GB02#ServiceEvent");
		serviceEvent.setTemplateId(templateId);
		
		documentationOf.setCOCDTP146227GB02ServiceEvent(serviceEvent);
		document.getDocumentationOf().add(documentationOf);
	}

}
