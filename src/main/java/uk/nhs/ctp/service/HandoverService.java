package uk.nhs.ctp.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Appointment;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus;
import org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle.BundleTypeEnumFactory;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Composition.CompositionStatus;
import org.hl7.fhir.dstu3.model.Composition.DocumentConfidentiality;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterLocationComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.EpisodeOfCare;
import org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Patient.PatientCommunicationComponent;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Provenance;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.repos.AuditRecordRepository;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@Service
public class HandoverService {
	
	@Autowired
	private CaseRepository caseRepository;
	
	@Autowired 
	private AuditRecordRepository auditRecordRepository;
	
	@Autowired
	ObjectMapper mapper;

	public String getHandoverMessage(String url, Long caseId) throws MalformedURLException, JsonProcessingException {
		
		FhirContext ctx = FhirContext.forDstu3();
		IGenericClient client = getClient(ctx, url);
		
		// Get Default resources
		Cases caseEntity = caseRepository.findOne(caseId);
		AuditRecord auditRecord = auditRecordRepository.findByCaseId(caseId);
		Bundle bundle = buildDefaultBundle();
		
		auditRecord.setAuditEntries(auditRecord.getAuditEntries().stream()
                .filter(entry -> entry.getType() != AuditEntryType.RESULT)
                .collect(Collectors.toList()));
		
		AuditEntry lastEntry = auditRecord.getAuditEntries().get(auditRecord.getAuditEntries().size()-1);
		Bundle containedBundle = ctx.newJsonParser().parseResource(Bundle.class, lastEntry.getContained());

		ReferralRequest referralRequest = client != null ? 
				getReferralRequest(url, client) :
				ResourceProviderUtils.getResource(containedBundle, ReferralRequest.class);
				
		// Add Resources to Bundle
		Practitioner generalPractitioner = getGeneralPractitioner();
		Patient patient = getPatient(caseEntity);
		patient.addGeneralPractitioner(new Reference(generalPractitioner));
		Encounter encounter = getEncounter(patient);
		Composition composition = getComposition(encounter, patient);
		
		Practitioner practitioner = getRecipient(caseEntity);
		addResourceToBundle(bundle, composition);
		addResourceToBundle(bundle, patient);
		addResourceToBundle(bundle, practitioner);
		
		buildCareAdvice(ctx, lastEntry, bundle);
		
		addBasedOn(client, containedBundle, referralRequest);
		addRelevantHistory(client, containedBundle, referralRequest);
		
		buildAppointment(bundle, referralRequest);
		
		addQuestionnaireResources(ctx, auditRecord, bundle);
		
		// add bundle to referralRequest
		referralRequest.addSupportingInfo(new Reference(bundle));
		return ctx.newJsonParser().encodeResourceToString(referralRequest);
	}

	public void buildAppointment(Bundle bundle, ReferralRequest referralRequest) {
		Appointment appointment = new Appointment();
		appointment.setStatus(AppointmentStatus.PROPOSED);
		appointment.setDescription("There is no appointment required");
		appointment.addParticipant();
		appointment.getParticipantFirstRep().setStatus(ParticipationStatus.TENTATIVE);
		
		appointment.setId("#appointment");
		referralRequest.addSupportingInfo().setReference("#appointment");
		referralRequest.addContained(appointment);
	}

	private void buildCareAdvice(FhirContext ctx, AuditEntry auditEntry, Bundle bundle) {
		CarePlan resultCarePlan = new CarePlan();
		resultCarePlan.setTitle("Result");
		resultCarePlan.setDescription("You should follow this advice within the next 12 hour period");
		
		CarePlan beforeYouGoCarePlan = new CarePlan();
		beforeYouGoCarePlan.setTitle("Before you go");
		beforeYouGoCarePlan.setDescription("take all your current medicines with you");
		
		CarePlan whatYouCanDoCarePlan = new CarePlan();
		whatYouCanDoCarePlan.setTitle("What you can do in the meantime");
		whatYouCanDoCarePlan.setDescription("Call 999 if you're symptoms are getting worse.");
		
		if (auditEntry.getContained() != null) {
			Bundle containedBundle = ctx.newJsonParser().parseResource(Bundle.class, auditEntry.getContained());
			CarePlan carePlan = ResourceProviderUtils.getResource(containedBundle, CarePlan.class);
			if (carePlan != null) 
					addResourceToBundle(bundle, carePlan);
		}
		
		
		addResourceToBundle(bundle, resultCarePlan);
		addResourceToBundle(bundle, beforeYouGoCarePlan);
		addResourceToBundle(bundle, whatYouCanDoCarePlan);
	}

