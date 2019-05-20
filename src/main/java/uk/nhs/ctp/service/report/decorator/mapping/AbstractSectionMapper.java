package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.Base;

import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146246GB01Section1.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;

public abstract class AbstractSectionMapper<BASE extends Base> 
		extends AbstractMapper<COCDTP146246GB01Section1, BASE> {

	@Override
	public COCDTP146246GB01Section1 map(BASE base) {
		COCDTP146246GB01Section1 section = new COCDTP146246GB01Section1();
		section.setClassCode(section.getClassCode());
		section.setMoodCode(section.getMoodCode());
		
		IINPfITUuidMandatory id = new IINPfITUuidMandatory();
		id.setRoot("3209305703485703987578");
		section.setId(id);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146246GB01#Section1");
		section.setTemplateId(templateId);
		
		map(base, section);
		
		return section;
	}
	
	protected abstract void map(BASE base, COCDTP146246GB01Section1 section);
}
