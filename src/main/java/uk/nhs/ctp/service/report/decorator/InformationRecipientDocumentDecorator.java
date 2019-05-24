package uk.nhs.ctp.service.report.decorator;

import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.recipient.InformationRecipientTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02PrimaryInformationRecipient;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;

@Component
public class InformationRecipientDocumentDecorator implements OneOneOneDecorator, AmbulanceDecorator {
	
	@Autowired
	private InformationRecipientTemplateResolver<? extends IBaseResource> recipientTemplateResolver;
	
	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.getInformationRecipient().addAll(createInformationRecipients(request));
	}

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getInformationRecipient().addAll(createInformationRecipients(request));
	}
	
	private List<POCDMT200001GB02PrimaryInformationRecipient> createInformationRecipients(ReportRequestDTO request) {
		return request.getReferralRequest().getRecipient().stream().map(ref -> 
				recipientTemplateResolver.resolve(ref.getResource(), request)).collect(Collectors.toList());
	}

}
