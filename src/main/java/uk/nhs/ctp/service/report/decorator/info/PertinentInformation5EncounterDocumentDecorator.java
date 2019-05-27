package uk.nhs.ctp.service.report.decorator.info;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import resources.CareConnectOrganization;
import resources.CareConnectPatient;
import uk.nhs.ctp.service.TerminologyService;
import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.decorator.mapping.HumanNameToCOCDTP145210GB01PersonMapper;
import uk.nhs.ctp.service.report.npfit.hl7.localisation.TemplateContent;
import uk.nhs.ctp.service.report.org.hl7.v3.AD;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01LanguageCommunication;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01Patient;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145201GB01PatientRole;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01AssignedEntity.TemplateId;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01Organization;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145210GB01Organization.Id;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02HealthCareFacility;
import uk.nhs.ctp.service.report.org.hl7.v3.COCDTP145222GB02Place;
import uk.nhs.ctp.service.report.org.hl7.v3.CS;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplain;
import uk.nhs.ctp.service.report.org.hl7.v3.CVNPfITCodedplainRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.CsEntityNameUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsNullFlavor;
import uk.nhs.ctp.service.report.org.hl7.v3.CsPostalAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.CsTelecommunicationAddressUse;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidMandatoryAssignedAuthority;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequired;
import uk.nhs.ctp.service.report.org.hl7.v3.IINPfITOidRequiredAssigningAuthorityName;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.ON;
import uk.nhs.ctp.service.report.org.hl7.v3.PN;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02EncounterEvent;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Informant;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Location;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation.SeperatableInd;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02RecordTarget;
import uk.nhs.ctp.service.report.org.hl7.v3.TEL;
import uk.nhs.ctp.service.report.org.hl7.v3.TN;
import uk.nhs.ctp.service.report.org.hl7.v3.TS;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class PertinentInformation5EncounterDocumentDecorator implements AmbulanceDecorator  {
	
	@Autowired
	private TerminologyService terminologyService;
	
	@Autowired
	private HumanNameToCOCDTP145210GB01PersonMapper humanNameToPersonMapper;
	
	@Autowired
	private SimpleDateFormat reportDateFormat;
	
	@Value("${ems.terminology.administrative.gender.system}")
	private String administrativeGenderSystem;
	
	@Value("${ems.terminology.human.language.system}")
	private String humanLanguageSystem;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		
		Encounter fhirEncounter = (Encounter) ResourceProviderUtils.getResource(request.getBundle(), Composition.class).getEncounter().getResource();
		RelatedPerson fhirInformant = (RelatedPerson) request.getReferralRequest().getRequester().getAgent().getResource();
		CareConnectOrganization fhirOrganization = (CareConnectOrganization) request.getReferralRequest().getRequester().getOnBehalfOf().getResource();
		
		REPCMT200001GB02PertinentInformation encounter = document.getPertinentInformation5();
		encounter.setTypeCode(encounter.getTypeCode());
		SeperatableInd seperatableInd = new SeperatableInd();
		seperatableInd.setValue(false);
		encounter.setSeperatableInd(seperatableInd);
		
		REPCMT200001GB02EncounterEvent encounterEvent = new REPCMT200001GB02EncounterEvent();
		encounterEvent.setClassCode(encounterEvent.getClassCode());
		encounterEvent.setMoodCode(encounterEvent.getMoodCode());
		
		IVLTS effectiveTime = new IVLTS();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		effectiveTime.setValue(format.format(new Date()));
		encounterEvent.setEffectiveTime(effectiveTime);
		
		II id = new II();
		id.setRoot("2.16.840.1.113883.2.1.3.2.4.18.49");
		id.setExtension(request.getCaseId().toString());
		encounterEvent.getId().add(id);
		
		II assigningAuthorityId = new II();
		assigningAuthorityId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.35");
		assigningAuthorityId.setAssigningAuthorityName("RA9:SOUTH DEVON HEALTHCARE NHS TRUST");
		encounterEvent.getId().add(assigningAuthorityId);
		
		REPCMT200001GB02Informant informant = new REPCMT200001GB02Informant();
		informant.setTypeCode(informant.getTypeCode());
		informant.getContextControlCode().add("OP");
		
		TemplateContent templateContent = new TemplateContent();
		templateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		templateContent.setExtension("NPFIT-000085#Role");
		informant.setContentId(templateContent);
		
		COCDTP145210GB01AssignedEntity assignedEntity = new COCDTP145210GB01AssignedEntity();
		assignedEntity.setClassCode(assignedEntity.getClassCode());
		
		// pull this from RefferalRequest.supportingInfo(assignedAuthor)
		AD informantAddress = new AD();
		informantAddress.getContent().add(fhirInformant.getAddressFirstRep().getLine().get(0).getValue());
		informantAddress.getContent().add(fhirInformant.getAddressFirstRep().getLine().get(1).getValue());
		informantAddress.getContent().add(fhirInformant.getAddressFirstRep().getCity());
		informantAddress.getContent().add(fhirInformant.getAddressFirstRep().getPostalCode());
		informantAddress.getUse().add(CsPostalAddressUse.PHYS);
		assignedEntity.setAddr(informantAddress);
		
		CVNPfITCodedplain code = new CVNPfITCodedplain();
		code.setCodeSystem("2.16.840.1.113883.2.1.3.2.4.17.124");
		code.setCode("NR1690");
		code.setDisplayName("Call Operator");
		assignedEntity.setCode(code);
		
		IINPfITOidRequiredAssigningAuthorityName assigningAuthorityNameId = new IINPfITOidRequiredAssigningAuthorityName();
		assigningAuthorityNameId.setNullFlavor(CsNullFlavor.NA);
		assignedEntity.getId().add(assigningAuthorityNameId);
		
		// populate informant Phone
		TEL phone = new TEL();
		phone.setValue(fhirInformant.getTelecomFirstRep().getValue());
		phone.getUse().add(CsTelecommunicationAddressUse.H);
		assignedEntity.getTelecom().add(phone);
		
		TemplateId templateId = new TemplateId();
		templateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		templateId.setExtension("COCD_TP145210GB01#AssignedEntity");
		assignedEntity.setTemplateId(templateId);

		assignedEntity.setAssignedPerson(humanNameToPersonMapper.map(fhirInformant.getNameFirstRep()));
		
		// populate informant organization
		COCDTP145210GB01Organization informantOrganization = new COCDTP145210GB01Organization();
		informantOrganization.setClassCode(informantOrganization.getClassCode());
		informantOrganization.setDeterminerCode(informantOrganization.getDeterminerCode());
		Id informantOrganizationId = new Id();
		informantOrganizationId.setNullFlavor(CsNullFlavor.NA);
		informantOrganization.setId(informantOrganizationId);
		ON informantOrganizationName = new ON();
		informantOrganizationName.getContent().add(fhirOrganization.getName());
		informantOrganizationName.getUse().add(CsEntityNameUse.L);
		informantOrganization.setName(informantOrganizationName);
		
		COCDTP145210GB01Organization.TemplateId informantOrganizationTemplateId = new COCDTP145210GB01Organization.TemplateId();
		informantOrganizationTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		informantOrganizationTemplateId.setExtension("COCD_TP145210GB01#representedOrganization");
		informantOrganization.setTemplateId(informantOrganizationTemplateId);
		assignedEntity.setRepresentedOrganization(informantOrganization);
		
		informant.setCOCDTP145210GB01AssignedEntity(assignedEntity);
		encounterEvent.setInformant(new JAXBElement<REPCMT200001GB02Informant>(new QName("urn:hl7-org:v3", "informant"), REPCMT200001GB02Informant.class, informant));
		
		// TODO populate location
//		fhirEncounter.getLocation()
		REPCMT200001GB02Location location = new REPCMT200001GB02Location();
		location.setTypeCode(location.getTypeCode());
		TemplateContent locationContentId = new TemplateContent();
		locationContentId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		locationContentId.setExtension("COCD_TP145222GB02#HealthCareFacility");
		location.setContentId(locationContentId);
		
		COCDTP145222GB02HealthCareFacility healthCareFacility = new COCDTP145222GB02HealthCareFacility();
		healthCareFacility.setClassCode(healthCareFacility.getClassCode());
		CVNPfITCodedplain typeCode = new CVNPfITCodedplain();
		typeCode.setCode(fhirEncounter.getClass_().getCode());
		typeCode.setCodeSystem(fhirEncounter.getClass_().getSystem());
		typeCode.setDisplayName(fhirEncounter.getClass_().getDisplay());
		healthCareFacility.setCode(typeCode);
		
		IINPfITOidRequired healthCareFacilityId = new IINPfITOidRequired();
		healthCareFacilityId.setNullFlavor(CsNullFlavor.NA);
		healthCareFacility.getId().add(healthCareFacilityId);
		
		COCDTP145222GB02HealthCareFacility.TemplateId healthCareFacilityTemplateId = new COCDTP145222GB02HealthCareFacility.TemplateId();
		healthCareFacilityTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		healthCareFacilityTemplateId.setExtension("COCD_TP145222GB02#HealthCareFacility");
		healthCareFacility.setTemplateId(healthCareFacilityTemplateId);
		
		// Set to to patients address for demonstration
		COCDTP145222GB02Place place = new COCDTP145222GB02Place();
		place.setClassCode(place.getClassCode());
		place.setDeterminerCode(place.getDeterminerCode());
		Location fhirLocation = (Location) fhirEncounter.getLocationFirstRep().getLocation().getResource();
		place.setAddr(new AD());
		place.getAddr().getContent().add(fhirLocation.getAddress().getLine().get(0).getValue());
		place.getAddr().getContent().add(fhirLocation.getAddress().getLine().get(1).getValue());
		place.getAddr().getContent().add(fhirLocation.getAddress().getCity());
		place.getAddr().getContent().add(fhirLocation.getAddress().getPostalCode());
		place.getAddr().getUse().add(CsPostalAddressUse.PHYS);
		TN locationName = new TN();
		locationName.getContent().add(fhirLocation.getName());
		locationName.getUse().add(CsEntityNameUse.L);
		place.setName(locationName);
		COCDTP145222GB02Place.TemplateId placeTemplateId = new COCDTP145222GB02Place.TemplateId();
		placeTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		placeTemplateId.setExtension("COCD_TP145222GB02#HealthCareFacility");
		place.setTemplateId(placeTemplateId);
		healthCareFacility.setLocation(new JAXBElement<COCDTP145222GB02Place>(new QName("urn:hl7-org:v3", "location"), COCDTP145222GB02Place.class, place));
		
		location.setCOCDTP145222GB02HealthCareFacility(healthCareFacility);
		encounterEvent.setLocation(location);
		
		// TODO populate record target
		REPCMT200001GB02RecordTarget recordTarget = new REPCMT200001GB02RecordTarget();
		recordTarget.setTypeCode(recordTarget.getTypeCode());
		recordTarget.getContextControlCode().add("OP");
		TemplateContent recordTargetTemplateContent = new TemplateContent();
		recordTargetTemplateContent.setRoot("2.16.840.1.113883.2.1.3.2.4.18.16");
		recordTargetTemplateContent.setExtension("COCD_TP145201GB01#PatientRole");
		recordTarget.setContentId(recordTargetTemplateContent);
		
		COCDTP145201GB01PatientRole patientRole = new COCDTP145201GB01PatientRole();
		patientRole.setClassCode(patientRole.getClassCode());
		
		AD patientAddress = new AD();
		CareConnectPatient fhirPatient = (CareConnectPatient) fhirEncounter.getSubject().getResource();
		patientAddress.getContent().add(fhirPatient.getAddressFirstRep().getLine().get(0).getValue());
		patientAddress.getContent().add(fhirPatient.getAddressFirstRep().getLine().get(1).getValue());
		patientAddress.getContent().add(fhirPatient.getAddressFirstRep().getCity());
		patientAddress.getContent().add(fhirPatient.getAddressFirstRep().getPostalCode());
		patientAddress.getUse().add(CsPostalAddressUse.PHYS);
		patientRole.getAddr().add(patientAddress);
		
		IINPfITOidMandatoryAssignedAuthority patientId = new IINPfITOidMandatoryAssignedAuthority();
		patientId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.24");
		patientId.setExtension(fhirPatient.getId());
		patientRole.getId().add(patientId);
		TEL patientTel = new TEL();
		patientTel.setValue(fhirPatient.getTelecomFirstRep().getValue());
		patientRole.getTelecom().add(patientTel);
		COCDTP145201GB01PatientRole.TemplateId patientRoleTemplateId = new COCDTP145201GB01PatientRole.TemplateId();
		patientRoleTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		patientRoleTemplateId.setExtension("COCD_TP145201GB01#PatientRole");
		patientRole.setTemplateId(patientRoleTemplateId);
		
		COCDTP145201GB01Patient patient = new COCDTP145201GB01Patient();
		patient.setClassCode(patient.getClassCode());
		patient.setDeterminerCode(patient.getDeterminerCode());
		patient.setAdministrativeGenderCode(terminologyService.getCode(
				fhirPatient.getGender().getSystem(), administrativeGenderSystem, fhirPatient.getGender().getDefinition()));
		TS birthTime = new TS();
		birthTime.setValue(reportDateFormat.format(fhirPatient.getBirthDate()));
		patient.setBirthTime(birthTime);
		patientRole.setPatientPatient(patient);
		
		PN patientName = new PN();
		patientName.getContent().add(fhirPatient.getNameFirstRep().getNameAsSingleString());
		patientName.getUse().add(CsEntityNameUse.L);
		patient.getName().add(patientName);
		
		COCDTP145201GB01Patient.TemplateId patientTemplateId = new COCDTP145201GB01Patient.TemplateId();
		patientTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		patientTemplateId.setExtension("COCD_TP145201GB01#patientPatient");
		patient.setTemplateId(patientTemplateId);
		
		// ComunicationLanguage - TEMP
		COCDTP145201GB01LanguageCommunication language = new COCDTP145201GB01LanguageCommunication();
		CS languagecode = new CS();
				
		CVNPfITCodedplainRequired tempCode = terminologyService.getCode(fhirPatient.getCommunicationFirstRep().getLanguage().getCodingFirstRep().getSystem(), humanLanguageSystem, fhirPatient.getCommunicationFirstRep().getLanguage().getCodingFirstRep().getCode());
		languagecode.setCode(tempCode.getCode());
		languagecode.setCodeSystem(tempCode.getCodeSystem());
		languagecode.setDisplayName(tempCode.getDisplayName());
		
		COCDTP145201GB01LanguageCommunication.TemplateId languageTemplateId = new COCDTP145201GB01LanguageCommunication.TemplateId();
		languageTemplateId.setRoot("2.16.840.1.113883.2.1.3.2.4.18.2");
		languageTemplateId.setExtension("COCD_TP145201GB01#languageCommunication");
		language.setTemplateId(languageTemplateId);
				
		language.setLanguageCode(languagecode);
		patient.getLanguageCommunication().add(language);
		
		recordTarget.setCOCDTP145201GB01PatientRole(patientRole);
		encounterEvent.setRecordTarget(recordTarget);
		
		encounter.setFlag(encounterEvent);
		
		document.setPertinentInformation5(encounter);
	}

}
