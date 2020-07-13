package uk.nhs.ctp.service.isvalid;

import ca.uhn.fhir.context.FhirContext;
import com.google.common.base.Preconditions;
import java.time.Clock;
import java.util.Date;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;

@Component
@RequiredArgsConstructor
public class IsValidOperationService {

  private final FhirContext fhirContext;
  private final Clock clock;

  private static final String IS_VALID = "$isValid";

  private static final String REQUEST_ID = "requestId";
  private static final String ODS_CODE = "ODSCode";
  private static final String EVALUATE_AT = "evaluateAtDateTime";
  private static final String DATE_OF_BIRTH = "dateOfBirth";

  /**
   * Invoke the $isValid operation on a CDSS for a patients GP
   * @return true if the CDSS claims to be valid
   */
  public Boolean invokeIsValid(CdssSupplier supplier, Identifier odsCode, Patient patient) {
    Preconditions.checkNotNull(odsCode, "$isValid requires an ODS code");
    BooleanType isValidResponse =
        (BooleanType)fhirContext.newRestfulGenericClient(supplier.getBaseUrl())
            .operation()
            .onType(ServiceDefinition.class)
            .named(IS_VALID)
            .withParameters(isValidParameters(odsCode, patient.getBirthDate()))
            .execute()
            .getParameterFirstRep()
            .getValue();
    return isValidResponse.booleanValue();
  }

  private Parameters isValidParameters(Identifier odsCode, Date dateOfBirth) {
    return new Parameters()
        .addParameter(new ParametersParameterComponent()
            .setName(REQUEST_ID)
            .setValue(new IdType(UUID.randomUUID().toString())))
        .addParameter(new ParametersParameterComponent()
            .setName(ODS_CODE)
            .setValue(odsCode))
        .addParameter(new ParametersParameterComponent()
            .setName(EVALUATE_AT)
            .setValue(new DateTimeType(Date.from(clock.instant()))))
        .addParameter(new ParametersParameterComponent()
            .setName(DATE_OF_BIRTH)
            .setValue(new DateTimeType(dateOfBirth)));
  }

}
