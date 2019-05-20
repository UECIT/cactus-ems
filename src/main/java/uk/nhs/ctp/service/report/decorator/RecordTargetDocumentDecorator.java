package uk.nhs.ctp.service.report.decorator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import resources.CareConnectPatient;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.PatientToPatientRoleMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02RecordTarget;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class RecordTargetDocumentDecorator implements OneOneOneDecorator {
	
	@Autowired
	private PatientToPatientRoleMapper patientToPatientRoleMapper;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02RecordTarget recordTarget = new POCDMT200001GB02RecordTarget();
		recordTarget.setTypeCode(recordTarget.getTypeCode());
		recordTarget.getContextControlCode().add("OP");
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145201GB01#PatientRole");
		recordTarget.setContentId(templateContent);
		
		CareConnectPatient ccPatient = ResourceProviderUtils.getResource(
				request.getReferralRequest().getContained(), CareConnectPatient.class);

		recordTarget.setCOCDTP145201GB01PatientRole(patientToPatientRoleMapper.map(ccPatient));

		document.setRecordTarget(recordTarget);
	}

}
