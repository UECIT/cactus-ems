package uk.nhs.ctp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
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

import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Party;
import uk.nhs.ctp.entities.Skillset;
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
	
	Cases caseWithNoData, caseWithObservation, caseWithImmunization, caseWithMedication, caseWithData;
	CaseObservation caseObservation;
	CaseImmunization caseImmunization;
	CaseMedication caseMedication;
	Calendar calendar;
	TriageQuestion[] questionResponses;
	SettingsDTO settings;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		
		calendar = Calendar.getInstance();
		calendar.set(2018, 05, 03);
		
		caseObservation = new CaseObservation();
		caseObservation.setId(1L);
		caseObservation.setCode("123456");
		caseObservation.setDisplay("Test Observation");
		caseObservation.setTimestamp(calendar.getTime());
		caseObservation.setValue(true); 
		
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
		
		
		ArrayList<TriageQuestion> questionResponsesTemp = new ArrayList<TriageQuestion>();
		
		TriageQuestion questionResponse = new TriageQuestion();
		questionResponse.setQuestion("Test question");
		questionResponse.setQuestionId("1");
		questionResponse.setQuestionnaireId("1");
		questionResponse.setQuestionType("CHOICE");
		
		TriageOption option1 = new TriageOption("1", "Option 1");
		
		questionResponse.setResponse(option1);
		
		questionResponsesTemp.add(questionResponse);
		
		questionResponses = questionResponsesTemp.toArray(new TriageQuestion[questionResponsesTemp.size()]);
		
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
		
	}


	@Test
	public void testParametersCreatedCorrectlyWithNoCaseDataStored() {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);
		
		Parameters parameters = parametersService.getEvaluateParameters(1L, null, settings, false);
		
		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		assertTrue(parameterComponents.size() == 13);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);

	}
	
	@Test
	public void testParametersCreatedCorrectlyWithNoCaseDataStoredAndQuestionAnswered() throws FHIRException {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithNoData);
		
		Parameters parameters = parametersService.getEvaluateParameters(1L, questionResponses, settings, false);

		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		
		assertTrue(parameterComponents.size() == 14);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);
		
		//Get inputData parameters
		List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputData"))
				.collect(Collectors.toList());
		
		//Check there are 4 input data parameters
		assertTrue(inputDataParameters.size() == 3);
		
		testQuestionnaireResponseIsCorrect(inputDataParameters);

	}
	
	@Test
	public void testParametersCreatedCorrectlyWithCaseImmunizationStored() {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithImmunization);

		Parameters parameters = parametersService.getEvaluateParameters(1L, null, settings, false);

		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		
		assertTrue(parameterComponents.size() == 14);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);
		
		//Get inputData parameters
		List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputData"))
				.collect(Collectors.toList());
		
		//Check there is 1 input data parameter
		assertTrue(inputDataParameters.size() == 3);
		
		testImmunizationIsCorrect(inputDataParameters);

	}
	
	@Test
	public void testParametersCreatedCorrectlyWithCaseMedicationStored() throws FHIRException {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithMedication);

		Parameters parameters = parametersService.getEvaluateParameters(1L, null, settings, false);

		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		
		assertTrue(parameterComponents.size() == 14);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);
		
		//Get inputData parameters
		List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputData"))
				.collect(Collectors.toList());
		
		//Check there is 1 input data parameter
		assertTrue(inputDataParameters.size() == 3);
		
		testMedicationIsCorrect(inputDataParameters);

	}
	
	@Test
	public void testParametersCreatedCorrectlyWithCaseObservationStored() throws FHIRException {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithObservation);

		Parameters parameters = parametersService.getEvaluateParameters(1L, null, settings, false);

		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		
		assertTrue(parameterComponents.size() == 14);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);
		
		//Get inputData parameters
		List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputData"))
				.collect(Collectors.toList());
		
		//Check there is 1 input data parameter
		assertTrue(inputDataParameters.size() == 3);
		
		testObservationIsCorrect(inputDataParameters);

	}

	@Test
	public void testParametersCreatedCorrectlyWithCaseDataStoredAndQuestionAnswered() throws FHIRException {
		when(mockCaseRepository.findOne(1L)).thenReturn(caseWithData);

		Parameters parameters = parametersService.getEvaluateParameters(1L, questionResponses, settings, false);

		assertNotNull(parameters);
		
		List<ParametersParameterComponent> parameterComponents = parameters.getParameter();
		
		assertTrue(parameterComponents.size() == 17);
		
		testRequestIdParamIsCorrect(parameterComponents);
		testPatientParamIsCorrect(parameterComponents);
		testContextParamsAreCorrect(parameterComponents);
		
		//Get inputData parameters
		List<ParametersParameterComponent> inputDataParameters = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputData"))
				.collect(Collectors.toList());
		
		//Check there are 4 input data parameters
		assertTrue(inputDataParameters.size() == 6);
		
		testQuestionnaireResponseIsCorrect(inputDataParameters);
		testObservationIsCorrect(inputDataParameters);
		testImmunizationIsCorrect(inputDataParameters);
		testMedicationIsCorrect(inputDataParameters);

		
	}


	private void testMedicationIsCorrect(List<ParametersParameterComponent> inputDataParameters) throws FHIRException {
		//Get medication
		List<MedicationAdministration> medications = inputDataParameters.stream()
			.filter(param -> param.getResource() instanceof MedicationAdministration)
			.map(param -> (MedicationAdministration) param.getResource())
			.collect(Collectors.toList());
		
		assertTrue(medications.size() == 1);
		
		MedicationAdministration medication = medications.get(0);
		
		assertNotNull(medication);
		assertEquals(MedicationAdministrationStatus.COMPLETED, medication.getStatus());
		assertNull(medication.getId());
		assertNotNull(medication.getMedicationCodeableConcept());
		assertTrue(medication.getMedicationCodeableConcept().getCoding().size() == 1);
		assertEquals("123456", medication.getMedicationCodeableConcept().getCodingFirstRep().getCode());
		assertEquals("Test Medication", medication.getMedicationCodeableConcept().getCodingFirstRep().getDisplay());
		assertFalse(medication.getNotGiven());
	}


	private void testImmunizationIsCorrect(List<ParametersParameterComponent> inputDataParameters) {
		//Get immunization
		List<Immunization> immunizations = inputDataParameters.stream()
			.filter(param -> param.getResource() instanceof Immunization)
			.map(param -> (Immunization) param.getResource())
			.collect(Collectors.toList());
		
		assertTrue(immunizations.size() == 1);
		
		Immunization immunization = immunizations.get(0);
		
		assertNotNull(immunization);
		assertEquals(ImmunizationStatus.COMPLETED, immunization.getStatus());
		assertNull(immunization.getId());
		assertNotNull(immunization.getVaccineCode());
		assertTrue(immunization.getVaccineCode().getCoding().size() == 1);
		assertEquals("123456", immunization.getVaccineCode().getCodingFirstRep().getCode());
		assertEquals("Test Immunization", immunization.getVaccineCode().getCodingFirstRep().getDisplay());
		assertTrue(immunization.getNotGiven());

	}


	private void testObservationIsCorrect(List<ParametersParameterComponent> inputDataParameters) throws FHIRException {
		//Get observation
		List<Observation> observations = inputDataParameters.stream()
			.filter(param -> param.getResource() instanceof Observation)
			.map(param -> (Observation) param.getResource())
			.collect(Collectors.toList());
		
		assertTrue(observations.size() == 3);
		
		Observation observation = observations.get(2);
		
		assertNotNull(observation);
		assertEquals(ObservationStatus.FINAL, observation.getStatus());
		assertNull(observation.getId());
		assertNotNull(observation.getCode());
		assertTrue(observation.getCode().getCoding().size() == 1);
		assertEquals("123456", observation.getCode().getCodingFirstRep().getCode());
		assertEquals("Test Observation", observation.getCode().getCodingFirstRep().getDisplay());
		assertTrue(observation.getValueBooleanType().booleanValue());
	}


	private void testQuestionnaireResponseIsCorrect(List<ParametersParameterComponent> inputDataParameters)
			throws FHIRException {
		//Get questionnaire response
		List<QuestionnaireResponse> questionnaireResponses = inputDataParameters.stream()
			.filter(param -> param.getResource() instanceof QuestionnaireResponse)
			.map(param -> (QuestionnaireResponse) param.getResource())
			.collect(Collectors.toList());
		
		assertTrue(questionnaireResponses.size() == 1);
		
		QuestionnaireResponse questionnaireResponse = questionnaireResponses.get(0);
		
		assertNotNull(questionnaireResponse);
		assertNotNull(questionnaireResponse.getQuestionnaire());
		assertEquals("Questionnaire/1", questionnaireResponse.getQuestionnaire().getReference());
		assertEquals(QuestionnaireResponseStatus.COMPLETED, questionnaireResponse.getStatus());
		
		assertTrue(questionnaireResponse.getItem().size() == 1);
		
		QuestionnaireResponseItemComponent item = questionnaireResponse.getItemFirstRep();
		
		assertNotNull(item);
		assertEquals("1", item.getLinkId());
		assertEquals("Test question", item.getText());
		
		assertTrue(item.getAnswer().size() == 1);
		
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
		
		assertTrue(requestIdParams.size() == 1);
		assertNotNull(requestIdParams.get(0).getValue());
		assertEquals("1", requestIdParams.get(0).getValue().primitiveValue());
	}


	private void testContextParamsAreCorrect(List<ParametersParameterComponent> parameterComponents) {
		//Get parameter "inputParameters"
		List<ParametersParameterComponent> inputParams = parameterComponents.stream()
				.filter(param -> param.getName().equals("inputParameters"))
				.collect(Collectors.toList());
		
		//Check not null and has resource
		assertTrue(inputParams.size() == 1);
		assertNotNull(inputParams.get(0).getResource());
		
		//Get inputParameters
		Parameters inputParamsResource = (Parameters) inputParams.get(0).getResource();
		
		List<ParametersParameterComponent> inputParamComponents = inputParamsResource.getParameter();
		
		//Check inputParameters has 1 parameter - "context"
		assertTrue(inputParamComponents.size() == 1);
		assertEquals("context", inputParamComponents.get(0).getName());
		
		List<ParametersParameterComponent> contextParams = inputParamComponents.get(0).getPart();
				
		//Check context parameters has two parameters
		assertTrue(contextParams.size() == 2);
		
		List<ParametersParameterComponent> skillsetParams = contextParams.stream()
				.filter(param -> param.getName().equals("skillset"))
				.collect(Collectors.toList());
		
		assertTrue(skillsetParams.size() == 1);
		assertNotNull(skillsetParams.get(0).getValue());
		
		//Check skillset parameter is correct
		assertEquals("CL", skillsetParams.get(0).getValue().primitiveValue());
		
		List<ParametersParameterComponent> partyParams = contextParams.stream()
				.filter(param -> param.getName().equals("party"))
				.collect(Collectors.toList());
		
		assertTrue(partyParams.size() == 1);
		assertNotNull(partyParams.get(0).getValue());
		
		//Check party parameter is correct
		assertEquals("1", partyParams.get(0).getValue().primitiveValue());
	}

	private void testPatientParamIsCorrect(List<ParametersParameterComponent> parameterComponents) {
		List<ParametersParameterComponent> patientParams = parameterComponents.stream()
				.filter(param -> param.getName().equals("patient"))
				.collect(Collectors.toList());
		
		assertTrue(patientParams.size() == 1);
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
