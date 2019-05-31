package uk.nhs.ctp.service.report.decorator.mapping.template.role;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145212GB02PersonMapper;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Person;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Workgroup;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145212GB02Workgroup.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
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
		assigningAuthorityName.setNullFlavor(CsNullFlavor.NI);
		workGroup.setId(assigningAuthorityName);
		
		TEL phone = new TEL();
		phone.getUse().add(CsTelecommunicationAddressUse.EC);
		phone.setValue(patient.getTelecomFirstRep().getValue());
		workGroup.getTelecom().add(phone);
		
		COCDTP145212GB02Person assignedPerson = humanNameToPersonMapperMapper.map(patient.getNameFirstRep());
		workGroup.setAssignedPerson(new JAXBElement<COCDTP145212GB02Person>(
				new QName("urn:hl7-org:v3", "assignedPerson"), COCDTP145212GB02Person.class, assignedPerson));
		
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
