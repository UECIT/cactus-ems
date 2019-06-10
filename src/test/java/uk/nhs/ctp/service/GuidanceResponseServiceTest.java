package uk.nhs.ctp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.service.resolver.GuidanceResponseResolver;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GuidanceResponseServiceTest {

	@Autowired
	private GuidanceResponseResolver guidanceResponseService;

	GuidanceResponse successResponse, dataRequiredResponse, outputDataMultipleResponse, outputDataSingularResponse,
			invalidResponse;

	RequestGroup result;
	CarePlan careplan;
	Parameters outputParameters1, outputParameters2;
	Observation observation1, observation2;
	DataRequirement dataRequirement;

	@Before
	public void setup() {
		
		result = new RequestGroup();
		result.setStatus(RequestStatus.ACTIVE);
		result.setIntent(RequestIntent.ORDER);
		
		careplan = new CarePlan();
		careplan.setStatus(CarePlanStatus.ACTIVE);
		careplan.setIntent(CarePlanIntent.ORDER);
		careplan.addActivity().addOutcomeCodeableConcept().addCoding().setCode("disposition").setDisplay("result");

		observation1 = new Observation().setStatus(ObservationStatus.FINAL)
				.setCode(
						new CodeableConcept().addCoding(new Coding("http://snomed.info/sct", "12345", "Observation 1")))
				.setValue(new BooleanType(true));

		observation2 = new Observation().setStatus(ObservationStatus.FINAL)
				.setCode(
						new CodeableConcept().addCoding(new Coding("http://snomed.info/sct", "12345", "Observation 2")))
				.setValue(new BooleanType(false));

		outputParameters1 = new Parameters();
		outputParameters1.setId("#outputParameters");
		outputParameters1.addParameter().setName("outputData").setResource(observation1);

		outputParameters2 = new Parameters();
		outputParameters2.setId("#outputParameters");
		outputParameters2.addParameter().setName("outputData").setResource(observation1);
		outputParameters2.addParameter().setName("outputData").setResource(observation2);

		dataRequirement = new DataRequirement();
		dataRequirement.setId("1");
		dataRequirement.setType("QuestionnaireResponse")
				.addProfile("https://www.hl7.org/fhir/questionnaireresponse.html");
		dataRequirement.addExtension().setUrl("https://www.hl7.org/fhir/questionnaire.html")
				.setValue(new Reference(new IdType("Questionnaire", "1")));
		dataRequirement.addExtension().setUrl("https://www.hl7.org/fhir/observation.html")
				.setValue(new Reference(new IdType("Observation", "12")));

		successResponse = new GuidanceResponse().setContext(new Reference(new IdType("ServiceDefinition", "1")))
				.setStatus(GuidanceResponseStatus.SUCCESS).setRequestId("1").setResult(new Reference(result));

		dataRequiredResponse = new GuidanceResponse().setContext(new Reference(new IdType("ServiceDefinition", "1")))
				.setStatus(GuidanceResponseStatus.DATAREQUIRED).setRequestId("1").addDataRequirement(dataRequirement);

		outputDataSingularResponse = new GuidanceResponse()
				.setContext(new Reference(new IdType("ServiceDefinition", "1")))
				.setStatus(GuidanceResponseStatus.INPROGRESS).setRequestId("1")
				.setOutputParameters(new Reference(outputParameters1));

		outputDataMultipleResponse = new GuidanceResponse()
				.setContext(new Reference(new IdType("ServiceDefinition", "1")))
				.setStatus(GuidanceResponseStatus.INPROGRESS).setRequestId("1")
				.setOutputParameters(new Reference(outputParameters2));

		invalidResponse = new GuidanceResponse().setContext(new Reference(new IdType("ServiceDefinition", "1")))
				.setStatus(GuidanceResponseStatus.DATAREQUIRED).setRequestId("1");
		
		result.addAction().setResource(new Reference(careplan));

	}

//	@Test
//	public void testResultRetrievedWhenStatusIsSuccess() {
//		spyGuidanceResponseService.processGuidanceResponse(successResponse, 1L, 1L);
//		
//		verify(spyGuidanceResponseService, times(1)).getResult(successResponse);
//	}

//	@Test
//	public void testQuestionnaireReferenceRetrievedWhenStatusIsDataRequired() {
//		spyGuidanceResponseService.processGuidanceResponse(dataRequiredResponse, 1L, 1L);
//		
//		verify(spyGuidanceResponseService, times(1)).getQuestionnaireReference(dataRequiredResponse);
//	}

	@Test
	public void testGetResultRetrievesResultCorrectly() {
		RequestGroup returnedResult = guidanceResponseService.getResult(successResponse);
		
		assertNotNull(returnedResult);
		assertThat(returnedResult).isEqualToComparingFieldByField(result);
	}

//	@Test
//	public void testGetQuestionnaireReferenceRetrievesReferenceCorrectly() {
//		String questionnaireRef = guidanceResponseService.getQuestionnaireReference(dataRequiredResponse);
//		
//		assertNotNull(questionnaireRef);
//		assertEquals("Questionnaire/1", questionnaireRef);
//	}

	@Test(expected = EMSException.class)
	public void testExceptionThrownWhenQuestionnaireReferenceNotPresentAndStatusIsDataRequired() {
		guidanceResponseService.getQuestionnaireReference(invalidResponse);
	}

}
