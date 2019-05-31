package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.ContactPoint;
import org.hl7.fhir.dstu3.model.HumanName;

import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant.AbstractPersonWithOrganizationUniversalTemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.AssignedEntityAware;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public abstract class PatientToPersonWithOrganizationUniversalTemplateMapper<CONTAINER extends AssignedEntityAware>
		extends AbstractPersonWithOrganizationUniversalTemplateMapper<CareConnectPatient, CONTAINER> {
	
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
	protected CareConnectOrganization getOrganization(CareConnectPatient patient) {
		CareConnectPractitioner gp = ResourceProviderUtils.getResource(
				patient.getGeneralPractitionerFirstRep().getResource(), CareConnectPractitioner.class);
		
		CareConnectOrganization organization = new CareConnectOrganization();
		organization.setAddress(gp.getAddress());
		organization.setName(gp.getNameFirstRep().getNameAsSingleString());
		
		return organization;
	}

	@Override
	protected Address getAddress(CareConnectPatient patient) {
		return patient.getAddressFirstRep();
	}
}
