package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum DeviceKind implements Concept {

  APPLICATION_SOFTWARE("706689003", "Application program software");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;

}
