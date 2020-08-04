package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum DocumentSectionCode implements Concept {
  OBSERVATIONS("1102421000000108", "Observations"),
  PLAN_AND_REQUESTED_ACTIONS("887201000000105", "Plan and requested actions");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
