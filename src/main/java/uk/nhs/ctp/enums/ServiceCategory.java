package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceCategory implements Concept {
  SPECIALIST_MEDICAL("27", "Specialist Medical");

  private final String system = "http://hl7.org/fhir/service-category";
  private final String value;
  private final String display;
}
