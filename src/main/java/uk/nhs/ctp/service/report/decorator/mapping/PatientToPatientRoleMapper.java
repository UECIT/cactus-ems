package uk.nhs.ctp.service.report.decorator.mapping;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidMandatoryAssignedAuthority;

@Component
public class PatientToPatientRoleMapper extends AbstractMapper<COCDTP145201GB01PatientRole, CareConnectPatient> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private PatientToPatientMapper patientToPatientMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private OrganizationToProviderOrganizationMapper organizationToProviderOrganizationMapper;
	
	@Override
	public COCDTP145201GB01PatientRole map(CareConnectPatient ccPatient) {
		COCDTP145201GB01PatientRole patientRole = new COCDTP145201GB01PatientRole();
		patientRole.setClassCode(patientRole.getClassCode());
		
		IINPfITOidMandatoryAssignedAuthority id = new IINPfITOidMandatoryAssignedAuthority();
		id.setRoot("2.16.840.1.113883.2.1.4.1");
		id.setExtension(ccPatient.getIdentifierFirstRep().getValue());
		id.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST");
		patientRole.getId().add(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145201GB01#PatientRole");
		patientRole.setTemplateId(templateId);
		
		patientRole.getAddr().addAll(addressToADMapper.map(ccPatient.getAddress()));
		patientRole.getTelecom().addAll(contactPointToTELMapper.map(ccPatient.getTelecom()));
		
		patientRole.setPatientPatient(patientToPatientMapper.map(ccPatient));
		
		Practitioner gp = (Practitioner)ccPatient.getGeneralPractitionerFirstRep().getResource();
		Organization organization = new Organization();
		organization.setAddress(gp.getAddress());
		organization.setName(gp.getNameFirstRep().getNameAsSingleString());
		
		patientRole.setProviderOrganization(new JAXBElement<COCDTP145201GB01Organization>(
				new QName("providerOrganization"), COCDTP145201GB01Organization.class, 
						organizationToProviderOrganizationMapper.map(organization)));
		
		return patientRole;
	}
}
