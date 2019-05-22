package uk.nhs.ctp.service.report.decorator.mapping.template.personwithorganizationuniversal;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01EncounterParticipant;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;

@Component
public class PatientToInformantWithOrganizationUniversalTemplateMapper 
		extends PatientToPersonWithOrganizationUniversalTemplateMapper<POCDMT200001GB02Informant>{

}
