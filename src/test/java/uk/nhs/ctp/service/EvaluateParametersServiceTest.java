package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.AdditionalMatchers.aryEq;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isFhir;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isParameter;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isParametersContaining;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.referenceTo;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.typeWithValue;

import java.util.Collections;
import java.util.Optional;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.StringType;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.Language;
import uk.nhs.ctp.enums.Setting;
import uk.nhs.ctp.enums.UserType;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;

/**
 * TODO: CDSCT-309 - Add cases for different initiating/receiving user types and referencing types
 */
@RunWith(MockitoJUnitRunner.class)
public class EvaluateParametersServiceTest {

  @Mock
  private CaseRepository caseRepository;

  @Mock
  private ReferenceService referenceService;

  @Mock
  private QuestionnaireService questionnaireService;

  @Mock
  private TokenAuthenticationService authService;

  @Mock
  private GenericResourceLocator resourceLocator;

  @InjectMocks
  private EvaluateParametersService evaluateParametersService;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String SUPPLIER = "supplier";

  @Before
  public void setup() {
    when(authService.requireSupplierId()).thenReturn(SUPPLIER);
  }

  @Test
  public void shouldFail_CaseNotFound() {
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.empty());

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(4L);

    expectedException.expect(EMSException.class);
    evaluateParametersService.getEvaluateParameters(requestDTO, new CdssSupplier(), "requestId");
  }

  @Test
  public void shouldBuildEvaluateParameters() {
    long caseId = 4L;
    Cases caseEntity = testCaseEntity();
    Reference encounterRef = new Reference("Encounter/Ref");
    String questionnaireId = "Questionnaire/someQuestion";
    CdssRequestDTO requestDTO = new CdssRequestDTO();
    TriageQuestion[] questionResponse = {new TriageQuestion()};
    requestDTO.setCaseId(caseId);
    requestDTO.setSettings(testSettings());
    requestDTO.setQuestionnaireId(questionnaireId);
    requestDTO.setQuestionResponse(questionResponse);
    requestDTO.setAmendingPrevious(false);
    String baseUrl = "some.base.url";
    CdssSupplier cdssSupplier = new CdssSupplier();
    cdssSupplier.setBaseUrl(baseUrl);

    when(caseRepository.getOneByIdAndSupplierId(caseId, SUPPLIER))
        .thenReturn(Optional.of(caseEntity));
    when(referenceService.buildRef(ResourceType.Encounter, caseId))
        .thenReturn(encounterRef);
    QuestionnaireResponse expectedQR = questionnaireResponse();
    when(questionnaireService.updateEncounterResponses(
        eq(caseEntity),
        eq(questionnaireId),
        aryEq(questionResponse),
        eq(false),
        argThat(referenceTo(caseEntity.getPatientId())),
        eq(baseUrl)
        )).thenReturn(Collections.singletonList(expectedQR));

    Parameters parameters = evaluateParametersService
        .getEvaluateParameters(requestDTO, cdssSupplier, "123456");

    assertThat(parameters, isParametersContaining(
        isParameter("requestId", typeWithValue("123456")),
        isParameter("encounter", referenceTo(encounterRef)),
        isParameter("patient", referenceTo("Patient/id")),
        isParameter("setting", isFhir(Setting.ONLINE.toCodeableConcept())),
        isParameter("userType", isFhir(UserType.PATIENT.toCodeableConcept())),
        isParameter("recipientType", isFhir(UserType.PATIENT.toCodeableConcept())),
        isParameter("userLanguage", isFhir(Language.BE.toCodeableConcept())),
        isParameter("recipientLanguage", isFhir(Language.AN.toCodeableConcept())),
        isParameter("userTaskContext", isFhir(expectedTaskContext())),
        isParameter("initiatingPerson", referenceTo("Patient/id")),
        isParameter("receivingPerson", referenceTo("Patient/id")),
        isParameter("inputData", referenceTo("Observation/Something")),
        isParameter("inputData", expectedQR)
    ));
  }

  private CodeableConcept expectedTaskContext() {
    return new CodeableConcept(new Coding(SystemURL.SNOMED, "task", "Task"))
        .setText("Task");
  }

  private QuestionnaireResponse questionnaireResponse() {
    return new QuestionnaireResponse()
        .setStatus(QuestionnaireResponseStatus.COMPLETED)
        .addItem(new QuestionnaireResponseItemComponent()
          .addAnswer(new QuestionnaireResponseItemAnswerComponent()
              .setValue(new StringType("The Answer"))));
  }

  private SettingsDTO testSettings() {
    SettingsDTO settingsDTO = new SettingsDTO();
    settingsDTO.setSetting(Setting.ONLINE.toDTO());
    settingsDTO.setUserType(UserType.PATIENT.toDTO());
    settingsDTO.setUserTaskContext(new CodeDTO("task", "Task", null));
    settingsDTO.setUserLanguage(Language.BE.toDTO());
    settingsDTO.setRecipientLanguage(Language.AN.toDTO());
    return settingsDTO;
  }

  private Cases testCaseEntity() {
    Cases caseEntity = new Cases();
    caseEntity.setPatientId("Patient/id");

    CaseParameter parameter = new CaseParameter();
    parameter.setReference("Observation/Something");
    caseEntity.addParameter(parameter);
    return caseEntity;
  }

}
