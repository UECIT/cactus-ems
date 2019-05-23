package uk.nhs.ctp.service.report.decorator.mapping.template.resolver;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;

import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02PrimaryInformationRecipient;

public class InformationRecipientChoiceTemplateResolver<RESOURCE extends IBaseResource>
		extends RecipientChoiceTemplateResolver<RESOURCE, POCDMT200001GB02PrimaryInformationRecipient> {

	@Autowired
	public InformationRecipientChoiceTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02PrimaryInformationRecipient>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02PrimaryInformationRecipient getContainer() {
		POCDMT200001GB02PrimaryInformationRecipient 
				informationRecipient = new POCDMT200001GB02PrimaryInformationRecipient();
		informationRecipient.setTypeCode(informationRecipient.getTypeCode());

		return informationRecipient;
	}
}
