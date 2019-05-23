package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Tracker;

@Component
public class TrackerRecipientChoiceTemplateResolver<RESOURCE extends IBaseResource> 
		extends RecipientChoiceTemplateResolver<RESOURCE, POCDMT200001GB02Tracker> {

	@Autowired
	public TrackerRecipientChoiceTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02Tracker>> templateMappers) {
		
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02Tracker getContainer() {
		POCDMT200001GB02Tracker tracker = new POCDMT200001GB02Tracker();
		tracker.setTypeCode(tracker.getTypeCode());
		
		return tracker;
	}
}
