package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Authorization;

@Component
public class ConsentTemplateResolver<RESOURCE extends IBaseResource> extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Authorization>{

	@Autowired
	public ConsentTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Authorization>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Authorization createContainer() {
		POCDMT200001GB02Authorization authorization = new POCDMT200001GB02Authorization();
		authorization.setTypeCode(authorization.getTypeCode());
		
		return authorization;
	}

}
