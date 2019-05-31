package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.recipient.TrackerRecipientChoiceTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class TrackerDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private TrackerRecipientChoiceTemplateResolver<? extends IBaseResource> templateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		document.getTracker().add(templateResolver.resolve(ResourceProviderUtils.getResource(
				request.getBundle(), CareConnectPatient.class).getGeneralPractitionerFirstRep().getResource(), request));
	}
}
