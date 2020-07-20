package uk.nhs.ctp.testhelper.fixtures;

import lombok.Getter;
import uk.nhs.ctp.enums.Concept;

@Getter
public enum TestConcept implements Concept {

  ANYTHING;

  private String system = "test.system";
  private String display = "Test Concept";
  private String value = "testval";

}
