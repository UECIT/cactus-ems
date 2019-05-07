package uk.nhs.ctp.service.report.decorators;

import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import resources.CareConnectPatient;
import resources.CareConnectPractitioner;
import uk.nhs.ctp.service.TerminologyService;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01LanguageCommunication;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Patient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;
import uk.nhs.ctp.service.report.org.hl7.v3.ObjectFactory;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02ClinicalDocument;
import uk.nhs.ctp.service.report.org.hl7.v3.POCDMT200001GB02RecordTarget;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class RecordTargetDocumentDecorator implements OneOneOneDecorator {
	
	@Autowired
	private TerminologyService terminologyService;
	
	@Value("${ems.terminology.administrative.gender.system}")
	private String administrativeGenderSystem;
	
	@Value("${ems.terminology.human.language.system}")
	private String humanLanguageSystem;

	@Override
	public void decorate(POCDMT200001GB02ClinicalDocument document, ReportRequestDTO request) {
		ObjectFactory objectFactory = new ObjectFactory();
		
		POCDMT200001GB02RecordTarget recordTarget = new POCDMT200001GB02RecordTarget();
		recordTarget.setTypeCode(recordTarget.getTypeCode());
		recordTarget.getContextControlCode().add("OP");
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("COCD_TP145201GB01#PatientRole");
		recordTarget.setContentId(templateContent);
		
		CareConnectPatient fhirPatient = 
				ResourceProviderUtils.getResource(request.getBundle(), CareConnectPatient.class);
		CareConnectOrganization nominatedPharmacy =
				ResourceProviderUtils.getResource(fhirPatient.getContained(), CareConnectOrganization.class);
		CareConnectPractitioner generalPractitioner = 
				ResourceProviderUtils.getResource(fhirPatient.getContained(), CareConnectPractitioner.class);
		
		// set PatientRole
		COCDTP145201GB01PatientRole patientRole = new COCDTP145201GB01PatientRole();
		patientRole.setClassCode(patientRole.getClassCode());
		
		COCDTP145201GB01Organization providerOrganization = objectFactory.createCOCDTP145201GB01Organization();
		
		List<StringType> addressLines = generalPractitioner.getAddress().get(0).getLine();
		String practice = addressLines.get(0).primitiveValue();
		AD providerOrganizationAddress = objectFactory.createAD();
		providerOrganizationAddress.getContent().add(new JAXBElement<String>(new QName("houseNumber"), String.class, practice));
		providerOrganization.setAddr(providerOrganizationAddress);
		
		HumanName contactName = generalPractitioner.getName().get(0);
		ON providerOrganizationName = objectFactory.createON();
		providerOrganizationName.getContent().add(new JAXBElement<String>(new QName("family"), String.class, contactName.getFamily()));
		providerOrganizationName.getContent().add(new JAXBElement<String>(new QName("given"), String.class, contactName.getGivenAsSingleString()));
		providerOrganizationName.getContent().add(new JAXBElement<String>(new QName("prefix"), String.class, contactName.getPrefixAsSingleString()));
		providerOrganization.setName(providerOrganizationName);
		
		patientRole.setProviderOrganization(
				new JAXBElement<COCDTP145201GB01Organization>(new QName("providerOrganization"), COCDTP145201GB01Organization.class, providerOrganization));

		// build patient
		COCDTP145201GB01Patient patient = new COCDTP145201GB01Patient();
		patient.setClassCode(patient.getClassCode());
		patient.setDeterminerCode(patient.getDeterminerCode());

		PN patientName = new PN();
		patientName.getContent().add(fhirPatient.getNameFirstRep().getNameAsSingleString());
		patientName.getUse().add(CsEntityNameUse.L);
		patient.getName().add(patientName);
		
		TS birthTime = new TS();
		birthTime.setValue(fhirPatient.getBirthDate().toString());
		patient.setBirthTime(birthTime);
		patient.setAdministrativeGenderCode(terminologyService.getCode(
				fhirPatient.getGender().getSystem(), administrativeGenderSystem, fhirPatient.getGender().getDefinition()));
		
		// ComunicationLanguage - TEMP
		COCDTP145201GB01LanguageCommunication language = new COCDTP145201GB01LanguageCommunication();
		CS languagecode = new CS();
		
		CVNPfITCodedplainRequired tempCode = terminologyService.getCode(fhirPatient.getCommunicationFirstRep().getLanguage().getCodingFirstRep().getSystem(), humanLanguageSystem, fhirPatient.getCommunicationFirstRep().getLanguage().getCodingFirstRep().getCode());
		languagecode.setCode(tempCode.getCode());
		languagecode.setCodeSystem(tempCode.getCodeSystem());
		languagecode.setDisplayName(tempCode.getDisplayName());
		
		language.setLanguageCode(languagecode);
		patient.getLanguageCommunication().add(language);
		
		patientRole.setPatientPatient(patient);
		recordTarget.setCOCDTP145201GB01PatientRole(patientRole);
		document.setRecordTarget(recordTarget);
	}

}
