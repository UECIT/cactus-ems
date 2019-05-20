package uk.nhs.ctp.service.report.decorator;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;

@Component
public class InformantDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Informant informant = new POCDMT200001GB02Informant();
		informant.setTypeCode(informant.getTypeCode());
		informant.getContextControlCode().add("OP");
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145210GB01#AssignedEntity");
		informant.setContentId(templateContent);
		
		COCDTP145210GB01AssignedEntity assignedEntity = new COCDTP145210GB01AssignedEntity();
		assignedEntity.setClassCode(assignedEntity.getClassCode());
		informant.setCOCDTP145210GB01AssignedEntity(assignedEntity);
		
		document.getInformant().add(informant);
	}

}
