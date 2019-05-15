package uk.nhs.ctp.service.report.decorators.tracker;

import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorators.OneOneOneDecorator;
import uk.nhs.ctp.service.report.decorators.tracker.mapping.TrackerDataResolver;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

@Component
public class TrackerDocumentDecorator implements OneOneOneDecorator {

	@Autowired
	private TrackerDataResolver<? extends Resource> trackerDataResolver;
	
	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Tracker tracker = new POCDMT200001GB02Tracker();
		
		tracker.setTypeCode(tracker.getTypeCode());
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145203GB03#IntendedRecipient");
		tracker.setContentId(templateContent);
		
		trackerDataResolver.resolve(request.getReferralRequest(), tracker);
	}

}
