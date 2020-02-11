package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum EncounterReason implements Concept {
  ARTHRITIS("3723001", "Arthritis");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
