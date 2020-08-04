package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum Qualification implements Concept {
  GP("62247001", "General practitioner");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
