package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;

@Component
public class EncounterParticipantTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, COCDTP146232GB01EncounterParticipant> {

	@Autowired
	public EncounterParticipantTemplateResolver(
			List<TemplateMapper<RESOURCE, COCDTP146232GB01EncounterParticipant>> templateMappers) {
		
		super(templateMappers);
	}
}
