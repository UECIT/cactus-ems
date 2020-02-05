package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ParticipationType implements Concept {

  PPRF("primary performer"),
  SPRF("secondary performer"),
  ATND("attender"),
  ADM("admitter"),
  DIS("discharger");

  private final String system = "http://hl7.org/fhir/v3/ParticipationType";
  private final String value = name();
  private final String display;
}
