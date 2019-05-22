package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;

@Component
public class InformantChoiceTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Informant> {
		
	@Autowired
	public InformantChoiceTemplateResolver(
		List<TemplateMapper<RESOURCE, POCDMT200001GB02Informant>> templateMappers) {
	
		super(templateMappers);
	}
}
