package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;
import java.util.Optional;

import org.hl7.fhir.instance.model.api.IBaseResource;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.ContentAware;

public abstract class AbstractTemplateResolver<RESOURCE extends IBaseResource, CONTAINER extends ContentAware> {
	
	private List<TemplateMapper<RESOURCE, CONTAINER>> templateMappers;
	
	public AbstractTemplateResolver(List<TemplateMapper<RESOURCE, CONTAINER>> templateMappers) {
		this.templateMappers = templateMappers;
	}
	
	public CONTAINER resolve(IBaseResource resource, CONTAINER container, ReportRequestDTO request) {
		Optional<TemplateMapper<RESOURCE, CONTAINER>> optional = 
				templateMappers.stream().filter(mapper -> 
					mapper.getResourceClass().equals(resource.getClass())).findFirst();
		
		if (optional.isPresent()) {
			TemplateMapper<RESOURCE, CONTAINER> mapper = optional.get();
			mapper.map(mapper.getResourceClass().cast(resource), container, request);
			
			TemplateContent templateContent = new TemplateContent();
			templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
			templateContent.setExtension(mapper.getTemplateName());
			container.setContentId(templateContent);
		}
		
		return container;
	}
}
