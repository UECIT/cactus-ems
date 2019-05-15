package uk.nhs.ctp.service.report.decorators.tracker.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.report.decorators.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorators.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorators.organisation.mapping.OrganizationToRepresentedOrganizationMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

@Component
public class PractitionerToRecipientOrganizationUniversalDataMapper
		implements TrackerDataMapper<CareConnectPractitioner> {

	@Autowired
	private OrganizationToRepresentedOrganizationMapper organizationToRepresentedOrganizationMapper;
	
	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Override
	public Class<CareConnectPractitioner> getResourceClass() {
		return CareConnectPractitioner.class;
	}

	@Override
	public void map(CareConnectPractitioner practitioner, POCDMT200001GB02Tracker tracker) {
		COCDTP145203GB03IntendedRecipient intendedRecipient = new COCDTP145203GB03IntendedRecipient();
		
		Organization organization = new Organization();
		organization.setAddress(practitioner.getAddress());
		organization.setName(practitioner.getNameFirstRep().getNameAsSingleString());
		
		intendedRecipient.setRepresentedOrganization(organizationToRepresentedOrganizationMapper.map(organization));
		intendedRecipient.setClassCode(intendedRecipient.getClassCode());
		intendedRecipient.setAddr(addressToADMapper.map(practitioner.getAddressFirstRep()));
		intendedRecipient.getTelecom().addAll(contactPointToTELMapper.map(practitioner.getTelecom()));
		
		tracker.setCOCDTP145203GB03IntendedRecipient(intendedRecipient);
		tracker.setContentId(getTemplateContent());
	}
	
	public TemplateContent getTemplateContent() {
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145203GB03#IntendedRecipient");
		
		return templateContent;
	}
}
