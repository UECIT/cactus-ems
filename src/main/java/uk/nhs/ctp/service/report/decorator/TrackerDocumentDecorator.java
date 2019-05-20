package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.resolver.InformationRecipientChoiceTemplateResolver;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class TrackerDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private InformationRecipientChoiceTemplateResolver<? extends IBaseResource> templateResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Tracker tracker = new POCDMT200001GB02Tracker();
		
		tracker.setTypeCode(tracker.getTypeCode());
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145203GB03#IntendedRecipient");
		tracker.setContentId(templateContent);
		
		document.getTracker().add(templateResolver.resolve(ResourceProviderUtils.getResource(
				request.getReferralRequest().getContained(), CareConnectPractitioner.class), tracker, request));
	}
}
