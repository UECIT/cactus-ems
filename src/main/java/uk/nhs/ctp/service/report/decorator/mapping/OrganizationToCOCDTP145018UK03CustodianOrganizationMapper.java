package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03CustodianOrganization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145018UK03CustodianOrganization.TemplateId;

@Component
public class OrganizationToCOCDTP145018UK03CustodianOrganizationMapper 
		extends OrganizationToOrganizationMapper<COCDTP145018UK03CustodianOrganization> {

	@Override
	protected COCDTP145018UK03CustodianOrganization createOrganization() {
		return new COCDTP145018UK03CustodianOrganization();
	}

	@Override
	protected void addTemplateId(COCDTP145018UK03CustodianOrganization organization) {
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145200GB01#representedCustodianOrganization");
		
		organization.setTemplateId(templateId);
	}
}
