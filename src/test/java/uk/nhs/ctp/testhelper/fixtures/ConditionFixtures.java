package uk.nhs.ctp.testhelper.fixtures;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;

@UtilityClass
public class ConditionFixtures {

  public Condition fhirCondition() {
    return new Condition()
        .setCode(new CodeableConcept(new Coding("condsys", "condcode", "conddisp")));
  }

}
