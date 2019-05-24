package uk.nhs.ctp.service.report.decorator.mapping.template.patient;

import java.util.List;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.decorator.mapping.template.AbstractTemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02RecordTarget;

@Component
public class PatientTemplateResolver<RESOURCE extends IBaseResource>
		extends AbstractTemplateResolver<RESOURCE, POCDMT200001GB02RecordTarget> {

	@Autowired
	public PatientTemplateResolver(
			List<TemplateMapper<RESOURCE, POCDMT200001GB02RecordTarget>> templateMappers) {

		super(templateMappers);
	}

	@Override
	protected POCDMT200001GB02RecordTarget createContainer() {
		POCDMT200001GB02RecordTarget recordTarget = new POCDMT200001GB02RecordTarget();
		recordTarget.setTypeCode(recordTarget.getTypeCode());
		recordTarget.getContextControlCode().add("OP");
		
		return recordTarget;
	}
}
