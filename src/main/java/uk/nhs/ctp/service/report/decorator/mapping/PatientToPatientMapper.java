package uk.nhs.ctp.service.report.decorator.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Patient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Patient.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;

@Component
public class PatientToPatientMapper extends AbstractMapper<COCDTP145201GB01Patient, CareConnectPatient> {

	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codingMapper;
	
	@Autowired
	private HumanNameToPNMapper humanNameToPNMapper;
	
	@Autowired
	private PatientCommunicationComponentToCOCDTP145201GB01LanguageCommunicationMapper patientCommunicationToLanguageCommunicationMapper;
	
	@Value("${ems.terminology.administrative.gender.system}")
	private String administrativeGenderSystem;
	
	@Override
	public COCDTP145201GB01Patient map(CareConnectPatient ccPatient) {
		COCDTP145201GB01Patient patient = new COCDTP145201GB01Patient();
		patient.setClassCode(patient.getClassCode());
		patient.setDeterminerCode(patient.getDeterminerCode());
		patient.getName().add(humanNameToPNMapper.map(ccPatient.getNameFirstRep()));
		
		TS birthTime = new TS();
		birthTime.setValue(ccPatient.getBirthDate().toString());
		patient.setBirthTime(birthTime);
		
		patient.setAdministrativeGenderCode(codingMapper.map(
				ccPatient.getGender().getSystem(), administrativeGenderSystem, ccPatient.getGender().getDefinition()));
		
		patient.getLanguageCommunication().add(
				patientCommunicationToLanguageCommunicationMapper.map(ccPatient.getCommunicationFirstRep()));
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145201GB01#patientPatient");
		patient.setTemplateId(templateId);
		
		return patient;
	}

	
}
