package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.location;

import java.util.List;

import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location.TemplateId;

@Component
public class HealthCareFacilityCOCDTP146232GB01TemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<Location, COCDTP146232GB01Location> {
	
	@Autowired
	public HealthCareFacilityCOCDTP146232GB01TemplateResolver(
		List<TemplateMapper<Location, COCDTP146232GB01Location>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected COCDTP146232GB01Location createContainer() {
		COCDTP146232GB01Location location = new COCDTP146232GB01Location();
		location.setTypeCode(location.getTypeCode());
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146232GB01#location");
		location.setTemplateId(templateId);
		
		return location;
	}
}
