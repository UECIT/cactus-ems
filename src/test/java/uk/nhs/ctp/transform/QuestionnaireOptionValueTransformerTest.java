package uk.nhs.ctp.transform;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;

import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemOptionComponent;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.TimeType;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.nhs.ctp.service.dto.TriageExtension;
import uk.nhs.ctp.service.dto.TriageOption;

public class QuestionnaireOptionValueTransformerTest {

  private QuestionnaireOptionValueTransformer optionValueTransformer;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    optionValueTransformer = new QuestionnaireOptionValueTransformer();
  }

  @Test
  public void shouldTransformCodingType() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new Coding("sys", "code", "disp"));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("code", "disp");
    assertThat(option, sameBeanAs(expected));
  }

  @Test
  public void shouldTransformWithExtension() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new Coding("sys", "code", "disp"));
    optionComponent.addExtension("some.extension.url", new StringType("extensionval"));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("code", "disp",
        new TriageExtension("some.extension.url", "extensionval"));
    assertThat(option, sameBeanAs(expected));
  }

  @Test
  public void shouldTransformNoValueThrows() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent();

    expectedException.expect(FHIRException.class);
    optionValueTransformer.transform(optionComponent);
  }

  @Test
  public void shouldTransformStringType() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new StringType("some value"));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("some value", "some value");
    assertThat(option, sameBeanAs(expected));
  }

  @Test
  public void shouldTransformIntegerType() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new IntegerType(55));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("55", "55");
    assertThat(option, sameBeanAs(expected));
  }

  @Test
  public void shouldTransformDateType() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new DateType(2011, 5, 2));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("2011-06-02", "2011-06-02");
    assertThat(option, sameBeanAs(expected));
  }

  @Test
  public void shouldTransformTimeType() {
    QuestionnaireItemOptionComponent optionComponent = new QuestionnaireItemOptionComponent()
        .setValue(new TimeType("19:55:13"));

    TriageOption option = optionValueTransformer.transform(optionComponent);

    TriageOption expected = new TriageOption("19:55:13", "19:55:13");
    assertThat(option, sameBeanAs(expected));
  }
}