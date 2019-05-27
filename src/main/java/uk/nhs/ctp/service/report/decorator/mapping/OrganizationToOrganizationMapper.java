package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.ctp.service.report.org.hl7.v3.ON;

public abstract class OrganizationToOrganizationMapper 
		<ORGANIZATION extends uk.nhs.ctp.service.report.org.hl7.v3.Entity<ON>> {

	@Autowired
	private OrganizationToONMapper organizationToONMapper;
	
	public ORGANIZATION map(Organization organization) {
		ORGANIZATION targetOrganization = createOrganization();
		targetOrganization.setClassCode(targetOrganization.getClassCode());
		targetOrganization.setDeterminerCode(targetOrganization.getDeterminerCode());
		targetOrganization.setName(organizationToONMapper.map(organization));
		
		addIds(targetOrganization);
		
		return targetOrganization;
	}
	
	protected abstract ORGANIZATION createOrganization();
	
	protected abstract void addIds(ORGANIZATION organization);
}
