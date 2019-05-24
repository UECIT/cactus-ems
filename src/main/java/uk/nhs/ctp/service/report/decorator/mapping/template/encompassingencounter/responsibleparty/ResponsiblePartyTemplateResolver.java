package uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.responsibleparty;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146232GB01ResponsibleParty;

@Component
public class ResponsiblePartyTemplateResolver<RESOURCE extends IBaseResource>
		extends AbstractTemplateResolver<RESOURCE, COCDTP146232GB01ResponsibleParty> {
	
	@Autowired
	public ResponsiblePartyTemplateResolver(
		List<TemplateMapper<RESOURCE, COCDTP146232GB01ResponsibleParty>> templateMappers) {
	
		super(templateMappers);
	}

	@Override
	protected COCDTP146232GB01ResponsibleParty createContainer() {
		COCDTP146232GB01ResponsibleParty responsibleParty = new COCDTP146232GB01ResponsibleParty();
		responsibleParty.setTypeCode(responsibleParty.getTypeCode());
		
		COCDTP146232GB01ResponsibleParty.TemplateId 
				responsiblePartyTemplateId = new COCDTP146232GB01ResponsibleParty.TemplateId();
		responsiblePartyTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		responsiblePartyTemplateId.setExtension("COCD_TP146232GB01#location");
		responsibleParty.setTemplateId(responsiblePartyTemplateId);
		
		return responsibleParty;
	}
}
