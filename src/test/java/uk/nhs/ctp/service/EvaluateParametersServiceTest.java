package uk.nhs.ctp.service;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Immunization.ImmunizationStatus;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.MedicationAdministration.MedicationAdministrationStatus;
import org.hl7.fhir.dstu3.model.NHSNumberIdentifier;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseItemComponent;
import org.hl7.fhir.dstu3.model.QuestionnaireResponse.QuestionnaireResponseStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Type;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.ReferencingType;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssRequestDTO;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.PersonDTO;
import uk.nhs.ctp.service.dto.PractitionerDTO;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.service.dto.TriageOption;
import uk.nhs.ctp.service.dto.TriageQuestion;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.StorageService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class EvaluateParametersServiceTest {

  private static final String BASE_URL = "http://base.url:8754";

  @Autowired
  private EvaluateParametersService evaluateParametersService;

  @MockBean
  private CaseRepository mockCaseRepository;
  @MockBean
  private StorageService mockStorageService;
  @MockBean
  private GenericResourceLocator resourceLocator;

  private Cases caseWithNoData, caseWithObservation, caseWithImmunization, caseWithMedication, caseWithData;
  private Calendar calendar;
  private TriageQuestion[] questionResponses;
  private SettingsDTO settings;

  @Before
  public void setup() {
    calendar = Calendar.getInstance();
    calendar.set(2018, Calendar.JUNE, 3);

    var caseObservation = new CaseObservation();
    caseObservation.setId(1L);
    caseObservation.setCode("123456");
    caseObservation.setDisplay("Test Observation");
    caseObservation.setValueCode("true");

    var caseImmunization = new CaseImmunization();
    caseImmunization.setId(1L);
    caseImmunization.setCode("123456");
    caseImmunization.setDisplay("Test Immunization");
    caseImmunization.setNotGiven(true);

    var caseMedication = new CaseMedication();
    caseMedication.setId(1L);
    caseMedication.setCode("123456");
    caseMedication.setDisplay("Test Medication");
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

    var questionnaire = new Questionnaire();
    questionnaire.setId("1");
    questionnaire.addItem().setLinkId("1").setText("Is this a test?");

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
    codeDto.setCode("phone");
    codeDto.setDisplay("Call Handler");

    CodeDTO languageDto = new CodeDTO();
    languageDto.setCode("en");

    settings = new SettingsDTO();

    settings.setUserType(new CodeDTO("Patient", "Patient", "UserTypeSys"));
    settings.setUserLanguage(languageDto);
    settings.setUserTaskContext(codeDto);
    settings.setRecipientLanguage(languageDto);
    settings.setSetting(codeDto);
    settings.setPractitioner(new PractitionerDTO("1L", "Bob Wilkins"));

    when(mockStorageService.findResource("Patient/1", CareConnectPatient.class))
        .thenReturn(newPatient());
    when(mockStorageService.storeExternal(any())).thenAnswer(new Answer<>() {
      private long nextId = 1;

      @Override
      public Object answer(InvocationOnMock invocation) {
        Resource resource = invocation.getArgumentAt(0, Resource.class);
        if (resource.hasId()) {
          return resource.getId();
        } else {
          return resource.getResourceType().name() + "/" + nextId++;
        }
      }
    });
    when(resourceLocator.<Questionnaire>findResource(any(Reference.class)))
        .thenReturn(questionnaire);
  }

  @Test
  public void testParametersCreatedCorrectlyWithNoCaseDataStored() {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString()
    );

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);
  }

  @Test
  public void testParametersCreatedCorrectlyWithNoCaseDataStoredAndQuestionAnswered()
      throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionResponse(questionResponses);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString()
    );

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    testQuestionnaireResponseIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseImmunizationStored() {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithImmunization);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString()
    );

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    testImmunizationIsCorrect(inputDataParameters);

  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseMedicationStored() throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithMedication);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString());

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    testMedicationIsCorrect(inputDataParameters);
  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseObservationStored_Resource() throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithObservation);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);
    supplier.setInputDataRefType(ReferencingType.BY_RESOURCE);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString());

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    testObservationIsCorrect(inputDataParameters);
  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseObservationStored_Reference() throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithObservation);

    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);
    supplier.setInputDataRefType(ReferencingType.BY_REFERENCE);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString());

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

    assertThat(inputDataParameters, hasSize(1));
    Type value = Iterables.getOnlyElement(inputDataParameters).getValue();
    assertThat(value, instanceOf(Reference.class));
    assertThat(((Reference)value).getReference(), is("http://localhost:8083/fhir/Observation/1"));
  }

  @Test
  public void testParametersCreatedCorrectlyWithCaseDataStoredAndQuestionAnswered()
      throws FHIRException {
    when(mockCaseRepository.findOne(1L)).thenReturn(caseWithData);
    CdssRequestDTO requestDTO = new CdssRequestDTO();
    requestDTO.setCaseId(1L);
    requestDTO.setQuestionResponse(questionResponses);
    requestDTO.setQuestionnaireId("1");
    requestDTO.setSettings(settings);
    CdssSupplier supplier = new CdssSupplier();
    supplier.setBaseUrl(BASE_URL);
    supplier.setInputDataRefType(ReferencingType.BY_RESOURCE);

    Parameters parameters = evaluateParametersService.getEvaluateParameters(
        requestDTO,
        supplier,
        UUID.randomUUID().toString());

    assertNotNull(parameters);

    List<ParametersParameterComponent> parameterComponents = parameters.getParameter();

    testRequestIdParamIsCorrect(parameterComponents);
    testPatientParamIsCorrect(parameterComponents);

    //Get inputData parameters
    List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
        .filter(param -> param.getName().equals("inputData"))
        .collect(Collectors.toList());

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

    assertEquals(1, observations.size());

    Observation observation = observations.stream()
        .filter(o -> o.getCode().getCodingFirstRep().getCode().equals("123456"))
        .findFirst()
        .orElse(null);

    assertNotNull(observation);
    assertEquals(ObservationStatus.FINAL, observation.getStatus());
    assertNotNull(observation.getId());
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
    assertEquals(BASE_URL + "/Questionnaire/1",
        questionnaireResponse.getQuestionnaire().getReference());
    assertEquals(QuestionnaireResponseStatus.COMPLETED, questionnaireResponse.getStatus());

    assertEquals(1, questionnaireResponse.getItem().size());

    QuestionnaireResponseItemComponent item = questionnaireResponse.getItemFirstRep();

    assertNotNull(item);
    assertEquals("1", item.getLinkId());

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
    assertNotNull(UUID.fromString(requestIdParams.get(0).getValue().primitiveValue()));
  }

  private void testPatientParamIsCorrect(List<ParametersParameterComponent> parameterComponents) {
    List<ParametersParameterComponent> patientParams = parameterComponents.stream()
        .filter(param -> param.getName().equals("patient"))
        .collect(Collectors.toList());

    assertEquals(1, patientParams.size());
    assertNotNull(patientParams.get(0).getValue());

    var patient = (Reference) patientParams.get(0).getValue();

    assertEquals(patient.getReference(), "Patient/1");
  }

  private Cases newCase() {
    Cases testCase = new Cases();

    testCase.setId(1L);
    testCase.setPatientId("Patient/1");
    testCase.setGender("male");
    testCase.setFirstName("John");
    testCase.setLastName("Smith");
    testCase.setDateOfBirth(calendar.getTime());
    testCase.setAddress("Test address");
    testCase.setNhsNumber("9476719915");
    testCase.setCreatedDate(calendar.getTime());
    testCase.setTriageComplete(false);
    testCase.setCreatedDate(new Date());

    return testCase;
  }

  private CareConnectPatient newPatient() {
    CareConnectPatient patient = new CareConnectPatient();
    patient.setId("Patient/1");
    NHSNumberIdentifier nhsNumber = new NHSNumberIdentifier();
    nhsNumber.setId("9476719915");
    patient.addIdentifier(nhsNumber);

    patient.setGender(AdministrativeGender.MALE);
    patient.addName()
        .addGiven("John")
        .setFamily("Smith");
    patient.setBirthDate(calendar.getTime());
    patient.addAddress()
        .addLine("Test address");

    return patient;
  }
}
