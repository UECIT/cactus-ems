package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.HealthCareFacilityAware;


@Component
public class HealthCareFacilityTemplateResolver
		<RESOURCE extends IBaseResource, CONTAINER extends HealthCareFacilityAware> 
				extends AbstractTemplateResolver<RESOURCE, CONTAINER> {

	public HealthCareFacilityTemplateResolver(
			List<TemplateMapper<RESOURCE, CONTAINER>> templateMappers) {
		
		super(templateMappers);
	}
	
	@Override
	protected CONTAINER createContainer() {
		return null;
	}
	
}