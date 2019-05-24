package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.participant;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.RelatedPersonToPersonWithOrganizationUniversalTemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;

@Component
public class RelatedPersonToEncounterParticipantChoicePersonWithOrganizationUniversalTemplateMapper 
		extends RelatedPersonToPersonWithOrganizationUniversalTemplateMapper<COCDTP146232GB01EncounterParticipant> {
}
