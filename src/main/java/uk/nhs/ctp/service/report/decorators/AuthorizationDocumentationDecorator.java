package uk.nhs.ctp.service.report.decorators;

import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Authorization;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;

@Component
public class AuthorizationDocumentationDecorator implements OneOneOneDecorator {

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		POCDMT200001GB02Authorization authorization = new POCDMT200001GB02Authorization();
		// The HL7 attribute typeCode uses a code to describe this class as one of authorization.
		authorization.setTypeCode(authorization.getTypeCode());
		// The HL7 (NHS localisation) attribute contentId, when valued in an instance, provides a unique forward pointing identifier for the template which constrains the classes and attributes which follow, for NHS use.
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP146226GB02#Consent");
		authorization.setContentId(templateContent);
		document.getAuthorization().add(authorization);
	}

}
