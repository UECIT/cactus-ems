package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunicationMode implements Concept {
  RSP("Received spoken");

  private final String value = name();
  private final String system = "https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-LanguageAbilityMode-1";
  private final String display;
}
