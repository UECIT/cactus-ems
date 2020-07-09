package uk.nhs.ctp.transform;

import static com.google.common.base.Preconditions.checkNotNull;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import java.sql.Date;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.CoordinateResource;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DateType;
import org.hl7.fhir.dstu3.model.DecimalType;
import org.hl7.fhir.dstu3.model.Enumerations.FHIRAllTypes;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.StringType;
import org.hl7.fhir.dstu3.model.TimeType;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.attachment.AttachmentService;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;

@Component
@RequiredArgsConstructor
public class QuestionnaireAnswerValueTransformer implements Transformer<TriageQuestion, Type> {

  private final AttachmentService attachmentService;

  private static final String IMAGE_MAP_EXTENSION = "imagemap";

  @Override
  public Type transform(TriageQuestion triageQuestion) {
    switch (QuestionnaireItemType.valueOf(triageQuestion.getQuestionType().toUpperCase())) {
      case STRING:
      case TEXT:
        return new StringType(triageQuestion.getResponseString());
      case INTEGER:
        return new IntegerType(triageQuestion.getResponseInteger());
      case BOOLEAN:
        return new BooleanType(triageQuestion.getResponseBoolean());
      case DECIMAL:
        return new DecimalType(triageQuestion.getResponseDecimal());
      case DATE:
        var date = Date.from(Instant.parse(triageQuestion.getResponseDate()));
        return new DateType(date, TemporalPrecisionEnum.DAY);
      case DATETIME:
        return new DateTimeType(triageQuestion.getResponseDate()); //Defaults to TemporalPrecisionEnum.SECOND
      case ATTACHMENT:
        var attachmentData = triageQuestion.getResponseAttachment().getBytes();
        String attachmentType = triageQuestion.getResponseAttachmentType();
        return attachmentService.storeAttachment(
            MediaType.valueOf(attachmentType), attachmentData);
      case REFERENCE:
        if (isImageMapAnswer(triageQuestion)) {
          CoordinateResource coordinateResource = new CoordinateResource();
          coordinateResource
              .setXCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getX()));
          coordinateResource
              .setYCoordinate(new IntegerType(triageQuestion.getResponseCoordinates().getY()));
          return new Reference(coordinateResource);
        }
      default: //Some coding types can have primitive type values (See CDSCT-64)
        checkNotNull(triageQuestion.getResponse(), "No response for question");
        return answerFromCoding(triageQuestion.getResponse());
    }
  }

  private Type answerFromCoding(TriageOption response) {
    return typeFromSystem(response.getSystem())
        .map(type -> {
          switch (type) {
            case STRING: return new StringType(response.getCode());
            case INTEGER: return new IntegerType(response.getCode());
            case DATE: return new DateType(response.getCode());
            case TIME: return new TimeType(response.getCode());
            default:
              return new Coding(response.getSystem(), response.getCode(), response.getDisplay());
          }
        }).orElse(new Coding(response.getSystem(), response.getCode(), response.getDisplay()));
  }

  private Optional<FHIRAllTypes> typeFromSystem(String system) {
    try {
      return Optional.of(FHIRAllTypes.fromCode(system));
    } catch (FHIRException | NullPointerException ignored) {
      return Optional.empty();
    }
  }

  private boolean isImageMapAnswer(TriageQuestion triageQuestion) {
    return triageQuestion.getExtension() != null
        && IMAGE_MAP_EXTENSION.equals(triageQuestion.getExtension().getCode());
  }
}
