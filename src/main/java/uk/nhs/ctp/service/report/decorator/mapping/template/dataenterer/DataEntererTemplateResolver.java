package uk.nhs.ctp.service.report.decorator.mapping.template.dataenterer;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT030001UK01DataEnterer;

@Component
public class DataEntererTemplateResolver <RESOURCE extends IBaseResource> 
extends AbstractTemplateResolver<RESOURCE, POCDMT030001UK01DataEnterer> {

	public DataEntererTemplateResolver(List<TemplateMapper<RESOURCE, POCDMT030001UK01DataEnterer>> templateMappers) {
		super(templateMappers);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected POCDMT030001UK01DataEnterer createContainer() {
		POCDMT030001UK01DataEnterer dataEnterer = new POCDMT030001UK01DataEnterer();
		dataEnterer.setTypeCode(dataEnterer.getTypeCode());
		dataEnterer.setContextControlCode(dataEnterer.getContextControlCode());
		
		return dataEnterer;
	}

}
