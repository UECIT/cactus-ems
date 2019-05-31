package uk.nhs.ctp.service.report.decorator.mapping.template.role;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145212GB02PersonMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Organization.Id;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Workgroup;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Workgroup.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Participant;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;

@Component
public class PatientToWorkgroupUniversalTemplateMapper 
		implements TemplateMapper<CareConnectPatient, POCDMT200001GB02Participant>{

	@Autowired
	private HumanNameToCOCDTP145212GB02PersonMapper humanNameToPersonMapperMapper;
	
	@Override
	public void map(CareConnectPatient patient, POCDMT200001GB02Participant participant, ReportRequestDTO request) {
		COCDTP145212GB02Workgroup workGroup = new COCDTP145212GB02Workgroup();
		workGroup.setClassCode(workGroup.getClassCode());
		
		CVNPfITCodedplain code = new CVNPfITCodedplain();
			code.setCode("01");
			code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.266");
			code.setDisplayName("EMS-Poc");
		workGroup.setCode(code);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		workGroup.setTemplateId(templateId);
		
		IINPfITOidRequiredAssigningAuthorityName assigningAuthorityName = new IINPfITOidRequiredAssigningAuthorityName();
		assigningAuthorityName.setRoot("1.2.826.0.1285.0.2.0.109");
        assigningAuthorityName.setExtension("24400320");
		workGroup.setId(assigningAuthorityName);
		
		TEL phone = new TEL();
		phone.getUse().add(CsTelecommunicationAddressUse.EC);
		phone.setValue(patient.getTelecomFirstRep().getValue());
		workGroup.getTelecom().add(phone);
		
		COCDTP145212GB02Person assignedPerson = humanNameToPersonMapperMapper.map(patient.getNameFirstRep());
		workGroup.setAssignedPerson(new JAXBElement<COCDTP145212GB02Person>(
				new QName("urn:hl7-org:v3", "assignedPerson"), COCDTP145212GB02Person.class, assignedPerson));
		
		COCDTP145212GB02Organization representedOrganization = new COCDTP145212GB02Organization();
        representedOrganization.setClassCode(representedOrganization.getClassCode());
        representedOrganization.setDeterminerCode(representedOrganization.getDeterminerCode());
        
        Id id = new Id();
        id.setRoot("2.16.840.1.113883.2.1.3.2.4.19.2");
        id.setExtension("K83032");
        representedOrganization.setId(id);
        
        ON name = new ON();
        name.getUse().add(CsEntityNameUse.L);
        name.getContent().add("Example Medical Practice");
        representedOrganization.setName(name);
        
        COCDTP145212GB02Organization.TemplateId templateIdOrganization = new COCDTP145212GB02Organization.TemplateId();
        templateIdOrganization.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
        templateIdOrganization.setExtension("COCD_TP145212GB02#representedOrganization");
        representedOrganization.setTemplateId(templateIdOrganization);
        
        workGroup.setRepresentedOrganization(representedOrganization);
		
		participant.setCOCDTP145212GB02Workgroup(workGroup);
	}

	@Override
	public Class<CareConnectPatient> getResourceClass() {
		return CareConnectPatient.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145212GB02#Workgroup";
	}

}
