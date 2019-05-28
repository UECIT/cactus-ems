package uk.nhs.ctp.service.report.decorator.mapping.template.informant;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Informant;

@Component
public class RelatedPersonREPCMT200001GB02ToRelatedEntityTemplateMapper 
	extends uk.nhs.ctp.service.report.decorator.mapping.template.RelatedPersonToRelatedEntityTemplateMapper<REPCMT200001GB02Informant>{

}
