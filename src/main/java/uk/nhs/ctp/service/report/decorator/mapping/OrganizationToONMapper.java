package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;

@Component
public class OrganizationToONMapper extends AbstractMapper<ON, CareConnectOrganization> {

	@Override
	public ON map(CareConnectOrganization organization) {
		ON on = new ON();
		on.getContent().add(organization.getName());
		//currently no mapping from FHIR organization name use to report name use so just set to (L)egal
		on.getUse().add(CsEntityNameUse.L);
		
		return on;
	}
}
