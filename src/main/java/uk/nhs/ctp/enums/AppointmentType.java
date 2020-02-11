package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppointmentType implements Concept {
  CHECKUP("Routine check-up"),
  EMERGENCY("Emergency appointment"),
  FOLLOWUP("Follow up visit"),
  ROUTINE("Routine appointment"),
  WALKIN("Unscheduled walk-in visit");

  private final String system = "http://hl7.org/fhir/v2/0276";
  private final String value = name();
  private final String display;
}
