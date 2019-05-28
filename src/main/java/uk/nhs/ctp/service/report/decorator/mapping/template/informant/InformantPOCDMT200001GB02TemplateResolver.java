package uk.nhs.ctp.service.report.decorator.mapping.template.informant;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.InformantTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;

@Component
public class InformantPOCDMT200001GB02TemplateResolver<RESOURCE extends IBaseResource> 
		extends InformantTemplateResolver<RESOURCE, POCDMT200001GB02Informant> {
		
	@Autowired
	public InformantPOCDMT200001GB02TemplateResolver(
		List<TemplateMapper<RESOURCE, POCDMT200001GB02Informant>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Informant getContainer() {
		POCDMT200001GB02Informant informant = new POCDMT200001GB02Informant();
		informant.setTypeCode(informant.getTypeCode());
		informant.getContextControlCode().add("OP");
		
		return informant;
	}
}
