package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145018UK03CustodianOrganizationMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03AssignedCustodian;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Custodian;

@Component
public class CustodianDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private OrganizationToCOCDTP145018UK03CustodianOrganizationMapper organizationToRepresentedCustodianOrganizationMapper;
	
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
		assignedCustodian.setRepresentedCustodianOrganization(
				organizationToRepresentedCustodianOrganizationMapper.map((Organization)request
						.getReferralRequest().getRequester().getOnBehalfOf().getResource()));
		
		custodian.setCOCDTP145018UK03AssignedCustodian(assignedCustodian);
		
		document.setCustodian(custodian);
	}

}
