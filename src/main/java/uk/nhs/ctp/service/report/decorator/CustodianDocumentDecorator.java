package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.resolver.CustodianChoiceTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;

@Component
public class CustodianDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private CustodianChoiceTemplateResolver<? extends IBaseResource> custodianChoiceTemplateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.setCustodian(custodianChoiceTemplateResolver.resolve(
				request.getReferralRequest().getRequester().getOnBehalfOf().getResource(), request));
	}

}
