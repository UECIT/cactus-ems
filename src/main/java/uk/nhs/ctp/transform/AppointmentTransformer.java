package uk.nhs.ctp.transform;

import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus;
import org.hl7.fhir.dstu3.model.Appointment.ParticipantRequired;
import org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus;
import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.DocumentReference.DocumentReferenceContentComponent;
import org.hl7.fhir.dstu3.model.Enumerations.DocumentReferenceStatus;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.Schedule;
import org.hl7.fhir.dstu3.model.Slot;
import org.hl7.fhir.dstu3.model.Slot.SlotStatus;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.builder.CareConnectPatientBuilder;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.model.Appointment;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;

@Component
@RequiredArgsConstructor
public class AppointmentTransformer
    implements Transformer<Appointment, org.hl7.fhir.dstu3.model.Appointment> {

  private final PatientRepository patientRepository;
  private final ReferenceService referenceService;
  private final CareConnectPatientBuilder patientBuilder;
  private final StorageService storageService;

  @Override
  public org.hl7.fhir.dstu3.model.Appointment transform(Appointment from) {
    var appointment = new org.hl7.fhir.dstu3.model.Appointment();

    appointment.setId(from.getId());
    appointment.setStatus(AppointmentStatus.fromCode(from.getStatus().toCode()));
    appointment.setServiceCategory(from.getServiceCategory().toCodeableConcept());
    appointment.addServiceType(from.getServiceType().toCodeableConcept());
    appointment.addSpecialty(from.getSpecialty().toCodeableConcept());
    appointment.setAppointmentType(from.getAppointmentType().toCodeableConcept());
    appointment.addReason(from.getReason().toCodeableConcept());
    appointment.setDescription(from.getDescription());
    appointment.setComment(from.getComment());
    appointment.setStart(Date.from(from.getStart()));
    appointment.setEnd(Date.from(from.getEnd()));
    appointment.setMinutesDuration((int)from.getEstimatedDuration().toMinutes());
    appointment.setCreated(Date.from(from.getCreated()));
    appointment.addSlot(new Reference(createSlot(from)));
    appointment.addIncomingReferral(referenceService.buildRef(ResourceType.ReferralRequest, from.getReferral()));

    PatientEntity patientEntity = patientRepository.findById(new IdType(from.getPatientId()).getIdPartAsLong());
    CareConnectPatient patient = patientBuilder.build(patientEntity);
    appointment.addParticipant()
        .setActor(new Reference(patient.setId("#patient")))
        .setRequired(ParticipantRequired.REQUIRED)
        .setStatus(ParticipationStatus.ACCEPTED);
    appointment.addContained(patient); // Must be contained according to scheduling profile.

    DocumentReference document = createDocument();
    appointment.addSupportingInformation(new Reference(document));
    appointment.addContained(document); // Must be contained according to scheduling profile

    return appointment;
  }

  private DocumentReference createDocument() {
    DocumentReference documentReference = new DocumentReference();
    documentReference.setId("#document");
    documentReference.setStatus(DocumentReferenceStatus.CURRENT);
    documentReference.setIndexed(new Date());
    documentReference.addIdentifier(
        new Identifier().setSystem(SystemURL.APPT_DOCUMENT_IDENTIFIER).setValue(UUID.randomUUID().toString()));
    documentReference.addContent(new DocumentReferenceContentComponent(
        new Attachment().setContentType("text/plain").setLanguage("en")));
    return documentReference;
  }

  private Slot createSlot(Appointment from) {
    Slot slot = new Slot();
    slot.setAppointmentType(from.getAppointmentType().toCodeableConcept());
    slot.addSpecialty(from.getSpecialty().toCodeableConcept());
    slot.addServiceType(from.getServiceType().toCodeableConcept());
    slot.setStatus(SlotStatus.BUSY);
    slot.setStart(Date.from(from.getStart()));
    slot.setEnd(Date.from(from.getEnd()));
    slot.setSchedule(new Reference(createSchedule(from)));
    storageService.storeExternal(slot);
    return slot;
  }

  private Schedule createSchedule(Appointment from) {
    Schedule schedule = new Schedule()
        .setActive(true)
        .addServiceType(from.getServiceType().toCodeableConcept())
        .addSpecialty(from.getSpecialty().toCodeableConcept())
        .addActor(from.getHealthcareService());

    storageService.storeExternal(schedule);
    return schedule;
  }
}
