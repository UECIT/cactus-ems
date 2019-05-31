package uk.nhs.ctp.service.report.decorator.mapping.template.serviceevent;

import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.CE;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146227GB02ServiceEvent;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146227GB02ServiceEvent.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02DocumentationOf;

@Component
public class BundleToServiceEventTemplateMapper implements TemplateMapper<Bundle, POCDMT200001GB02DocumentationOf>{

	@Override
	public void map(Bundle resource, POCDMT200001GB02DocumentationOf container, ReportRequestDTO request) {
		COCDTP146227GB02ServiceEvent serviceEvent = new COCDTP146227GB02ServiceEvent();
		serviceEvent.setClassCode("OBSSER");
		serviceEvent.setMoodCode(serviceEvent.getMoodCode());
		
		CE code = new CE();
		code.setCode("272379006");
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.15");
		code.setDisplayName("Event");
		serviceEvent.setCode(code);
		
		II id = new II();
		id.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST\"");
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.40");
		id.setExtension("KA901");
		serviceEvent.getId().add(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		serviceEvent.setTemplateId(templateId);
		
		container.setCOCDTP146227GB02ServiceEvent(serviceEvent);
		
	}

	@Override
	public Class<Bundle> getResourceClass() {
		return Bundle.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP146227GB02#ServiceEvent";
	}

}
