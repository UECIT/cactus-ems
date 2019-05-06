package uk.nhs.ctp.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanIntent;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Enumerations.PublicationStatus;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemComponent;
import org.hl7.fhir.dstu3.model.Questionnaire.QuestionnaireItemType;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestIntent;
import org.hl7.fhir.dstu3.model.RequestGroup.RequestStatus;
import org.hl7.fhir.exceptions.FHIRException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import uk.nhs.ctp.service.dto.CdssResponseDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.TriageQuestion;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ResponseServiceTest {
	
	@Autowired
	private ResponseService responseService;
	
	CdssResult resultOnlyCdssResult, dataRequirementOnlyCdssResult, dataRequirementAndResultCdssResult;
	Questionnaire questionnaire;
	RequestGroup result;
	Long caseId, cdssSupplierId;
	String serviceDefinitionId, resultString;
	
	@Before
	public void setup() {
		caseId = 1L;
		cdssSupplierId = 1L;
		serviceDefinitionId = "1";
		
		questionnaire = new Questionnaire();
		questionnaire.setId("1");
		questionnaire.setStatus(PublicationStatus.ACTIVE);
		
		QuestionnaireItemComponent question = questionnaire.addItem();
		question.setLinkId("1");
		question.setType(QuestionnaireItemType.CHOICE);
		question.setText("Test question");
		
		question.addOption().setValue(new Coding()
				.setCode("1")
				.setDisplay("Answer 1"));
		
		question.addOption().setValue(new Coding()
				.setCode("2")
				.setDisplay("Answer 2"));
		
		resultString = "Test result";
		CarePlan careplan = new CarePlan();
		careplan.setStatus(CarePlanStatus.ACTIVE);
		careplan.setIntent(CarePlanIntent.ORDER);
		careplan.addActivity()
   	 		.addOutcomeCodeableConcept()
   	 			.addCoding()
   	 			.setCode("disposition")
   	 			.setDisplay(resultString);
		
		result = new RequestGroup();
		result.setStatus(RequestStatus.ACTIVE);
		result.setIntent(RequestIntent.ORDER);
		
		result.addAction().setResource(new Reference(careplan));
		
		resultOnlyCdssResult = new CdssResult();
		resultOnlyCdssResult.setServiceDefinitionId(serviceDefinitionId);
		resultOnlyCdssResult.setResult(result);
				
		dataRequirementOnlyCdssResult = new CdssResult();
		dataRequirementOnlyCdssResult.setServiceDefinitionId(serviceDefinitionId);
		dataRequirementOnlyCdssResult.setQuestionnaireId("Questionnaire/1");
		
		dataRequirementAndResultCdssResult = new CdssResult();
		dataRequirementAndResultCdssResult.setServiceDefinitionId(serviceDefinitionId);
		dataRequirementAndResultCdssResult.setQuestionnaireId("Questionnaire/1");
		dataRequirementAndResultCdssResult.setResult(result);
	}

	@Test
	public void testResponseBuiltCorrectlyForResultOnly() throws FHIRException {
		assertTrue(resultOnlyCdssResult.hasResult());
		assertFalse(resultOnlyCdssResult.hasQuestionnaire());
		
		CdssResponseDTO response = responseService.buildResponse(resultOnlyCdssResult, null, caseId, cdssSupplierId);
	
		assertNotNull(response);
		assertEquals(resultString, response.getResult());
		assertNull(response.getTriageQuestions());
		assertEquals(serviceDefinitionId, response.getServiceDefinitionId());
		assertEquals(caseId, response.getCaseId());
		assertEquals(cdssSupplierId, response.getCdssSupplierId());

	}
	
	@Test
	public void testResponseBuiltCorrectlyForDataRequirementOnly() throws FHIRException {
		assertFalse(dataRequirementOnlyCdssResult.hasResult());
		assertTrue(dataRequirementOnlyCdssResult.hasQuestionnaire());
		assertNotNull(questionnaire);
		
		CdssResponseDTO response = responseService.buildResponse(dataRequirementOnlyCdssResult, questionnaire, caseId, cdssSupplierId);
		
		assertNotNull(response);
		assertNull(response.getResult());
		assertEquals(serviceDefinitionId, response.getServiceDefinitionId());
		assertEquals(caseId, response.getCaseId());
		assertEquals(cdssSupplierId, response.getCdssSupplierId());
		
		List<TriageQuestion> triageQuestion = response.getTriageQuestions();
		
		assertNotNull(triageQuestion);
		assertEquals("Test question", triageQuestion.get(0).getQuestion());
		assertEquals("1", triageQuestion.get(0).getQuestionnaireId());
		assertEquals("1", triageQuestion.get(0).getQuestionId());
		assertNull(triageQuestion.get(0).getResponse());
		assertTrue(triageQuestion.get(0).getOptions().size() == 2);

	}
	
	@Test
	public void testResponseContainsResultOnlyIfDataRequirementAndResultPresent() throws FHIRException {
		assertTrue(dataRequirementAndResultCdssResult.hasResult());
		assertTrue(dataRequirementAndResultCdssResult.hasQuestionnaire());
		assertNotNull(questionnaire);
		
		CdssResponseDTO response = responseService.buildResponse(dataRequirementAndResultCdssResult, questionnaire, caseId, cdssSupplierId);
		
		assertNotNull(response);
		assertEquals(resultString, response.getResult());
		assertNull(response.getTriageQuestions());
		assertEquals(serviceDefinitionId, response.getServiceDefinitionId());
		assertEquals(caseId, response.getCaseId());
		assertEquals(cdssSupplierId, response.getCdssSupplierId());
	}

}
