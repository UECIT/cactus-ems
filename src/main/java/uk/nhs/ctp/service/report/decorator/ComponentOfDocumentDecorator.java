package uk.nhs.ctp.service.report.decorator;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CareConnectEncounter;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.location.HealthCareFacilityCOCDTP146232GB01TemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant.ParticipantTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.responsibleparty.ResponsiblePartyTemplateResolver;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncompassingEncounter.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01ResponsibleParty;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidMandatoryAssignedAuthority;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class ComponentOfDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private ParticipantTemplateResolver<? extends IBaseResource> participantTemplateResolver;
	
	@Autowired
	private HealthCareFacilityCOCDTP146232GB01TemplateResolver<? extends IBaseResource> healthCareFacilityTemplateResolver;
	
	@Autowired
	private ResponsiblePartyTemplateResolver<? extends IBaseResource> responsiblePartyTemplateResolver;
	
	@Autowired
	private Generex uuidGenerator;
	
	@Autowired
	private SimpleDateFormat reportDateFormat;
	
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
		
		IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(reportDateFormat.format(new Date()));
        encompassingEncounter.setEffectiveTime(effectiveTime);
		
		CV cv1 = new CV();
		cv1.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		cv1.setCode("F");
		cv1.setDisplayName("TEST");
		encompassingEncounter.setDischargeDispositionCode(cv1); //where does this come from??????
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146232GB01#EncompassingEncounter");
		encompassingEncounter.setTemplateId(templateId);
		
		IINPfITOidMandatoryAssignedAuthority id = new IINPfITOidMandatoryAssignedAuthority();
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.49");
		id.setExtension(uuidGenerator.random());
		encompassingEncounter.getId().add(id);
		
		IINPfITOidMandatoryAssignedAuthority caseId = new IINPfITOidMandatoryAssignedAuthority();
		caseId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.34");
		caseId.setExtension(String.valueOf(request.getCaseId()));
		encompassingEncounter.getId().add(caseId);
		
		CV cv = new CV();
		cv.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.326");
		cv.setCode("NHS111Encounter");
		encompassingEncounter.setCode(cv);
		
		Bundle resourceBundle = ResourceProviderUtils.getResource(
				request.getReferralRequest().getContained(), Bundle.class);
		
		Composition composition = ResourceProviderUtils.getResource(resourceBundle, Composition.class);
		var encounter = (CareConnectEncounter) composition.getEncounter().getResource();
		
		COCDTP146232GB01EncounterParticipant encounterParticipant = 
				participantTemplateResolver.resolve(encounter.getSubject().getResource(), request);
		
		if (encounterParticipant != null)
			encompassingEncounter.getEncounterParticipant().add(encounterParticipant);
		
		encounter.getParticipant().stream()
				.map(component -> component.getIndividual().getResource())
				.forEach(resource -> {
					COCDTP146232GB01EncounterParticipant individualParticipant = 
							participantTemplateResolver.resolve(resource, request);
					
					if (individualParticipant != null)
						encompassingEncounter.getEncounterParticipant().add(individualParticipant);
				});

		CareConnectLocation fhirLocation = 
				ResourceProviderUtils.getResource(composition.getContained(), CareConnectLocation.class);
		
		COCDTP146232GB01Location location = healthCareFacilityTemplateResolver.resolve(fhirLocation, request);
		encompassingEncounter.setLocation(location);
		
		COCDTP146232GB01ResponsibleParty responsibleParty = responsiblePartyTemplateResolver.resolve(
				request.getReferralRequest().getRequester().getAgent().getResource(), request);
		
		encompassingEncounter.setResponsibleParty(new JAXBElement<>(
				new QName("urn:hl7-org:v3", "responsibleParty"), COCDTP146232GB01ResponsibleParty.class, responsibleParty));
		
		componentOf.setCOCDTP146232GB01EncompassingEncounter(encompassingEncounter);
		
		document.setComponentOf(componentOf);
	}

}
