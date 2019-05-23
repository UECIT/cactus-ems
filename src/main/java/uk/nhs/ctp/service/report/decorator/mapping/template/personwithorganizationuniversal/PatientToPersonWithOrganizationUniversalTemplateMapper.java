package uk.nhs.ctp.service.report.decorator.mapping.template.personwithorganizationuniversal;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;

import resources.CareConnectPatient;
import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.report.org.hl7.v3.AssignedEntityAware;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public abstract class PatientToPersonWithOrganizationUniversalTemplateMapper<CONTAINER extends AssignedEntityAware>
		extends PersonToPersonWithOrganizationUniversalTemplate<CareConnectPatient, CONTAINER> {
	
	@Override
	public Class<CareConnectPatient> getResourceClass() {
		return CareConnectPatient.class;
	}
	
	@Override
	public String getTemplateName() {
		return "COCD_TP145210GB01#AssignedEntity";
	}

	@Override
	protected HumanName getName(CareConnectPatient patient) {
		return patient.getNameFirstRep();
	}

	@Override
	protected List<ContactPoint> getTelecom(CareConnectPatient patient) {
		return patient.getTelecom();
	}

	@Override
	protected Organization getOrganization(CareConnectPatient patient) {
		Practitioner gp = ResourceProviderUtils.getResource(
				patient.getGeneralPractitionerFirstRep().getResource(), CareConnectPractitioner.class);
		
		Organization organization = new Organization();
		organization.setAddress(gp.getAddress());
		organization.setName(gp.getNameFirstRep().getNameAsSingleString());
		
		return organization;
	}

	@Override
	protected Address getAddress(CareConnectPatient patient) {
		return patient.getAddressFirstRep();
	}
}
