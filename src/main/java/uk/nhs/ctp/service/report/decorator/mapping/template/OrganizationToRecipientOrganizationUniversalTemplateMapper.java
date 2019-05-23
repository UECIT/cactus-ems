package uk.nhs.ctp.service.report.decorator.mapping.template;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02PrimaryInformationRecipient;

public class OrganizationToRecipientOrganizationUniversalTemplateMapper
		implements TemplateMapper<CareConnectOrganization, POCDMT200001GB02PrimaryInformationRecipient>{

	@Override
	public void map(CareConnectOrganization resource, POCDMT200001GB02PrimaryInformationRecipient container,
			ReportRequestDTO request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Class<CareConnectOrganization> getResourceClass() {
		return CareConnectOrganization.class;
	}

	@Override
	public String getTemplateName() {
		return "";
	}

}
