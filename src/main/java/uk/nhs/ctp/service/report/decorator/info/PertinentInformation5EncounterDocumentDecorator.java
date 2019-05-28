package uk.nhs.ctp.service.report.decorator.info;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.RelatedPerson;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.nhs.ctp.service.dto.ReportRequestDTO;
import uk.nhs.ctp.service.report.decorator.AmbulanceDecorator;
import uk.nhs.ctp.service.report.decorator.mapping.template.encompassingencounter.location.HealthCareFacilityREPCMT200001GB02TemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.informant.InformantREPCMT200001GB02TemplateResolver;
import uk.nhs.ctp.service.report.decorator.mapping.template.patient.PatientTemplateResolver;
import uk.nhs.ctp.service.report.org.hl7.v3.II;
import uk.nhs.ctp.service.report.org.hl7.v3.IVLTS;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02AmbulanceRequest;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02EncounterEvent;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Informant;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02Location;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation;
import uk.nhs.ctp.service.report.org.hl7.v3.REPCMT200001GB02PertinentInformation.SeperatableInd;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Component
public class PertinentInformation5EncounterDocumentDecorator implements AmbulanceDecorator  {
	
	@Autowired
	private InformantREPCMT200001GB02TemplateResolver<? extends IBaseResource> informantTemplate;
	
	@Autowired 
	private HealthCareFacilityREPCMT200001GB02TemplateResolver<? extends IBaseResource> healthCareFacilityTemplate;
	
	@Autowired
	private PatientTemplateResolver <? extends IBaseResource> patientTemplate;
	
	@Value("${ems.terminology.administrative.gender.system}")
	private String administrativeGenderSystem;
	
	@Value("${ems.terminology.administrative.gender.oid}")
	private String administrativeGenderOid;
	
	@Value("${ems.terminology.human.language.system}")
	private String humanLanguageSystem;
	
	@Value("${ems.terminology.human.language.oid}")
	private String humanLanguageOid;

	@Override
	public void decorate(REPCMT200001GB02AmbulanceRequest document, ReportRequestDTO request) {
		
		Encounter fhirEncounter = (Encounter) ResourceProviderUtils.getResource(request.getBundle(), Composition.class).getEncounter().getResource();
		RelatedPerson fhirInformant = (RelatedPerson) request.getReferralRequest().getRequester().getAgent().getResource();
		
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
		
		REPCMT200001GB02Informant informant = informantTemplate.resolve(fhirInformant, request);
		encounterEvent.setInformant(new JAXBElement<REPCMT200001GB02Informant>(new QName("urn:hl7-org:v3", "informant"), REPCMT200001GB02Informant.class, informant));
		
		REPCMT200001GB02Location location = healthCareFacilityTemplate.resolve(fhirEncounter.getLocationFirstRep().getLocation().getResource(), request);
		encounterEvent.setLocation(location);
		
		encounterEvent.setRecordTarget(patientTemplate.resolve(fhirEncounter.getSubject().getResource(), request));
		
		encounter.setFlag(encounterEvent);
		
		document.setPertinentInformation5(encounter);
	}

}
