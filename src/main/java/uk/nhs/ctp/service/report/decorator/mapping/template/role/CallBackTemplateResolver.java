package uk.nhs.ctp.service.report.decorator.mapping.template.role;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Participant;

@Component
public class CallBackTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Participant>{

	@Autowired
	public CallBackTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Participant>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Participant createContainer() {
		POCDMT200001GB02Participant participant = new POCDMT200001GB02Participant();
		participant.getTypeCode().add("CALLBCK");
		participant.getContextControlCode().add("OP");
		
		return participant;
	}

}
