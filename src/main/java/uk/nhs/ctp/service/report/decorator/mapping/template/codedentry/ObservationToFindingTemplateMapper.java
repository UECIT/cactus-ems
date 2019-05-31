package uk.nhs.ctp.service.report.decorator.mapping.template.codedentry;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hl7.fhir.dstu3.model.CareConnectObservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mifmif.common.regex.Generex;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.mapping.template.TemplateMapper;
import uk.nhs.ctp.service.report.org.hl7.v3.CDNPfITCDAUrl;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding.StatusCode;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP146312GB01Finding.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CV;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITUuidMandatory;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02Component2;
import uk.nhs.ctp.service.report.org.hl7.v3.XActMoodDocumentObservation;

@Component
public class ObservationToFindingTemplateMapper implements TemplateMapper<CareConnectObservation, POCDMT200001GB02Component2> {

	@Autowired
    private SimpleDateFormat reportDateFormat;
	
	@Autowired
	private Generex uuidGenerator;
	
	@Override
	public Class<CareConnectObservation> getResourceClass() {
		return CareConnectObservation.class;
	}

	@Override
	public void map(CareConnectObservation observation, POCDMT200001GB02Component2 component, ReportRequestDTO request) {	
		
		COCDTP146312GB01Finding finding = new COCDTP146312GB01Finding();
		finding.setClassCode(finding.getClassCode());
		finding.setMoodCode(XActMoodDocumentObservation.DEF);
		
		CDNPfITCDAUrl code = new CDNPfITCDAUrl();
        code.setCode("163131000000108 ");
        code.setCodeSystem("2.16.840.1.113883.6.96");
        code.setDisplayName("Clinical observations and findings ");
        finding.setCode(code);
		
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
