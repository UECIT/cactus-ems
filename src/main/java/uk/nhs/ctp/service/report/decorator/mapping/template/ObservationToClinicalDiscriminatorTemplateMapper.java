package uk.nhs.ctp.service.report.decorator.mapping.template;

import org.hl7.fhir.dstu3.model.Observation;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146092GB01ClinicalDiscriminator;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146092GB01ClinicalDiscriminator.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component2;

@Component
public class ObservationToClinicalDiscriminatorTemplateMapper implements TemplateMapper<Observation, POCDMT200001GB02Component2> {

	@Override
	public Class<Observation> getResourceClass() {
		return Observation.class;
	}

	@Override
	public void map(Observation observation, POCDMT200001GB02Component2 component, ReportRequestDTO request) {	
		COCDTP146092GB01ClinicalDiscriminator clinicalDiscriminator = new COCDTP146092GB01ClinicalDiscriminator();
		clinicalDiscriminator.setClassCode(clinicalDiscriminator.getClassCode());
		clinicalDiscriminator.setMoodCode(clinicalDiscriminator.getMoodCode());
		
		CV code = new CV();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.540");
		code.setCode("CD");
		clinicalDiscriminator.setCode(code);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP146092GB01#ClinicalDiscriminator");
		clinicalDiscriminator.setTemplateId(templateId);
		
		CV observationCode = new CV();
		observationCode.setCodeSystem(observation.getCode().getCodingFirstRep().getSystem());
		observationCode.setCode(observation.getCode().getCodingFirstRep().getCode());
		observationCode.setDisplayName(observation.getCode().getCodingFirstRep().getDisplay() + " (" + observation.getValueBooleanType() + ")");
		clinicalDiscriminator.setValue(observationCode);
		component.setCOCDTP146092GB01ClinicalDiscriminator(clinicalDiscriminator);
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP146092GB01#ClinicalDiscriminator";
	}

}
