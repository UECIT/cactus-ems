package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;

@Component
public class PatientToPersonWithOrganizationUniversalTemplateMapper 
		implements TemplateMapper<CareConnectPatient, COCDTP146232GB01EncounterParticipant> {

	@Override
	public void map(CareConnectPatient resource, COCDTP146232GB01EncounterParticipant encounterParticipant, ReportRequestDTO request) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Class<CareConnectPatient> getResourceClass() {
		return CareConnectPatient.class;
	}
	
	@Override
	public String getTemplateName() {
		return "COCD_TP145210GB01#AssignedEntity";
	}
}
