package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum PracticeSetting implements Concept {
  GENERAL_MEDICAL_PRACTICE("408443003", "General medical practice");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
