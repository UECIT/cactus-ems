package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceCategory implements Concept {

  ART_MUSIC_DRAMA("R0540", "Art, Music & Drama Student");

  private final String system = "https://fhir.hl7.org.uk/STU3/CodeSystem/CareConnect-SDSJobRoleName-1";
  private final String value;
  private final String display;
}
