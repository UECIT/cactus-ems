package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01Location.TemplateId;

@Component
public class LocationToCOCDTP146232GB01LocationMapper extends AbstractMapper<COCDTP146232GB01Location, CareConnectLocation>{

	@Override
	public COCDTP146232GB01Location map(CareConnectLocation location) {
		COCDTP146232GB01Location targetLocation = new COCDTP146232GB01Location();
		targetLocation.setTypeCode(targetLocation.getTypeCode());
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146232GB01#location");
		targetLocation.setTemplateId(templateId);
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145222GB02#HealthCareFacility");
		targetLocation.setContentId(templateContent);
		
		return targetLocation;
	}

}
