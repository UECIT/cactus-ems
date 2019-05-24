package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.Consent;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.consent.ConsentTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Authorization;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class AuthorizationDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private ConsentTemplateResolver<? extends IBaseResource> consentTemplateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		
		POCDMT200001GB02Authorization authorization = 
				consentTemplateResolver.resolve(ResourceProviderUtils.getResource(
						request.getReferralRequest().getContained(), Consent.class), request);
		
		if (authorization != null) document.getAuthorization().add(authorization);
	}

}
