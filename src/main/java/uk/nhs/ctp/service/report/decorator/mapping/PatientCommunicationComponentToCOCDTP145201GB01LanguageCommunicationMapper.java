package uk.nhs.ctp.service.report.decorator.mapping;

import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.report.org.hl7.v3.BL;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01LanguageCommunication;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01LanguageCommunication.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;

@Component
public class PatientCommunicationComponentToCOCDTP145201GB01LanguageCommunicationMapper 
		extends AbstractMapper<COCDTP145201GB01LanguageCommunication, PatientCommunicationComponent> {

	@Autowired
	private CodingToCVNPfITCodedplainRequiredMapper codingMapper;
	
	@Value("${ems.terminology.human.language.system}")
	private String humanLanguageSystem;
	
	@Override
	public COCDTP145201GB01LanguageCommunication map(PatientCommunicationComponent communicationComponent) {
		COCDTP145201GB01LanguageCommunication language = new COCDTP145201GB01LanguageCommunication();
		CS languageCode = new CS();
		CVNPfITCodedplainRequired code = codingMapper.map(
				communicationComponent.getLanguage().getCodingFirstRep(), humanLanguageSystem);
		
		languageCode.setCode(code.getCode());
		language.setLanguageCode(languageCode);
		
		BL bl = new BL();
		bl.setNullFlavor(CsNullFlavor.UNK);
		language.setPreferenceInd(bl);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145201GB01#languageCommunication");
		language.setTemplateId(templateId);
		
		return language;
	}

}
