package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145203GB03OrganizationMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145203GB03IntendedRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

@Component
public class PractitionerToRecipientOrganizationUniversalDataMapper
		implements TemplateMapper<CareConnectPractitioner, POCDMT200001GB02Tracker> {

	@Autowired
	private OrganizationToCOCDTP145203GB03OrganizationMapper organizationToRepresentedOrganizationMapper;
	
	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Override
	public Class<CareConnectPractitioner> getResourceClass() {
		return CareConnectPractitioner.class;
	}

	@Override
	public void map(CareConnectPractitioner practitioner, POCDMT200001GB02Tracker tracker, ReportRequestDTO request) {
		COCDTP145203GB03IntendedRecipient intendedRecipient = new COCDTP145203GB03IntendedRecipient();
		
		Organization organization = new Organization();
		organization.setAddress(practitioner.getAddress());
		organization.setName(practitioner.getNameFirstRep().getNameAsSingleString());
		
		intendedRecipient.setRepresentedOrganization(organizationToRepresentedOrganizationMapper.map(organization));
		intendedRecipient.setClassCode(intendedRecipient.getClassCode());
		intendedRecipient.setAddr(addressToADMapper.map(practitioner.getAddressFirstRep()));
		intendedRecipient.getTelecom().addAll(contactPointToTELMapper.map(practitioner.getTelecom()));
		
		tracker.setCOCDTP145203GB03IntendedRecipient(intendedRecipient);
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145203GB03#IntendedRecipient";
	}
}
