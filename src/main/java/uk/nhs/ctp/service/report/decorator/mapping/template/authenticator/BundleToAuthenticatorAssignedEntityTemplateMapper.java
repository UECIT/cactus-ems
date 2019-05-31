package uk.nhs.ctp.service.report.decorator.mapping.template.authenticator;

import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT030001UK01Authenticator;

@Component
public class BundleToAuthenticatorAssignedEntityTemplateMapper implements TemplateMapper<Bundle, POCDMT030001UK01Authenticator>{

	@Override
	public void map(Bundle resource, POCDMT030001UK01Authenticator container, ReportRequestDTO request) {
		COCDTP145210GB01AssignedEntity assignedEntity = new COCDTP145210GB01AssignedEntity();
		
		assignedEntity.setClassCode(assignedEntity.getClassCode());
		
		CVNPfITCodedplain code = new CVNPfITCodedplain();
		code.setCode("test");
		code.setCodeSystem("test");
		code.setDisplayName("test");
		assignedEntity.setCode(code);
		
		container.setCOCDTP145205GB01AssignedEntity(assignedEntity);
	}

	@Override
	public Class<Bundle> getResourceClass() {
		return Bundle.class;
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP145205GB01#AssignedEntity";
	}

}
