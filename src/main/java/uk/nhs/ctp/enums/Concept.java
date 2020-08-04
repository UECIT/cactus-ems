package uk.nhs.ctp.enums;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import uk.nhs.ctp.service.report.org.hl7.v3.Code;

public interface Concept {

  String getDisplay();
  String getSystem();
  String getValue();

  default CodeableConcept toCodeableConcept() {
    final var coding = toCoding();
    return new CodeableConcept().addCoding(coding).setText(coding.getDisplay());
  }

  default Coding toCoding() {
    return new Coding()
        .setCode(getValue())
        .setDisplay(getDisplay())
        .setSystem(getSystem());
  }

  default String toCode() {
    return getValue();
  }
}
