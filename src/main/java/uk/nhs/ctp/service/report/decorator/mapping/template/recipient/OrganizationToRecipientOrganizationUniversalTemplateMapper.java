package uk.nhs.ctp.service.report.decorator.mapping.template.recipient;

import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145203GB03OrganizationMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03IntendedRecipient.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.RecipientAware;

@Component
public class OrganizationToRecipientOrganizationUniversalTemplateMapper<CONTAINER extends RecipientAware>
		implements TemplateMapper<CareConnectOrganization, CONTAINER> {

	@Autowired
	private OrganizationToCOCDTP145203GB03OrganizationMapper organizationToOrganizationMapper;
	
	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Override
	public void map(CareConnectOrganization organization, CONTAINER container, ReportRequestDTO request) {

		COCDTP145203GB03IntendedRecipient intendedRecipient = new COCDTP145203GB03IntendedRecipient();
		intendedRecipient.setClassCode(intendedRecipient.getClassCode());
		intendedRecipient.setAddr(addressToADMapper.map(organization.getAddressFirstRep()));
		intendedRecipient.getTelecom().addAll(contactPointToTELMapper.map(organization.getTelecom()));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		intendedRecipient.setTemplateId(templateId);
		
		intendedRecipient.setRepresentedOrganization(organizationToOrganizationMapper.map(organization));
		container.setCOCDTP145203GB03IntendedRecipient(intendedRecipient);
	}

	@Override
	public Class<CareConnectOrganization> getResourceClass() {
		return CareConnectOrganization.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145203GB03#IntendedRecipient";
	}
}
