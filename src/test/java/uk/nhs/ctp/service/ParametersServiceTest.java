package uk.nhs.ctp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Immunization.ImmunizationStatus;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.entities.AuditRecord;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.Party;
import uk.nhs.ctp.entities.Skillset;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.PersonDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ParametersServiceTest {

  @Autowired
  @InjectMocks
  private ParametersService parametersService;

  @Mock
  private CaseRepository mockCaseRepository;
  @Mock
  private AuditService mockAuditService;
  @Mock
  private ReferenceStorageService mockStorageService;

  private Cases caseWithNoData, caseWithObservation, caseWithImmunization, caseWithMedication, caseWithData;
  private CaseObservation caseObservation;
  private CaseImmunization caseImmunization;
  private CaseMedication caseMedication;
  private AuditRecord caseAudit;
  private Calendar calendar;
  private TriageQuestion[] questionResponses;
  private SettingsDTO settings;
  private ReferencingContext referencingContext;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);

    calendar = Calendar.getInstance();
    calendar.set(2018, Calendar.JUNE, 3);

    caseObservation = new CaseObservation();
    caseObservation.setId(1L);
    caseObservation.setCode("123456");
    caseObservation.setDisplay("Test Observation");
    caseObservation.setTimestamp(calendar.getTime());
    caseObservation.setValueCode("true");

    caseImmunization = new CaseImmunization();
    caseImmunization.setId(1L);
    caseImmunization.setCode("123456");
    caseImmunization.setDisplay("Test Immunization");
    caseImmunization.setTimestamp(calendar.getTime());
    caseImmunization.setNotGiven(true);

    caseMedication = new CaseMedication();
    caseMedication.setId(1L);
    caseMedication.setCode("123456");
    caseMedication.setDisplay("Test Medication");
    caseMedication.setTimestamp(calendar.getTime());
    caseMedication.setNotGiven(false);

    caseWithNoData = newCase();

    caseWithImmunization = newCase();
    caseWithImmunization.addImmunization(caseImmunization);

    caseWithMedication = newCase();
    caseWithMedication.addMedication(caseMedication);

    caseWithObservation = newCase();
    caseWithObservation.addObservation(caseObservation);

    caseWithData = newCase();
    caseWithData.addImmunization(caseImmunization);
    caseWithData.addMedication(caseMedication);
    caseWithData.addObservation(caseObservation);

    caseAudit = new AuditRecord();
    caseAudit.setCaseId(1L);
    caseAudit.setTriageComplete(false);
    caseAudit.setCreatedDate(new Date());

    var questionResponsesTemp = new ArrayList<TriageQuestion>();

    TriageQuestion questionResponse = new TriageQuestion();
    questionResponse.setQuestion("Test question");
    questionResponse.setQuestionId("1");
    questionResponse.setQuestionnaireId("1");
    questionResponse.setQuestionType("CHOICE");

    TriageOption option1 = new TriageOption("1", "Option 1");

    questionResponse.setResponse(option1);

    questionResponsesTemp.add(questionResponse);

    questionResponses = questionResponsesTemp.toArray(new TriageQuestion[0]);

    PersonDTO personDto = new PersonDTO();
    personDto.setBirthDate("2011-09-07");
    personDto.setGender("male");
    personDto.setName("Joe Bloggs");
    personDto.setTelecom("0123 123 1234");
    CodeDTO codeDto = new CodeDTO();
    codeDto.setCode("158974003");
    codeDto.setDisplay("Call Handler");
    settings = new SettingsDTO();

    settings.setInitiatingPerson(personDto);
    settings.setUserType(codeDto);
    settings.setUserLanguage(codeDto);
    settings.setUserTaskContext(codeDto);
    settings.setReceivingPerson(personDto);
    settings.setRecipientType(codeDto);
    settings.setRecipientLanguage(codeDto);
    settings.setSetting(codeDto);

    referencingContext = new ReferencingContext(ReferencingType.ContainedReferences);
  }


  @Test
  public void testParametersCreatedCorrectlyWithNoCaseDataStored() {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        null,
        settings,
        false,
        referencingContext, "");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
    assertEquals(14, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

  }

  @Test
  public void testParametersCreatedCorrectlyWithNoCaseDataStoredAndQuestionAnswered()
      throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);
    when(mockStorageService.storeExternal(any(Resource.class))).thenReturn(new Reference());

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        questionResponses,
        settings,
        false,
        referencingContext, "1");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    assertEquals(15, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertEquals(3, inputDataParameters.size());

    testQuestionnaireResponseIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseImmunizationStored() {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithImmunization);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        null,
        settings,
        false,
        referencingContext, "");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    assertEquals(15, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertEquals(3, inputDataParameters.size());

    testImmunizationIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseMedicationStored() throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithMedication);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        null,
        settings,
        false,
        referencingContext, "");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    assertEquals(15, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertEquals(3, inputDataParameters.size());

    testMedicationIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseObservationStored() throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithObservation);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        null,
        settings,
        false,
        referencingContext, "");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    assertEquals(15, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertEquals(3, inputDataParameters.size());

    testObservationIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseDataStoredAndQuestionAnswered()
      throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithData);
    when(mockAuditService.getAuditRecordByCase(1L)).thenReturn(caseAudit);
    when(mockStorageService.storeExternal(any(Resource.class))).thenReturn(new Reference());

    Parameters parameters = parametersService.getEvaluateParameters(
        1L,
        questionResponses,
        settings,
        false,
        referencingContext, "1");

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    assertEquals(18, parameterComponents.size());

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
    testContextParamsAreCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertEquals(6, inputDataParameters.size());

    testQuestionnaireResponseIsCorrect(inputDataParameters);
    testObservationIsCorrect(inputDataParameters);
    testImmunizationIsCorrect(inputDataParameters);
    testMedicationIsCorrect(inputDataParameters);

  }


  private void testMedicationIsCorrect(List<ParametersParameterComponent> inputDataParameters)
      throws FHIRException {
    // Get medication
    List<MedicationAdministration> medications = inputDataParameters.stream()
        .filter(param -> param.getResource() instanceof MedicationAdministration)
        .map(param -> (MedicationAdministration) param.getResource())
        .collect(Collectors.toList());

    assertEquals(1, medications.size());

    MedicationAdministration medication = medications.get(0);

    assertNotNull(medication);
    assertEquals(MedicationAdministrationStatus.COMPLETED, medication.getStatus());
    assertNull(medication.getId());
    assertNotNull(medication.getMedicationCodeableConcept());
    assertEquals(1, medication.getMedicationCodeableConcept().getCoding().size());
    assertEquals("123456", medication.getMedicationCodeableConcept().getCodingFirstRep().getCode());
    assertEquals("Test Medication",
        medication.getMedicationCodeableConcept().getCodingFirstRep().getDisplay());
    assertFalse(medication.getNotGiven());
  }


  private void testImmunizationIsCorrect(List<ParametersParameterComponent> inputDataParameters) {
    //Get immunization
    List<Immunization> immunizations = inputDataParameters.stream()
        .filter(param -> param.getResource() instanceof Immunization)
        .map(param -> (Immunization) param.getResource())
        .collect(Collectors.toList());

    assertEquals(1, immunizations.size());

    Immunization immunization = immunizations.get(0);

    assertNotNull(immunization);
    assertEquals(ImmunizationStatus.COMPLETED, immunization.getStatus());
    assertNull(immunization.getId());
    assertNotNull(immunization.getVaccineCode());
    assertEquals(1, immunization.getVaccineCode().getCoding().size());
    assertEquals("123456", immunization.getVaccineCode().getCodingFirstRep().getCode());
    assertEquals("Test Immunization",
        immunization.getVaccineCode().getCodingFirstRep().getDisplay());
    assertTrue(immunization.getNotGiven());

  }


  private void testObservationIsCorrect(List<ParametersParameterComponent> inputDataParameters)
      throws FHIRException {
    //Get observation
    List<Observation> observations = inputDataParameters.stream()
        .filter(param -> param.getResource() instanceof Observation)
        .map(param -> (Observation) param.getResource())
        .collect(Collectors.toList());

    assertEquals(3, observations.size());

    Observation observation = observations.stream()
        .filter(o -> o.getCode().getCodingFirstRep().getCode().equals("123456"))
        .findFirst()
        .orElse(null);

    assertNotNull(observation);
    assertEquals(ObservationStatus.FINAL, observation.getStatus());
    assertNull(observation.getId());
    assertNotNull(observation.getCode());
    assertEquals(1, observation.getCode().getCoding().size());
    assertEquals("Test Observation", observation.getCode().getCodingFirstRep().getDisplay());
    assertEquals("true", observation.getValueCodeableConcept().getCodingFirstRep().getCode());
  }


  private void testQuestionnaireResponseIsCorrect(
      List<ParametersParameterComponent> inputDataParameters)
      throws FHIRException {
    //Get questionnaire response
    List<QuestionnaireResponse> questionnaireResponses = inputDataParameters.stream()
        .filter(param -> param.getResource() instanceof QuestionnaireResponse)
        .map(param -> (QuestionnaireResponse) param.getResource())
        .collect(Collectors.toList());

    assertEquals(1, questionnaireResponses.size());

    QuestionnaireResponse questionnaireResponse = questionnaireResponses.get(0);

    assertNotNull(questionnaireResponse);
    assertNotNull(questionnaireResponse.getQuestionnaire());
    assertEquals("Questionnaire/1", questionnaireResponse.getQuestionnaire().getReference());
    assertEquals(QuestionnaireResponseStatus.COMPLETED, questionnaireResponse.getStatus());

    assertEquals(1, questionnaireResponse.getItem().size());

    QuestionnaireResponseItemComponent item = questionnaireResponse.getItemFirstRep();

    assertNotNull(item);
    assertEquals("1", item.getLinkId());
    assertEquals("Test question", item.getText());

    assertEquals(1, item.getAnswer().size());

    QuestionnaireResponseItemAnswerComponent answer = item.getAnswerFirstRep();

    assertNotNull(answer);
    assertNotNull(answer.getValueCoding());
    assertEquals("1", answer.getValueCoding().getCode());
    assertEquals("Option 1", answer.getValueCoding().getDisplay());
  }


  private void testRequestIdParamIsCorrect(List<ParametersParameterComponent> parameterComponents) {
    List<ParametersParameterComponent> requestIdParams = parameterComponents.stream()
        .filter(param -> param.getName().equals("requestId"))
        .collect(Collectors.toList());

    assertEquals(1, requestIdParams.size());
    assertNotNull(requestIdParams.get(0).getValue());
    assertEquals("1", requestIdParams.get(0).getValue().primitiveValue());
  }


  private void testContextParamsAreCorrect(List<ParametersParameterComponent> parameterComponents) {
    //Get parameter "inputParameters"
    List<ParametersParameterComponent> inputParams = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputParameters"))
        .collect(Collectors.toList());

    //Check not null and has resource
    assertEquals(1, inputParams.size());
    assertNotNull(inputParams.get(0).getResource());

    //Get inputParameters
    Parameters inputParamsResource = (Parameters) inputParams.get(0).getResource();

    List<ParametersParameterComponent> inputParamComponents = inputParamsResource.getParameter();

    //Check inputParameters has 1 parameter - "context"
    assertEquals(1, inputParamComponents.size());
    assertEquals("context", inputParamComponents.get(0).getName());

    List<ParametersParameterComponent> contextParams = inputParamComponents.get(0).getPart();

    //Check context parameters has two parameters
    assertEquals(2, contextParams.size());

    List<ParametersParameterComponent> skillsetParams = contextParams.stream()
        .filter(param -> param.getName().equals("skillset"))
        .collect(Collectors.toList());

    assertEquals(1, skillsetParams.size());
    assertNotNull(skillsetParams.get(0).getValue());

    //Check skillset parameter is correct
    assertEquals("CL", skillsetParams.get(0).getValue().primitiveValue());

    List<ParametersParameterComponent> partyParams = contextParams.stream()
        .filter(param -> param.getName().equals("party"))
        .collect(Collectors.toList());

    assertEquals(1, partyParams.size());
    assertNotNull(partyParams.get(0).getValue());

    //Check party parameter is correct
    assertEquals("1", partyParams.get(0).getValue().primitiveValue());
  }

  private void testPatientParamIsCorrect(List<ParametersParameterComponent> parameterComponents) {
    List<ParametersParameterComponent> patientParams = parameterComponents.stream()
        .filter(param -> param.getName().equals("patient"))
        .collect(Collectors.toList());

    assertEquals(1, patientParams.size());
    assertNotNull(patientParams.get(0).getResource());

    Patient patient = (Patient) patientParams.get(0).getResource();

    assertEquals(AdministrativeGender.MALE, patient.getGender());
    assertEquals(calendar.getTime(), patient.getBirthDate());
  }

  private Cases newCase() {
    Cases testCase = new Cases();

    Party party = new Party();
    party.setCode("1");
    party.setDescription("1st Party");

    Skillset skillset = new Skillset();
    skillset.setCode("CL");
    skillset.setDescription("Clinician");

    testCase.setId(1L);
    testCase.setGender("male");
    testCase.setFirstName("John");
    testCase.setLastName("Smith");
    testCase.setDateOfBirth(calendar.getTime());
    testCase.setAddress("Test address");
    testCase.setNhsNumber("9476719915");
    testCase.setParty(party);
    testCase.setSkillset(skillset);
    testCase.setTimestamp(calendar.getTime());

    return testCase;
  }
}