	public Encounter getEncounter(Patient patient) {
		Encounter encounter = new Encounter();
		// Set the status of the encounter
		encounter.setStatus(EncounterStatus.FINISHED);
		// Set the class of the encounter
		encounter.setClass_(new Coding()
				.setCode("unscheduled")
				.setDisplay("unscheduled")
				.setSystem("http://hl7.org/fhir/v3/ActCode"));
		// Set the type of the priority
		encounter.addType(new CodeableConcept().addCoding(new Coding()
				.setCode("OKI")
				.setDisplay("Outpatient Kenacort injection")
				.setSystem("http://hl7.org/fhir/ValueSet/encounter-type")));
		// Set the priority of the encounter
		encounter.setPriority(new CodeableConcept().addCoding(new Coding()
				.setCode("A")
				.setDisplay("ASAP")
				.setSystem("http://hl7.org/fhir/ValueSet/v3-ActPriority")));
		// Set the subject of the encounter(patient)
		encounter.setSubject(new Reference(patient));

		// Populate the EpisodeOfCare resource - Episode(s) of care that this encounter
		// should be recorded against
		EpisodeOfCare episodeOfCare = new EpisodeOfCare();
		episodeOfCare.setStatus(EpisodeOfCareStatus.ACTIVE);
		episodeOfCare.setPatient(new Reference(patient));
		encounter.addEpisodeOfCare(new Reference(episodeOfCare));

		// Populate the Participant resource
		EncounterParticipantComponent encounterParticipant = new EncounterParticipantComponent();
		encounterParticipant.addType(new CodeableConcept().addCoding(new Coding()
				.setCode("ATND")
				.setDisplay("attender")
				.setSystem("http://hl7.org/fhir/v3/ParticipationType")));
		Period period = new Period();
		period.setStart(new Date());
		period.setEnd(new Date());
		encounterParticipant.setPeriod(period);
		encounterParticipant.setIndividual(patient.getGeneralPractitionerFirstRep());
		encounter.addParticipant(encounterParticipant);
		// Populate the Period resource - The start and end time of the encounter
		encounter.setPeriod(period);
		// Populate the length resource - Quantity of time the encounter lasted (less
		// time absent)
		Duration duration = new Duration();
		duration.setValue(10L);
		duration.setUnitElement(new StringType("minutes"));
		duration.setSystem("http://unitsofmeasure.org");
		duration.setCode("min");
		encounter.setLength(duration);
		// TODO Populate the Location resource - List of locations where the patient has been
		encounter.addLocation(new EncounterLocationComponent(new Reference(new Location())));

		return encounter;
	}

	public void addQuestionnaireResources(FhirContext ctx, AuditRecord auditRecord, Bundle bundle) {
		auditRecord.getAuditEntries().stream().forEach(auditEntry -> {
			if (auditEntry.getType() != AuditEntryType.RESULT) {
				getQuestionnaireResponse(ctx, bundle, auditEntry);
				getParameters(ctx, bundle, auditEntry);
				getQuestionnaire(ctx, bundle, auditEntry);
			}
		});
	}

	public void addRelevantHistory(IGenericClient client, Bundle containedResources, ReferralRequest referralRequest) {
		if (referralRequest.hasRelevantHistory()) {
			Provenance provenance = client == null ? ResourceProviderUtils.getResource(containedResources, Provenance.class)
					: getRelevantHistory(client, referralRequest);
			
			provenance.setId("#provenance");
			referralRequest.getRelevantHistoryFirstRep().setReference("#provenance");
			referralRequest.getRelevantHistoryFirstRep().setResource(null);
			referralRequest.addContained(provenance);
		}
	}

