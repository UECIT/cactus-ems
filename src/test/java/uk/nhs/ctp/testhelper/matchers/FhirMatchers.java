package uk.nhs.ctp.testhelper.matchers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.instance.model.api.IBaseResource;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FhirMatchers {

  public static Matcher<PrimitiveType<?>> typeWithValue(String value) {
    return new FunctionMatcher<>(type -> value.equals(type.getValueAsString()), value);
  }

  public static Matcher<IBaseResource> coordinate2d(int x, int y) {
    // Coordinate resource doesn't override 'equalsDeep' so have custom matcher for now
    return new FunctionMatcher<>(res -> {
      if (!(res instanceof CoordinateResource)) {return false;}
      var coords = ((CoordinateResource)res);
      return coords.getXCoordinate().getValue() == x
          && coords.getYCoordinate().getValue() == y;
    }, "x:" + x + " , y:" + y);
  }

  public static Matcher<Element> isFhir(Element expected) {
    return new FunctionMatcher<>(
        actual -> actual.equalsDeep(expected),
        expected.toString());
  }
  public static Matcher<Base> isFhir(Base expected) {
    return new FunctionMatcher<>(
        actual -> actual.equalsDeep(expected),
        expected.toString());
  }

}
