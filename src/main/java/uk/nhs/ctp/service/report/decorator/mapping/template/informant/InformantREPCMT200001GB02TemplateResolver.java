package uk.nhs.ctp.service.report.decorator.mapping.template.informant;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.InformantTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Informant;

@Component
public class InformantREPCMT200001GB02TemplateResolver<RESOURCE extends IBaseResource> 
	extends InformantTemplateResolver<RESOURCE, REPCMT200001GB02Informant>  {
		
	@Autowired
	public InformantREPCMT200001GB02TemplateResolver(
		List<TemplateMapper<RESOURCE, REPCMT200001GB02Informant>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected REPCMT200001GB02Informant getContainer() {
		REPCMT200001GB02Informant informant = new REPCMT200001GB02Informant();
		informant.setTypeCode(informant.getTypeCode());
		informant.getContextControlCode().add("OP");
		
		return informant;
	}
}