	public void addBasedOn(IGenericClient client, Bundle containedResources, ReferralRequest referralRequest) {
		if (referralRequest.hasBasedOn()) {
			ProcedureRequest procedureRequest = client == null ? 
					ResourceProviderUtils.getResource(containedResources, ProcedureRequest.class) : 
					getBasedOn(client, referralRequest);
					
			procedureRequest.setId("#procedureRequest");
			referralRequest.getBasedOnFirstRep().setReference("#procedureRequest");
			referralRequest.addContained(procedureRequest);
		}
	}

	public void getParameters(FhirContext ctx, Bundle bundle, AuditEntry auditEntry) {
		if (auditEntry.getContained() != null) {
			Bundle containedBundle = ctx.newJsonParser().parseResource(Bundle.class, auditEntry.getContained());
			Parameters parameters = ResourceProviderUtils.getResource(containedBundle, Parameters.class);
			if (parameters != null) 
					addResourceToBundle(bundle, parameters.getParameterFirstRep().getResource());
		}
	}

	public void getQuestionnaire(FhirContext ctx, Bundle bundle, AuditEntry auditentry) {
		if (auditentry.getCdssQuestionnaireResponse() != null) {
			Questionnaire questionnaire = (Questionnaire)ctx.newJsonParser().parseResource(auditentry.getCdssQuestionnaireResponse());
			addResourceToBundle(bundle, questionnaire);
		}
	}

	public void getQuestionnaireResponse(FhirContext ctx, Bundle bundle, AuditEntry auditentry) {
		if (auditentry.getCdssServiceDefinitionRequest() != null) {
			Bundle containedBundle = ctx.newJsonParser().parseResource(Bundle.class, auditentry.getCdssServiceDefinitionRequest());
			Parameters parameters = ResourceProviderUtils.getResource(containedBundle, Parameters.class);
			parameters.getParameter().stream().forEach(param -> {
				if(param.hasResource() && param.getResource().getResourceType().equals(ResourceType.QuestionnaireResponse)) {
					QuestionnaireResponse questionnaireResponse = (QuestionnaireResponse) param.getResource();
					addResourceToBundle(bundle, questionnaireResponse);
				}
			});
		}
	}

	public Composition getComposition(Encounter encounter, Patient patient) {
		Composition composition = new Composition();
		composition.setStatus(CompositionStatus.FINAL);
		composition.setType(new CodeableConcept()
				.setText("Transfer summary report")
				.addCoding(new Coding()
					.setCode("371535009")
					.setDisplay("Transfer summary report")
					.setSystem(SystemURL.SNOMED)));
		composition.setTitle("Transfer summary report");
		composition.setSubject(new Reference(patient));
		composition.setEncounter(new Reference(encounter));
		composition.setConfidentiality(DocumentConfidentiality.N);
		return composition;
	}

	public void addResourceToBundle(Bundle bundle, Resource resource) {
		BundleEntryComponent entry = new BundleEntryComponent();
		entry.setResource(resource);
		bundle.addEntry(entry);
	}

	public Bundle buildDefaultBundle() {
		Bundle bundle = new Bundle();
		BundleTypeEnumFactory bundleTypeEnumFactory = new BundleTypeEnumFactory();
		bundle.setType(bundleTypeEnumFactory.fromCode("document"));
		bundle.setId("#resourceBundle");
		return bundle;
	}
	
	public ReferralRequest getReferralRequest(String url, IGenericClient client) throws MalformedURLException {
		ReferralRequest referralRequest = client.read().resource(ReferralRequest.class).withUrl(url).execute();
		return referralRequest;
	}


	private Provenance getRelevantHistory(IGenericClient client, ReferralRequest referralRequest) {
		Provenance relevantHistory = client.read().resource(Provenance.class).withUrl(referralRequest.getRelevantHistoryFirstRep().getReference()).execute();
		return relevantHistory;
	}


	private ProcedureRequest getBasedOn(IGenericClient client, ReferralRequest referralRequest) {
		ProcedureRequest basedOn = client.read().resource(ProcedureRequest.class).withUrl(referralRequest.getBasedOnFirstRep().getReference()).execute();
		return basedOn;
	}

