package uk.nhs.ctp.service.isvalid;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isFhir;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isParameter;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isParametersContaining;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IOperation;
import ca.uhn.fhir.rest.gclient.IOperationUnnamed;
import ca.uhn.fhir.rest.gclient.IOperationUntyped;
import ca.uhn.fhir.rest.gclient.IOperationUntypedWithInputAndPartialOutput;
import java.text.SimpleDateFormat;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.hl7.fhir.dstu3.model.Type;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.IdentifierType;

@RunWith(MockitoJUnitRunner.class)
public class IsValidOperationServiceTest {

  @InjectMocks
  private IsValidOperationService isValidOperationService;

  @Mock
  private FhirContext mockContext;

  @Mock
  private Clock mockClock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final Instant FIXED_INSTANT =
      LocalDateTime.of(2004, 3, 3, 4, 3).toInstant(ZoneOffset.UTC);

  @Test
  public void shouldInvokeIsValid() throws Exception {
    CdssSupplier cdss = new CdssSupplier();
    cdss.setBaseUrl("some.base.url");
    Identifier odsCode = new Identifier()
        .setSystem(IdentifierType.OC.getSystem())
        .setValue("123456");
    Date birthDate = (new SimpleDateFormat("dd/MM/yyyy").parse("01/02/2003"));
    Patient patient = new Patient().setBirthDate(birthDate);
    String requestId = "validRequestId";

    mockOperation("some.base.url", odsCode, birthDate, new BooleanType(true));
    when(mockClock.instant()).thenReturn(FIXED_INSTANT);

    Boolean result = isValidOperationService.invokeIsValid(cdss, odsCode, patient, requestId);

    assertThat(result, is(true));
  }

  @SuppressWarnings("unchecked")
  private void mockOperation(String base, Identifier odsCode, Date birthDate, Type returns) {
    var mockClient = mock(IGenericClient.class);
    when(mockContext.newRestfulGenericClient(base))
        .thenReturn(mockClient);
    var mockOperation = mock(IOperation.class);
    when(mockClient.operation())
        .thenReturn(mockOperation);
    var mockOpUnnamed = mock(IOperationUnnamed.class);
    when(mockOperation.onType(ServiceDefinition.class))
        .thenReturn(mockOpUnnamed);
    var mockOpUntyped = mock(IOperationUntyped.class);
    when(mockOpUnnamed.named("$isValid"))
        .thenReturn(mockOpUntyped);
    var mockOpPartial = mock(IOperationUntypedWithInputAndPartialOutput.class);
    when(mockOpUntyped.withParameters(argThat(isValidParameters(odsCode, birthDate))))
        .thenReturn(mockOpPartial);
    when(mockOpPartial.execute())
        .thenReturn(new Parameters().addParameter(new ParametersParameterComponent()
          .setName("return")
          .setValue(returns)));
  }

  private Matcher<Parameters> isValidParameters(Identifier odsCode, Date birthDate) {
    return isParametersContaining(
        isParameter("requestId", any(IdType.class)),
        isParameter("ODSCode", isFhir(odsCode)),
        isParameter("evaluateAtDateTime", isFhir(new DateTimeType(Date.from(FIXED_INSTANT)))),
        isParameter("dateOfBirth", isFhir(new DateTimeType(birthDate))));
  }

  @Test
  public void shouldFailIsValid_noOdsCode() throws Exception{
    CdssSupplier cdss = new CdssSupplier();
    cdss.setBaseUrl("some.base.url");
    Date birthDate = (new SimpleDateFormat("dd/MM/yyyy").parse("01/02/2003"));
    Patient patient = new Patient().setBirthDate(birthDate);
    String requestId = "validRequestId";

    expectedException.expect(NullPointerException.class);
    isValidOperationService.invokeIsValid(cdss, null, patient, requestId);
  }

}