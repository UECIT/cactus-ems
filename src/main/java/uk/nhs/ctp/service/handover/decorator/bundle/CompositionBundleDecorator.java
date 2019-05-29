package uk.nhs.ctp.service.handover.decorator.bundle;

import java.util.Date;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Composition.CompositionStatus;
import org.hl7.fhir.dstu3.model.Composition.DocumentConfidentiality;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.EpisodeOfCare.EpisodeOfCareStatus;
import org.hl7.fhir.dstu3.model.Period;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.springframework.stereotype.Component;

import resources.CareConnectEncounter;
import resources.CareConnectEncounter.EncounterLocationComponent;
import resources.CareConnectEncounter.EncounterParticipantComponent;
import resources.CareConnectEpisodeOfCare;
import resources.CareConnectLocation;
import resources.CareConnectPatient;
import uk.nhs.ctp.SystemURL;

@Component
public class CompositionBundleDecorator extends BundleDecorator<CareConnectPatient, Composition> {

	public void decorate(Bundle bundle, CareConnectPatient patient) {
		CareConnectEncounter encounter = new CareConnectEncounter();
		// Set the status of the encounter
		encounter.setStatus(EncounterStatus.FINISHED);
		// Set the class of the encounter
		encounter.setClass_(new Coding()
				.setCode("VR")
				.setDisplay("virtual")
				.setSystem("http://terminology.hl7.org/CodeSystem/v3-ActCode"));
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
		CareConnectEpisodeOfCare episodeOfCare = new CareConnectEpisodeOfCare();
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
		
		encounterParticipant.setIndividual(patient.getGeneralPractitioner().get(0));
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
		
		encounter.addLocation(new EncounterLocationComponent(new Reference(
				new CareConnectLocation().setAddress(patient.getAddressFirstRep()).setName("Patients Address"))));
		
		Composition composition = new Composition();
		composition.setStatus(CompositionStatus.FINAL);
		composition.setType(new CodeableConcept()
				.setText("Transfer summary report")
				.addCoding(new Coding()
					.setCode("371535009")
					.setDisplay("Transfer summary report")
					.setSystem(SystemURL.SNOMED)));
		composition.setTitle("Transfer summary report");
		composition.setSubject(new Reference(encounter.getSubjectTarget()));
		composition.setEncounter(new Reference(encounter));
		composition.setConfidentiality(DocumentConfidentiality.N);

		addToBundle(bundle, composition);
	}
}
