package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location;

@Component
public class HealthCareFacilityChoiceTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<Location, COCDTP146232GB01Location> {

	@Autowired
	public HealthCareFacilityChoiceTemplateResolver(
		List<TemplateMapper<Location, COCDTP146232GB01Location>> templateMappers) {
	
		super(templateMappers);
	}
}
