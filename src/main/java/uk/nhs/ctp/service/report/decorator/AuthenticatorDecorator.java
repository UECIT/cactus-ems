package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.authenticator.AuthenticatorTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT030001UK01Authenticator;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;

@Component
public class AuthenticatorDecorator implements OneOneOneDecorator {
	
	@Autowired
	private AuthenticatorTemplateResolver<? extends IBaseResource> authenticatorTemplateResolver;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {

		POCDMT030001UK01Authenticator authenticator = authenticatorTemplateResolver.resolve(request.getBundle(), request);
		
		if (authenticator != null) document.setAuthenticator(authenticator);
	}

}
