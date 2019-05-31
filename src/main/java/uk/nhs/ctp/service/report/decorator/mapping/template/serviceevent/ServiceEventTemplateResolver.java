package uk.nhs.ctp.service.report.decorator.mapping.template.serviceevent;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02DocumentationOf;

@Component
public class ServiceEventTemplateResolver<RESOURCE extends IBaseResource> extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02DocumentationOf>{

	public ServiceEventTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02DocumentationOf>> templateMappers) {
		super(templateMappers);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected POCDMT200001GB02DocumentationOf createContainer() {
		POCDMT200001GB02DocumentationOf documentationOf = new POCDMT200001GB02DocumentationOf();
		documentationOf.setTypeCode(documentationOf.getTypeCode());
		
		return documentationOf;
	}

}
