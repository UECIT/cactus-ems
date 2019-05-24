package uk.nhs.ctp.service.report.decorator;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.role.CallBackTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Participant;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class CallBackContactDocumentDecorator implements AmbulanceDecorator {
	
	@Autowired
	private CallBackTemplateResolver<? extends IBaseResource> callBackTemplateResolver;
	
	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		document.setCallBackContact(CreateCallBackContact(request));
	}
	
	private JAXBElement<POCDMT200001GB02Participant> CreateCallBackContact(ReportRequestDTO request) {
		POCDMT200001GB02Participant callBackContact = callBackTemplateResolver.resolve(
						ResourceProviderUtils.getResource(request.getBundle(), CareConnectPatient.class), request);
		
		return new JAXBElement<POCDMT200001GB02Participant>(
				new QName("callBackContact"), POCDMT200001GB02Participant.class, callBackContact);
	}

}
