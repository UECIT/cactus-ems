package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.RelatedPerson;

import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant.AbstractPersonWithOrganizationUniversalTemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.AssignedEntityAware;

public abstract class RelatedPersonToPersonWithOrganizationUniversalTemplateMapper<CONTAINER extends AssignedEntityAware> 
		extends AbstractPersonWithOrganizationUniversalTemplateMapper<RelatedPerson, CONTAINER> {

	@Override
	public Class<RelatedPerson> getResourceClass() {
		return RelatedPerson.class;
	}
	
	@Override
	public String getTemplateName() {
		return "COCD_TP145210GB01#AssignedEntity";
	}
	
	@Override
	protected HumanName getName(RelatedPerson relatedPerson) {
		return relatedPerson.getNameFirstRep();
	}

	@Override
	protected List<ContactPoint> getTelecom(RelatedPerson relatedPerson) {
		return relatedPerson.getTelecom();
	}

	@Override
	protected Organization getOrganization(RelatedPerson relatedPerson) {
		Organization organization = new Organization();
		organization.setAddress(relatedPerson.getAddress());
		organization.setName(relatedPerson.getNameFirstRep().getNameAsSingleString());
		
		return organization;
	}

	@Override
	protected Address getAddress(RelatedPerson relatedPerson) {
		return relatedPerson.getAddressFirstRep();
	}
}
