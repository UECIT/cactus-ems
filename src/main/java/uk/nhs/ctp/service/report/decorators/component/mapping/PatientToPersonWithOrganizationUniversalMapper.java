package uk.nhs.ctp.service.report.decorators.component.mapping;

import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;

@Component
public class PatientToPersonWithOrganizationUniversalMapper 
		extends AbstractEncounterParticipantDataMapper<CareConnectPatient> {

	@Override
	public Class<CareConnectPatient> getResourceClass() {
		return CareConnectPatient.class;
	}

	@Override
	public void map(CareConnectPatient patient, COCDTP146232GB01EncounterParticipant encounterParticipant) {
//		encounterParticipant.setCOCDTP145210GB01AssignedEntity(patientToAssignedEntityMapper.map(patient));
	}
	
	protected String getTemplateName() {
		return "COCD_TP145210GB01#AssignedEntity";
	}
	
}
