package uk.nhs.ctp.service.report.decorators;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03AssignedCustodian;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Custodian;

public class CustodianDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Custodian custodian = new POCDMT200001GB02Custodian();
		custodian.setTypeCode(custodian.getTypeCode());
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145018UK03#AssignedCustodian");
		custodian.setContentId(templateContent);
		
		COCDTP145018UK03AssignedCustodian assignedCustodian = new COCDTP145018UK03AssignedCustodian();
		assignedCustodian.setClassCode(assignedCustodian.getClassCode());
		custodian.setCOCDTP145018UK03AssignedCustodian(assignedCustodian);
		
		document.setCustodian(custodian);
	}

}
