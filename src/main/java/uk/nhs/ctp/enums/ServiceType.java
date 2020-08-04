package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceType implements Concept {
  GENERAL_PRACTICE("124", "General Practice");

  private final String system = "http://hl7.org/fhir/service-type";
  private final String value;
  private final String display;
}