	private Practitioner getRecipient(Cases caseEntity) {
		Practitioner practitioner = new Practitioner();
		
		Identifier identifier = new Identifier();
		identifier.setSystem("https://fhir.nhs.uk/Id/nhs-number");
		identifier.setValue("9476719917");
		practitioner.getIdentifier().add(identifier);
		
		HumanName name = new HumanName();
		name.addSuffix("Dr");
		name.addGiven("John");
		name.setFamily("Blog");
		practitioner.getName().add(name);
		
		practitioner.setGender(AdministrativeGender.MALE);
		practitioner.setBirthDate(new Date());
		
		Address gpAddress = new Address();
		gpAddress.addLine("Durham Emergency Department");
		gpAddress.addLine("Sunderland Rd");
		gpAddress.setCity("Durham");
		gpAddress.setPostalCode("S12 2L1");
		practitioner.addAddress(gpAddress);
		
		CodeableConcept practitionerQualification = new CodeableConcept();
		practitionerQualification.addCoding();
		practitionerQualification.getCodingFirstRep().setSystem(SystemURL.SNOMED);
		practitionerQualification.getCodingFirstRep().setCode("62247001");
		practitionerQualification.getCodingFirstRep().setDisplay("GP - General practitioner");
		practitioner.addQualification(new PractitionerQualificationComponent(practitionerQualification));
		
		return practitioner;
	}


	private Patient getPatient(Cases caseEntity) {
		Patient patient = new Patient();
		
		Identifier identifier = new Identifier();
		identifier.setSystem("https://fhir.nhs.uk/Id/nhs-number");
		identifier.setValue(caseEntity.getNhsNumber());
		patient.getIdentifier().add(identifier);
		
		HumanName name = new HumanName();
		name.addGiven(caseEntity.getFirstName());
		name.setFamily(caseEntity.getLastName());
		patient.getName().add(name);
		
		patient.setGender(getGender(caseEntity.getGender()));
		patient.setBirthDate(caseEntity.getDateOfBirth());
		
		CodeableConcept language = new CodeableConcept();
		language.addCoding().setCode("en").setDisplay("English").setSystem("http://uecdi-tom-terminology.eu-west-2.elasticbeanstalk.com/fhir/CodeSystem/languages");
		patient.addCommunication(new PatientCommunicationComponent(language));
		
		Address address = new Address();
		address.addLine("2 St Hilds Court");
		address.addLine("Renny's Ln");
		address.setCity("Durham");
		address.setPostalCode("DH1 2HP");
		patient.addAddress(address);
		
		return patient;
	}
	
	private Practitioner getGeneralPractitioner () {
		// Build GeneralPractitioner
		Practitioner generalPractitioner = new Practitioner();
		generalPractitioner.setId("#1234");
		HumanName gpName = new HumanName();
		gpName.addPrefix("Dr");
		gpName.addGiven("M");
		gpName.setFamily("Khan");
		generalPractitioner.addName(gpName);

		Address gpAddress = new Address();
		gpAddress.addLine("Dunelm Medical Practice");
		gpAddress.addLine("Gilesgate Medical Centre");
		gpAddress.addLine("Sunderland Rd");
		gpAddress.setCity("Durham");
		gpAddress.setPostalCode("S25 4HE");
		generalPractitioner.addAddress(gpAddress);

		PractitionerQualificationComponent practitionerQualificationComponent = new PractitionerQualificationComponent();
		CodeableConcept codeableConcept = new CodeableConcept();
		codeableConcept.addCoding().setCode("62247001").setDisplay("General practitioner").setSystem(SystemURL.SNOMED);
		practitionerQualificationComponent.setCode(codeableConcept);
		generalPractitioner.addQualification(practitionerQualificationComponent);

		return generalPractitioner;
	}
			
	
	private IGenericClient getClient(FhirContext ctx, String url) {
		try {
			IGenericClient client = ctx.newRestfulGenericClient(buildBaseUrl(url));
			return client;
		} catch (Exception e) {
			return null;
		}
	}
	
	private String buildBaseUrl(String url) throws MalformedURLException {
		URL uri = new URL(url);
		if (url.contains("/fhir")) {
			return uri.getProtocol() + "://" + uri.getAuthority() + "/fhir";
		} else {
			return uri.getProtocol() + "://" + uri.getAuthority();
		}
	}
	
	private AdministrativeGender getGender(String gender) {
		if (gender.contentEquals("male")) {
			return AdministrativeGender.MALE;
		}
		if (gender.contentEquals("female")) {
			return AdministrativeGender.FEMALE;
		}
		return AdministrativeGender.UNKNOWN;
	}

}
