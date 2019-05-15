package uk.nhs.ctp.service.report.decorators.organisation.mapping;

import org.hl7.fhir.dstu3.model.Organization;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorators.mapping.AbstractMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;

@Component
public class OrganizationToONMapper extends AbstractMapper<ON, Organization> {

	@Override
	public ON map(Organization organization) {
		ON on = new ON();
		on.getContent().add(organization.getName());
		//currently no mapping from FHIR organization name use to report name use so just set to (L)egal
		on.getUse().add(CsEntityNameUse.L);
		
		return on;
	}
}
