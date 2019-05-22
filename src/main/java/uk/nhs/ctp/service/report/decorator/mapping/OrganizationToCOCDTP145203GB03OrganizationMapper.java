package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization.StandardIndustryClassCode;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03Organization.TemplateId;

@Primary
@Component
public class OrganizationToCOCDTP145203GB03OrganizationMapper 
		extends OrganizationToClassCodeAwareOrganizationMapper<StandardIndustryClassCode, COCDTP145203GB03Organization> {

	@Override
	protected StandardIndustryClassCode createStandardIndustryClassCode() {
		return new COCDTP145203GB03Organization.StandardIndustryClassCode();
	}

	@Override
	protected COCDTP145203GB03Organization createOrganization() {
		return new COCDTP145203GB03Organization();
	}

	@Override
	protected void addTemplateId(COCDTP145203GB03Organization organization) {
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145200GB01#representedOrganization");
		
		organization.setTemplateId(templateId);
	}
		
}
