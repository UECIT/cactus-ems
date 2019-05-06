package uk.nhs.ctp.service.report.decorators;

import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Patient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02RecordTarget;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class RecordTargetDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02RecordTarget recordTarget = new POCDMT200001GB02RecordTarget();
		recordTarget.setTypeCode(recordTarget.getTypeCode());
		recordTarget.getContextControlCode().add("OP");
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145201GB01#PatientRole");
		recordTarget.setContentId(templateContent);
		
		// set PatientRole
		COCDTP145201GB01PatientRole patientRole = new COCDTP145201GB01PatientRole();
		patientRole.setClassCode(patientRole.getClassCode());
		
		// build patient
		COCDTP145201GB01Patient patient = new COCDTP145201GB01Patient();
		patient.setClassCode(patient.getClassCode());
		patient.setDeterminerCode(patient.getDeterminerCode());
		Patient fhirPatient = ResourceProviderUtils.getResource(request.getBundle(), Patient.class);
		
		PN patientName = new PN();
		patientName.getContent().add(fhirPatient.getNameFirstRep().getNameAsSingleString());
		patientName.getUse().add(CsEntityNameUse.L);
		patient.getName().add(patientName);
		
		TS birthTime = new TS();
		birthTime.setValue(fhirPatient.getBirthDate().toString());
		patient.setBirthTime(birthTime);
		
		CVNPfITCodedplainRequired administrativeGenderCode = new CVNPfITCodedplainRequired();
		administrativeGenderCode.setCode(fhirPatient.getGender().getDefinition());
		administrativeGenderCode.setCodeSystem(fhirPatient.getGender().getSystem());
		administrativeGenderCode.setDisplayName(fhirPatient.getGender().getDisplay());
		patient.setAdministrativeGenderCode(administrativeGenderCode);
		
		patientRole.setPatientPatient(patient);
		recordTarget.setCOCDTP145201GB01PatientRole(patientRole);
		document.setRecordTarget(recordTarget);
	}

}
