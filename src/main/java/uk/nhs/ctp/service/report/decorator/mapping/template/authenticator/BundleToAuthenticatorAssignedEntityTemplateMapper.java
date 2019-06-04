package uk.nhs.ctp.service.report.decorator.mapping.template.authenticator;

import org.hl7.fhir.dstu3.model.Bundle;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
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
		
		IINPfITOidRequiredAssigningAuthorityName id = new IINPfITOidRequiredAssigningAuthorityName();
		id.setRoot("1.2.826.0.1285.0.2.0.65");
		id.setExtension("K9684");
		assignedEntity.getId().add(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		assignedEntity.setTemplateId(templateId);
		
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
