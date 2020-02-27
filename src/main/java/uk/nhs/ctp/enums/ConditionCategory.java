package uk.nhs.ctp.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum  ConditionCategory implements Concept{

  PROBLEM_LIST("problem-list-item", "Problem List Item"),
  ENCOUNTER_DIAGNOSIS("encounter-diagnosis", "Encounter Diagnosis");

  private final String system = "https://www.hl7.org/fhir/STU3/codesystem-condition-category.html";
  private final String value;
  private final String display;

}
