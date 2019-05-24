package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.XEncounterParticipant;

@Component
public class ParticipantTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, COCDTP146232GB01EncounterParticipant> {

	@Autowired
	public ParticipantTemplateResolver(
			List<TemplateMapper<RESOURCE, COCDTP146232GB01EncounterParticipant>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected COCDTP146232GB01EncounterParticipant createContainer() {
		COCDTP146232GB01EncounterParticipant encounterParticipant = new COCDTP146232GB01EncounterParticipant();
		encounterParticipant.setTypeCode(XEncounterParticipant.REF);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146232GB01#encounterParticipant");
		encounterParticipant.setTemplateId(templateId);
		
		return encounterParticipant;
	}
}
