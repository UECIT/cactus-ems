package uk.nhs.ctp.service.report.decorator.mapping.template.integratedurgentcaretextsection;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component31;

public abstract class AbstractSection1TemplateMapper<RESOURCE extends IBaseResource> 
		implements TemplateMapper<RESOURCE, POCDMT200001GB02Component31>{
	
	@Autowired
	private Generex uuidGenerator;
	
	@Override
	public void map(RESOURCE resource, POCDMT200001GB02Component31 container, ReportRequestDTO request) {
		COCDTP146246GB01Section1 section = new COCDTP146246GB01Section1();
		section.setClassCode(section.getClassCode());
		section.setMoodCode(section.getMoodCode());
		
		IINPfITUuidMandatory id = new IINPfITUuidMandatory();
		id.setRoot(uuidGenerator.random());
		section.setId(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		section.setTemplateId(templateId);
		
		map(resource, section);
		container.setCOCDTP146246GB01Section1(section);
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP146246GB01#Section1";
	}
	
	protected abstract void map(RESOURCE resource, COCDTP146246GB01Section1 section);

}
