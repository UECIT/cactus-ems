package uk.nhs.ctp.testhelper.fixtures;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.ProcedureRequest;

@UtilityClass
public class ProcedureRequestFixtures {

  public ProcedureRequest fhirProcedureRequest() {
    return new ProcedureRequest()
        .setCode(TestConcept.ANYTHING.toCodeableConcept());
  }

}
