package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.hl7.fhir.instance.model.api.IBaseResource;

import uk.nhs.ctp.service.dto.ReportRequestDTO;

public interface TemplateMapper<RESOURCE extends IBaseResource, CONTAINER> {

	void map(RESOURCE resource, CONTAINER container, ReportRequestDTO request);
	
	Class<RESOURCE> getResourceClass();
	
	String getTemplateName();
	
	static void hello() {
		System.out.println("");
	}
}
