package uk.nhs.ctp.service.report.decorator.mapping.template.textsection;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component31;

@Component
public class IntegratedUrgentCareTextSectionTemplateResolver<RESOURCE extends IBaseResource> 
		extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Component31> {

	public IntegratedUrgentCareTextSectionTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Component31>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Component31 createContainer() {
		POCDMT200001GB02Component31 sectionComponent = new POCDMT200001GB02Component31();
		sectionComponent.setTypeCode(sectionComponent.getTypeCode());
		sectionComponent.setContextConductionInd(true);
	
		II sectionComponentId = new II();
		sectionComponentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		sectionComponentId.setExtension("COCD_TP146246GB01"); 
		
		return sectionComponent;
	}

}
