package uk.nhs.ctp.registry;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import uk.nhs.ctp.enums.AppointmentStatus;
import uk.nhs.ctp.enums.AppointmentType;
import uk.nhs.ctp.enums.EncounterReason;
import uk.nhs.ctp.enums.PracticeSetting;
import uk.nhs.ctp.enums.ServiceCategory;
import uk.nhs.ctp.enums.ServiceType;
import uk.nhs.ctp.model.Appointment;

public class AppointmentRegistry implements Registry<Appointment> {

  public List<Appointment> getAll() {
    var now = Instant.now();
    return List.of(Appointment.builder()
        .status(AppointmentStatus.BOOKED)
        .serviceCategory(ServiceCategory.SPECIALIST_MEDICAL)
        .serviceType(ServiceType.GENERAL_PRACTICE)
        .specialty(PracticeSetting.GENERAL_MEDICAL_PRACTICE)
        .appointmentType(AppointmentType.ROUTINE)
        .reason(EncounterReason.ARTHRITIS)
        .description("Head to our main wing at the designated time for your routine appointment")
        .start(now.plus(Duration.ofDays(5)).plus(Duration.ofHours(2)))
        .end(now.plus(Duration.ofDays(5)).plus(Duration.ofHours(3)))
        .estimatedDuration(Duration.ofHours(1))
        .created(now)
        .comment("This is not a real appointment.")
        .build());
  }
}
