package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

@Component
public class InformationRecipientChoiceTemplateResolver<RESOURCE extends IBaseResource> extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02Tracker>{

	@Autowired
	public InformationRecipientChoiceTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Tracker>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Tracker createContainer() {
		POCDMT200001GB02Tracker tracker = new POCDMT200001GB02Tracker();
		
		tracker.setTypeCode(tracker.getTypeCode());
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145203GB03#IntendedRecipient");
		tracker.setContentId(templateContent);
		
		return tracker;
	}
	
}
