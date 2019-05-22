package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.ClassCodeAware;
import uk.nhs.ctp.service.report.org.hl7.v3.DetailedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;

public abstract class OrganizationToDetailedOrganizationMapper
		<CODE extends CV, ORGANIZATION extends DetailedEntity<ON> & ClassCodeAware<CODE>> 
			extends OrganizationToClassCodeAwareOrganizationMapper<CODE, ORGANIZATION> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	public ORGANIZATION map(Organization organization) {
		ORGANIZATION targetOrganization = super.map(organization);
		targetOrganization.setAddr(addressToADMapper.map(organization.getAddressFirstRep()));
		targetOrganization.getTelecom().addAll(contactPointToTELMapper.map(organization.getTelecom()));

		return targetOrganization;
	}
	
}
