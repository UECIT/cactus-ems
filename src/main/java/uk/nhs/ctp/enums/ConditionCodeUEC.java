package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum ConditionCodeUEC implements Concept {

  CHEST_PAIN("29857009", "Chest pain");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
