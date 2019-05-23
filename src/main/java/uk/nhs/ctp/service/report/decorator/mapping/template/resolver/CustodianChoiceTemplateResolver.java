package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Custodian;

@Component
public class CustodianChoiceTemplateResolver<RESOURCE extends IBaseResource> extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Custodian> {

	@Autowired
	public CustodianChoiceTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Custodian>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Custodian createContainer() {
		POCDMT200001GB02Custodian custodian = new POCDMT200001GB02Custodian();
		custodian.setTypeCode(custodian.getTypeCode());
		
		return custodian;
	}

}
