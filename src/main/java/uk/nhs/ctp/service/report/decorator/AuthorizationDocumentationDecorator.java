package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.Consent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.ConsentToConsentMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Authorization;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class AuthorizationDocumentationDecorator implements OneOneOneDecorator {

	@Autowired
	private ConsentToConsentMapper consentToConsentMapper;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Authorization authorization = new POCDMT200001GB02Authorization();

		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP146226GB02#Consent");
		authorization.setContentId(templateContent);
		
		authorization.setTypeCode(authorization.getTypeCode());
		authorization.setCOCDTP146226GB02Consent(consentToConsentMapper.map(
				ResourceProviderUtils.getResource(request.getReferralRequest().getContained(), Consent.class)));
		
		document.getAuthorization().add(authorization);
	}

}
