package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.serviceevent.ServiceEventTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02DocumentationOf;

@Component
public class DocumentationOfDocumentDecorator implements OneOneOneDecorator {
	
	@Autowired
	private ServiceEventTemplateResolver<? extends IBaseResource> serviceEventTemplateResolver;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {

		POCDMT200001GB02DocumentationOf serviceEvent = serviceEventTemplateResolver.resolve(request.getBundle(), request);
		if (serviceEvent != null) document.getDocumentationOf().add(serviceEvent);
	}

}
