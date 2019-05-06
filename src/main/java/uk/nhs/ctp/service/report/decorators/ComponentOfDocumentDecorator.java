package uk.nhs.ctp.service.report.decorators;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component;

public class ComponentOfDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Component componentOf = new POCDMT200001GB02Component();
		componentOf.setTypeCode(componentOf.getTypeCode());
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP146232GB01#EncompassingEncounter");
		
		COCDTP146232GB01EncompassingEncounter encompassingEncounter = new COCDTP146232GB01EncompassingEncounter();
		encompassingEncounter.setClassCode(encompassingEncounter.getClassCode());
		encompassingEncounter.setMoodCode(encompassingEncounter.getMoodCode());
		componentOf.setCOCDTP146232GB01EncompassingEncounter(encompassingEncounter);
		
		componentOf.setContentId(templateContent);
		document.setComponentOf(componentOf);
	}

}
