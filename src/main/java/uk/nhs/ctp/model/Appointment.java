package uk.nhs.ctp.model;

import java.time.Duration;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import uk.nhs.ctp.enums.AppointmentStatus;
import uk.nhs.ctp.enums.AppointmentType;
import uk.nhs.ctp.enums.EncounterReason;
import uk.nhs.ctp.enums.PracticeSetting;
import uk.nhs.ctp.enums.ServiceCategory;
import uk.nhs.ctp.enums.ServiceType;

@Data
@Builder
public class Appointment {
  private String id;
  private AppointmentStatus status;
  private ServiceCategory serviceCategory;
  private ServiceType serviceType;
  private PracticeSetting specialty;
  private AppointmentType appointmentType;
  private EncounterReason reason;
  private String description;
  private Instant start;
  private Instant end;
  private Duration estimatedDuration;
  private Instant created;
  private String comment;
  private String practitionerId;
}
