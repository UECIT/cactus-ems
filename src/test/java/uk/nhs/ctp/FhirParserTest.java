package uk.nhs.ctp;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Assert;
import org.junit.Test;

public class FhirParserTest {

  private final String A_HOST = "https://pseudohost:1337/fhir";
  private final String RESOURCE_ID = "Patient/should-strip";

  @Test
  public void fhirParser_withLocalReference_shouldNotStripBaseUrl() {
    var absoluteUrl = A_HOST + "/" + RESOURCE_ID;

    var parsedUrl = stringifyThenParse(absoluteUrl);

    Assert.assertEquals(RESOURCE_ID, parsedUrl);
  }

  @Test
  public void fhirParser_withExternalReference_shouldStripBaseUrl() {
    var absoluteUrl = "https://externalhost:1337/fhir/" + RESOURCE_ID;

    var parsedUrl = stringifyThenParse(absoluteUrl);

    Assert.assertEquals(absoluteUrl, parsedUrl);
  }

  private Parameters wrap(Reference reference) {
    var parameters = new Parameters();
    parameters.addParameter().setValue(reference);
    return parameters;
  }
  private Reference unwrap(Parameters parameters) {
    return (Reference) parameters.getParameterFirstRep().getValue();
  }

  private String stringifyThenParse(String url) {
    var parser = FhirContext.forDstu3().newJsonParser().setServerBaseUrl(A_HOST);
    var serialised = parser.encodeResourceToString(wrap(new Reference(url)));
    return unwrap((Parameters) parser.parseResource(serialised)).getReference();
  }
}
