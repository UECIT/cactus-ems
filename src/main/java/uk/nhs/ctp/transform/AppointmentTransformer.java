package uk.nhs.ctp.transform;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Appointment.AppointmentStatus;
import org.hl7.fhir.dstu3.model.Appointment.ParticipantRequired;
import org.hl7.fhir.dstu3.model.Appointment.ParticipationStatus;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.Appointment;
import uk.nhs.ctp.service.ReferenceService;

@Component
@RequiredArgsConstructor
public class AppointmentTransformer
    implements Transformer<Appointment, org.hl7.fhir.dstu3.model.Appointment> {

  private final ReferenceService referenceService;

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
    appointment.addParticipant()
        .setActor(referenceService.buildRef(ResourceType.Practitioner, from.getPractitionerId()))
        .setRequired(ParticipantRequired.REQUIRED)
        .setStatus(ParticipationStatus.ACCEPTED);

    return appointment;
  }
}
