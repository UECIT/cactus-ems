package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import uk.nhs.ctp.SystemURL;

@Getter
@RequiredArgsConstructor
public enum DocumentType implements Concept {
  INVESTIGATION_RESULT("24641000000107", "Investigation result"),
  OUTPATIENT_MEDICAL_NOTE("820491000000108", "Outpatient medical note"),
  REPORT_CLINICAL_ENCOUNTER("371531000", "Report of clinical encounter (record artifact)"),
  REPORT("229059009 ", "Report");

  private final String system = SystemURL.SNOMED;
  private final String value;
  private final String display;
}
