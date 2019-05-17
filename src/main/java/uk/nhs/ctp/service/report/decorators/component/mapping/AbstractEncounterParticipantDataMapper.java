package uk.nhs.ctp.service.report.decorators.component.mapping;

import org.hl7.fhir.dstu3.model.Resource;

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;
import uk.nhs.ctp.service.report.org.hl7.v3.XEncounterParticipant;

public abstract class AbstractEncounterParticipantDataMapper<RESOURCE extends Resource> 
		implements EncounterParticipantDataMapper<RESOURCE> {

	@Override
	public void map(RESOURCE resource, COCDTP146232GB01EncompassingEncounter encompassingEncounter) {

		COCDTP146232GB01EncounterParticipant encounterParticipant = new COCDTP146232GB01EncounterParticipant();
		encounterParticipant.setTypeCode(XEncounterParticipant.REF);
		
		COCDTP146232GB01EncounterParticipant.TemplateId 
				encounterParticipantTemplateId = new COCDTP146232GB01EncounterParticipant.TemplateId();
		encounterParticipantTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		encounterParticipantTemplateId.setExtension("COCD_TP146232GB01#encounterParticipant");
		encounterParticipant.setTemplateId(encounterParticipantTemplateId);
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension(getTemplateName());
		encounterParticipant.setContentId(templateContent);
		
		map(resource, encounterParticipant);
	}
	
	protected abstract void map(RESOURCE resource, COCDTP146232GB01EncounterParticipant encounterParticipant);
	
	protected abstract String getTemplateName();
}
