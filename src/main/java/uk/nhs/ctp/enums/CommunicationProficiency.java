package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommunicationProficiency implements Concept {
  E("Excellent");

  private final String value = name();
  private final String system = "https://fhir.hl7.org.uk/STU3/ValueSet/CareConnect-LanguageAbilityProficiency-1";
  private final String display;
}
