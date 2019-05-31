package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.location;

import java.util.List;

import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Location;

@Component
public class HealthCareFacilityREPCMT200001GB02TemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<CareConnectLocation, REPCMT200001GB02Location> {
	
	@Autowired
	public HealthCareFacilityREPCMT200001GB02TemplateResolver(
		List<TemplateMapper<CareConnectLocation, REPCMT200001GB02Location>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected REPCMT200001GB02Location createContainer() {
		REPCMT200001GB02Location location = new REPCMT200001GB02Location();
		location.setTypeCode(location.getTypeCode());
		return location;
	}
}
