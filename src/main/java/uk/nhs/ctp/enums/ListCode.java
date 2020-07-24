package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum ListCode implements Concept {
  TRIAGE(SystemURL.SNOMED, "225390008", "Triage (procedure)");

  private final String system;
  private final String value;
  private final String display;
}
