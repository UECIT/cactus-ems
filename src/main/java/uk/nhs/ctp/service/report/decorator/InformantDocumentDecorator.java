package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.informant.InformantPOCDMT200001GB02TemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class InformantDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private InformantPOCDMT200001GB02TemplateResolver<? extends IBaseResource> informantTemplateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getInformant().add(informantTemplateResolver.resolve(ResourceProviderUtils.getResources(
				request.getBundle(), QuestionnaireResponse.class).get(0).getSource().getResource(), request));
	}

}
