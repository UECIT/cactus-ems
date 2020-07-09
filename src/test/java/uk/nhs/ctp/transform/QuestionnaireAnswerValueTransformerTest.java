package uk.nhs.ctp.transform;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.IMAGE_JPEG;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.coordinate2d;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isFhir;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.typeWithValue;

import org.hl7.fhir.dstu3.model.Attachment;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.attachment.AttachmentService;
import uk.nhs.ctp.service.dto.Coordinates;
import uk.nhs.ctp.service.dto.ExtensionDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireAnswerValueTransformerTest {

  @InjectMocks
  private QuestionnaireAnswerValueTransformer answerValueTransformer;

  @Mock
  private AttachmentService attachmentService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Test
  public void transformsTextType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.TEXT.toString());
    triageQuestion.setResponseString("The response");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(StringType.class));
    assertThat((StringType)answer, typeWithValue("The response"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsStringType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.STRING.toString());
    triageQuestion.setResponseString("The response");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(StringType.class));
    assertThat((StringType)answer, typeWithValue("The response"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsIntegerType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.INTEGER.toString());
    triageQuestion.setResponseInteger("56");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(IntegerType.class));
    assertThat((IntegerType)answer, typeWithValue("56"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsBooleanType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.BOOLEAN.toString());
    triageQuestion.setResponseBoolean("true");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(BooleanType.class));
    assertThat((BooleanType)answer, typeWithValue("true"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsDecimalType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.DECIMAL.toString());
    triageQuestion.setResponseDecimal("4.456");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(DecimalType.class));
    assertThat((DecimalType)answer, typeWithValue("4.456"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsDateTypeFromTime() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.DATE.toString());
    triageQuestion.setResponseDate("2020-07-06T14:00:00.000Z");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(DateType.class));
    assertThat((DateType)answer, typeWithValue("2020-07-06"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformsDateTimeType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.DATETIME.toString());
    triageQuestion.setResponseDate("2020-07-06T23:00:00.000Z");

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(DateTimeType.class));
    assertThat((DateTimeType)answer, typeWithValue("2020-07-06T23:00:00.000Z"));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformAttachmentType() {
    TriageQuestion triageQuestion = new TriageQuestion();
    String attachment = "Some encoded attachment";
    triageQuestion.setQuestionType(QuestionnaireItemType.ATTACHMENT.toString());
    triageQuestion.setResponseAttachment(attachment);
    triageQuestion.setResponseAttachmentType("image/jpeg");

    Attachment expected = new Attachment()
        .setData(attachment.getBytes());

    when(attachmentService.storeAttachment(IMAGE_JPEG, attachment.getBytes()))
        .thenReturn(expected);

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, is(expected));
  }

  @Test
  public void transformReferenceImageMap() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.REFERENCE.toString());
    Coordinates coords = new Coordinates();
    coords.setX(4);
    coords.setY(7);
    triageQuestion.setResponseCoordinates(coords);
    ExtensionDTO imageExt = new ExtensionDTO();
    imageExt.setCode("imagemap");
    triageQuestion.setExtension(imageExt);

    Type answer = answerValueTransformer.transform(triageQuestion);

    assertThat(answer, instanceOf(Reference.class));
    assertThat(((Reference)answer).getResource(), coordinate2d(4, 7));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformReferenceNotImageMapDefaultsToCoding() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.REFERENCE.toString());
    TriageOption response = new TriageOption("code", "Answer");
    triageQuestion.setResponse(response);

    Type answer = answerValueTransformer.transform(triageQuestion);

    Coding expected = new Coding(null, "code", "Answer");

    assertThat(answer, instanceOf(Coding.class));
    assertThat(answer, isFhir(expected));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void transformDefaultCoding_actuallyCoding() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.URL.toString());
    TriageOption response = new TriageOption("a-system", "url", "Answer");
    triageQuestion.setResponse(response);

    Type answer = answerValueTransformer.transform(triageQuestion);

    Coding expected = new Coding("a-system", "url", "Answer");

    assertThat(answer, instanceOf(Coding.class));
    assertThat(answer, isFhir(expected));
    verifyZeroInteractions(attachmentService);
  }

  @Test
  public void noResponseThrows() {
    TriageQuestion triageQuestion = new TriageQuestion();
    triageQuestion.setQuestionType(QuestionnaireItemType.URL.toString());

    expectedException.expect(NullPointerException.class);
    answerValueTransformer.transform(triageQuestion);

    verifyZeroInteractions(attachmentService);
  }

}