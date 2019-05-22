package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.resolver.InformantChoiceTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Informant;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class InformantDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private InformantChoiceTemplateResolver<? extends IBaseResource> informantChoiceTemplateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Informant informant = new POCDMT200001GB02Informant();
		informant.setTypeCode(informant.getTypeCode());
		informant.getContextControlCode().add("OP");
		
		informantChoiceTemplateResolver.resolve(ResourceProviderUtils.getResources(
				request.getBundle(), QuestionnaireResponse.class).get(0).getSource().getResource(), informant, request);
		
		document.getInformant().add(informant);
	}

}
