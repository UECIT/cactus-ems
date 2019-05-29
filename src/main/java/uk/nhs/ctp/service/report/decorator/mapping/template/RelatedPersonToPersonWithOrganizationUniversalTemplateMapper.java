package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;

import resources.CareConnectOrganization;
import resources.CareConnectRelatedPerson;
import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant.AbstractPersonWithOrganizationUniversalTemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.AssignedEntityAware;

public abstract class RelatedPersonToPersonWithOrganizationUniversalTemplateMapper<CONTAINER extends AssignedEntityAware> 
		extends AbstractPersonWithOrganizationUniversalTemplateMapper<CareConnectRelatedPerson, CONTAINER> {

	@Override
	public Class<CareConnectRelatedPerson> getResourceClass() {
		return CareConnectRelatedPerson.class;
	}
	
	@Override
	public String getTemplateName() {
		return "COCD_TP145210GB01#AssignedEntity";
	}
	
	@Override
	protected HumanName getName(CareConnectRelatedPerson relatedPerson) {
		return relatedPerson.getNameFirstRep();
	}

	@Override
	protected List<ContactPoint> getTelecom(CareConnectRelatedPerson relatedPerson) {
		return relatedPerson.getTelecom();
	}

	@Override
	protected CareConnectOrganization getOrganization(CareConnectRelatedPerson relatedPerson) {
		CareConnectOrganization organization = new CareConnectOrganization();
		organization.setAddress(relatedPerson.getAddress());
		organization.setName(relatedPerson.getNameFirstRep().getNameAsSingleString());
		
		return organization;
	}

	@Override
	protected Address getAddress(CareConnectRelatedPerson relatedPerson) {
		return relatedPerson.getAddressFirstRep();
	}
}
