package uk.nhs.ctp.service.report.decorators.component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorators.OneOneOneDecorator;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01ResponsibleParty;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component;
import uk.nhs.ctp.service.report.org.hl7.v3.XEncounterParticipant;

public class ComponentOfDocumentDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Component componentOf = new POCDMT200001GB02Component();
		componentOf.setTypeCode(componentOf.getTypeCode());
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP146232GB01#EncompassingEncounter");
		componentOf.setContentId(templateContent);
		
		COCDTP146232GB01EncompassingEncounter encompassingEncounter = new COCDTP146232GB01EncompassingEncounter();
		encompassingEncounter.setClassCode(encompassingEncounter.getClassCode());
		encompassingEncounter.setMoodCode(encompassingEncounter.getMoodCode());
		encompassingEncounter.setDischargeDispositionCode(null); //where does this come from??????
		
		CV cv = new CV();
		cv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.326");
		cv.setCode("NHS111Encounter");
		encompassingEncounter.setCode(cv);
		

		
		//put in mapper
//		TemplateContent encounterParticipantContentId = new TemplateContent();
//		encounterParticipantContentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
//		encounterParticipantContentId.setExtension("");
//		encounterParticipant.setContentId(value);
		
//		encounterParticipantDataResolver.resolve
		
		COCDTP146232GB01Location location = new COCDTP146232GB01Location();
		location.setTypeCode(location.getTypeCode());
		
		COCDTP146232GB01Location.TemplateId locationTemplateId = new COCDTP146232GB01Location.TemplateId();
		locationTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		locationTemplateId.setExtension("COCD_TP146232GB01#location");
		location.setTemplateId(locationTemplateId);
		
		//put in mapper
//		TemplateContent encounterParticipantContentId = new TemplateContent();
//		encounterParticipantContentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
//		encounterParticipantContentId.setExtension("");
//		encounterParticipant.setContentId(value);
		
//		somethingToLocationMapper.map
		
		COCDTP146232GB01ResponsibleParty responsibleParty = new COCDTP146232GB01ResponsibleParty();
		responsibleParty.setTypeCode(responsibleParty.getTypeCode());
		
		COCDTP146232GB01ResponsibleParty.TemplateId 
				responsiblePartyTemplateId = new COCDTP146232GB01ResponsibleParty.TemplateId();
		responsiblePartyTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		responsiblePartyTemplateId.setExtension("COCD_TP146232GB01#location");
		responsibleParty.setTemplateId(responsiblePartyTemplateId);
		
		//put in mapper
//		TemplateContent encounterParticipantContentId = new TemplateContent();
//		encounterParticipantContentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
//		encounterParticipantContentId.setExtension("");
//		encounterParticipant.setContentId(value);
		
//		responsiblePartDataResolver.resolve
		
		componentOf.setCOCDTP146232GB01EncompassingEncounter(encompassingEncounter);
		
		document.setComponentOf(componentOf);
	}

}
