package uk.nhs.ctp.service.report.decorator.mapping.template.codedentry;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.Observation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding.StatusCode;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component2;

@Component
public class ObservationToFindingTemplateMapper implements TemplateMapper<Observation, POCDMT200001GB02Component2> {

	@Autowired
    private SimpleDateFormat reportDateFormat;
	
	@Autowired
	private Generex uuidGenerator;
	
	@Override
	public Class<Observation> getResourceClass() {
		return Observation.class;
	}

	@Override
	public void map(Observation observation, POCDMT200001GB02Component2 component, ReportRequestDTO request) {	
		
		COCDTP146312GB01Finding finding = new COCDTP146312GB01Finding();
		finding.setClassCode(finding.getClassCode());
		finding.setMoodCode(finding.getMoodCode());
		
		IVLTS effectiveTime = new IVLTS();
        effectiveTime.setValue(reportDateFormat.format(new Date()));
		finding.setEffectiveTime(effectiveTime);
		
		IINPfITUuidMandatory id = new IINPfITUuidMandatory();
		id.setRoot(uuidGenerator.random());
		finding.setId(id);
		
		CS interpretationCode = new CS();
		interpretationCode.setCode("N");
		interpretationCode.setCodeSystem("2.16.840.1.113883.5.83");
		interpretationCode.setDisplayName("Normal");
		finding.setInterpretationCode(interpretationCode);
		
		StatusCode statusCode = new StatusCode();
		statusCode.setCode("completed");
		statusCode.setCodeSystem("2.16.840.1.113883.5.14");
		statusCode.setDisplayName("completed");
		finding.setStatusCode(statusCode);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension(getTemplateName());
		finding.setTemplateId(templateId);
		
		CV observationCode = new CV();
		observationCode.setCodeSystem(observation.getCode().getCodingFirstRep().getSystem());
		observationCode.setCode(observation.getCode().getCodingFirstRep().getCode());
		observationCode.setDisplayName(observation.getCode().getCodingFirstRep().getDisplay() + " (" + observation.getValueBooleanType() + ")");
		finding.setValue(observationCode);
		
		component.setCOCDTP146312GB01Finding(finding);
	}

	@Override
	public String getTemplateName() {
		return "COCD_TP146312GB01#Finding";
	}

}
