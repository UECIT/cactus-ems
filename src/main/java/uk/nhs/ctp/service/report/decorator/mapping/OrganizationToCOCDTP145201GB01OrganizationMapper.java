package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;

@Component
public class OrganizationToCOCDTP145201GB01OrganizationMapper
		extends OrganizationToDetailedOrganizationMapper<CV, COCDTP145201GB01Organization> {
	
	@Override
	protected CV createStandardIndustryClassCode() {
		return new CV();
	}

	@Override
	protected COCDTP145201GB01Organization createOrganization() {
		return new COCDTP145201GB01Organization();
	}

	@Override
	protected void addTemplateId(COCDTP145201GB01Organization organization) {
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145201GB01#providerOrganization");
		
		organization.setTemplateId(templateId);
	}
}
