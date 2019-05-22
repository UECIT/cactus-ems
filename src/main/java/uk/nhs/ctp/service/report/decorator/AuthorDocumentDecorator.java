package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.resolver.AuthorChoiceTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Author;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;

@Component
public class AuthorDocumentDecorator implements OneOneOneDecorator, AmbulanceDecorator {
	
	@Autowired
	private AuthorChoiceTemplateResolver<? extends IBaseResource> authorChoiceTemplateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor(request));
	}

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.getAuthor().add(createAuthor(request));
	}
	
	public POCDMT200001GB02Author createAuthor(ReportRequestDTO request) {
		return authorChoiceTemplateResolver.resolve(
				request.getReferralRequest().getRequester().getAgent().getResource(), request);
	}

}
