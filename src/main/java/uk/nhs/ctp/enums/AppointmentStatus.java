package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AppointmentStatus implements Concept {
  PROPOSED("proposed", "Proposed"),
  PENDING("pending", "Pending"),
  BOOKED("booked", "Booked"),
  ARRIVED("arrived", "Arrived"),
  FULFILLED("fulfilled", "Fulfilled"),
  CANCELLED("cancelled", "Cancelled"),
  NO_SHOW("noshow", "No Show"),
  ENTERED_IN_ERROR("entered-in-error", "Entered in error");

  private final String system = "http://hl7.org/fhir/appointmentstatus";
  private final String value;
  private final String display;
}
