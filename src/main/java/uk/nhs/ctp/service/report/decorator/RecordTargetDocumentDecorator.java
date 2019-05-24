package uk.nhs.ctp.service.report.decorator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.patient.PatientTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class RecordTargetDocumentDecorator implements OneOneOneDecorator {
	
	@Autowired
	private PatientTemplateResolver<? extends IBaseResource> patientTemplateResolver;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		CareConnectPatient patient = ResourceProviderUtils.getResource(
				request.getReferralRequest().getContained(), CareConnectPatient.class);
		
		document.setRecordTarget(patientTemplateResolver.resolve(patient, request));
	}

}
