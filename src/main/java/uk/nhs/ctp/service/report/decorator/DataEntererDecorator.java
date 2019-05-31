package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.dataenterer.DataEntererTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT030001UK01DataEnterer;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;

@Component
public class DataEntererDecorator implements OneOneOneDecorator {
	
	@Autowired
	private DataEntererTemplateResolver<? extends IBaseResource> dataEntererTemplateResolver;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {

		POCDMT030001UK01DataEnterer dataEnterer = dataEntererTemplateResolver.resolve(request.getReferralRequest().getRequester().getAgent().getResource(), request);
		if (dataEnterer != null) document.setDataEnterer(dataEnterer);
	}

}
