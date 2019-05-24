package uk.nhs.ctp.service.report.decorator.mapping.template.patient;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.AddressToADMapper;
import uk.nhs.ctp.service.report.decorator.mapping.ContactPointToTELMapper;
import uk.nhs.ctp.service.report.decorator.mapping.OrganizationToCOCDTP145201GB01OrganizationMapper;
import uk.nhs.ctp.service.report.decorator.mapping.PatientToPatientMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidMandatoryAssignedAuthority;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02RecordTarget;

@Component
public class PatientToPatientUniversalTemplateMapper implements TemplateMapper<CareConnectPatient, POCDMT200001GB02RecordTarget> {

	@Autowired
	private AddressToADMapper addressToADMapper;
	
	@Autowired
	private PatientToPatientMapper patientToPatientMapper;
	
	@Autowired
	private ContactPointToTELMapper contactPointToTELMapper;
	
	@Autowired
	private OrganizationToCOCDTP145201GB01OrganizationMapper organizationToProviderOrganizationMapper;
	
	@Override
	public void map(CareConnectPatient ccPatient, POCDMT200001GB02RecordTarget container, ReportRequestDTO request) {
		COCDTP145201GB01PatientRole patientRole = new COCDTP145201GB01PatientRole();
		patientRole.setClassCode(patientRole.getClassCode());
		
		IINPfITOidMandatoryAssignedAuthority id = new IINPfITOidMandatoryAssignedAuthority();
		id.setRoot("2.16.840.1.113883.2.1.4.1");
		id.setExtension(ccPatient.getIdentifierFirstRep().getValue());
		id.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST");
		patientRole.getId().add(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
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
		
		container.setCOCDTP145201GB01PatientRole(patientRole);
	}

	@Override
	public Class<CareConnectPatient> getResourceClass() {
		return CareConnectPatient.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145201GB01#PatientRole";
	}
}
