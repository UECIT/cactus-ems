package uk.nhs.ctp.service.report.decorator.mapping.template;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.RecipientAware;

@Component
public abstract class RecipientChoiceTemplateResolver
		<RESOURCE extends IBaseResource, CONTAINER extends RecipientAware> 
				extends AbstractTemplateResolver<RESOURCE, CONTAINER> {

	public RecipientChoiceTemplateResolver(
			List<TemplateMapper<RESOURCE, CONTAINER>> templateMappers) {
		
		super(templateMappers);
	}
	
	@Override
	protected CONTAINER createContainer() {
		CONTAINER container = getContainer();
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145203GB03#IntendedRecipient");
		container.setContentId(templateContent);
		
		return container;
	}

	protected abstract CONTAINER getContainer();
	
}
