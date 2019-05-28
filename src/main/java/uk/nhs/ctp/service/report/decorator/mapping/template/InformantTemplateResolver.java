package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.InformantAware;

@Component
public abstract class InformantTemplateResolver
		<RESOURCE extends IBaseResource, CONTAINER extends InformantAware> 
				extends AbstractTemplateResolver<RESOURCE, CONTAINER> {

	public InformantTemplateResolver(
			List<TemplateMapper<RESOURCE, CONTAINER>> templateMappers) {
		
		super(templateMappers);
	}
	
	@Override
	protected CONTAINER createContainer() {
		return getContainer();
	}

	protected abstract CONTAINER getContainer();
	
}
