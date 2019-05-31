package uk.nhs.ctp.service.report.decorator.mapping.template.custodian;

import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145018UK03CustodianOrganizationMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03AssignedCustodian;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03AssignedCustodian.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Custodian;

@Component
public class OrganizationToCustodianOrganizationUniversalTemplateMapper 
		implements TemplateMapper<CareConnectOrganization, POCDMT200001GB02Custodian> {
	
	@Autowired
	private OrganizationToCOCDTP145018UK03CustodianOrganizationMapper organizationToOrganizationMapper;
	
	@Override
	public void map(CareConnectOrganization organization, POCDMT200001GB02Custodian custodian, ReportRequestDTO request) {
		COCDTP145018UK03AssignedCustodian assignedCustodian = new COCDTP145018UK03AssignedCustodian();
		assignedCustodian.setClassCode(assignedCustodian.getClassCode());
		assignedCustodian.setRepresentedCustodianOrganization(organizationToOrganizationMapper.map(organization));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		assignedCustodian.setTemplateId(templateId);
		
		custodian.setCOCDTP145018UK03AssignedCustodian(assignedCustodian);
	}

	@Override
	public Class<CareConnectOrganization> getResourceClass() {
		return CareConnectOrganization.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145018UK03#AssignedCustodian";
	}
}
