package uk.nhs.ctp.testhelper.matchers;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hl7.fhir.dstu3.model.Base;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Type;
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
  public static <T extends Base> Matcher<T> isFhir(T expected) {
    return new FunctionMatcher<>(
        actual -> actual.equalsDeep(expected),
        expected.toString());
  }

  public static Matcher<Reference> referenceTo(String ref) {
    return new FunctionMatcher<>(actual ->
        actual.hasReference()
            ? actual.getReference().equals(ref)
            : actual.getResource().getIdElement().getValue().equals(ref), "reference to " + ref);
  }

  public static Matcher<Reference> referenceTo(Resource resource) {
    return referenceTo(resource.getId());
  }

  @SafeVarargs
  public static Matcher<Parameters> isParametersContaining(
      Matcher<ParametersParameterComponent>... matchers) {
    return new FunctionMatcher<>(
        parameters -> Matchers.contains(matchers).matches(parameters.getParameter()),
        "is a Parameters resource");
  }

  public static Matcher<ParametersParameterComponent> isParameter(String name, Resource value) {
    return new FunctionMatcher<>(
        parameter -> name.equals(parameter.getName()) && value.equalsDeep(parameter.getResource()),
        "is Parameter with name " + name);
  }

  public static Matcher<ParametersParameterComponent> isParameter(String name, Type value) {
    return new FunctionMatcher<>(
        parameter -> name.equals(parameter.getName()) && value.equalsDeep(parameter.getValue()),
        "is parameter with type = " + value);
  }

  public static Matcher<ParametersParameterComponent> isParameter(String name, Matcher<?> valueMatcher) {
    return new FunctionMatcher<>(
        parameter -> name.equals(parameter.getName()) && valueMatcher.matches(parameter.getValue()),
        "value matching given matcher");
  }

}
