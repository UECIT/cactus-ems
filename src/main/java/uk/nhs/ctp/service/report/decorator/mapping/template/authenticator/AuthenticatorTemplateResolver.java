package uk.nhs.ctp.service.report.decorator.mapping.template.authenticator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT030001UK01Authenticator;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;

@Component
public class AuthenticatorTemplateResolver <RESOURCE extends IBaseResource> 
extends AbstractTemplateResolver<RESOURCE, POCDMT030001UK01Authenticator>  {
	
	@Autowired
	private SimpleDateFormat reportDateFormat;

	public AuthenticatorTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT030001UK01Authenticator>> templateMappers) {
		super(templateMappers);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected POCDMT030001UK01Authenticator createContainer() {
		POCDMT030001UK01Authenticator authenticator = new POCDMT030001UK01Authenticator();
		authenticator.setTypeCode(authenticator.getTypeCode());
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145205GB01#AssignedEntity");
		authenticator.setContentId(templateContent);
		
		CS signatureCode = new CS();
		signatureCode.setNullFlavor(CsNullFlavor.NA);
		authenticator.setSignatureCode(signatureCode);
		
		TS time = new TS();
		time.setValue(reportDateFormat.format(new Date()));
		authenticator.setTime(time);
		
		return authenticator;
	}

}
