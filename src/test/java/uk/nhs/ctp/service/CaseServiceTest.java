package uk.nhs.ctp.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Resource;
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
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.repos.TestScenarioRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CaseServiceTest {
	
	@Autowired
	@InjectMocks
	private CaseService caseService;
	
	@InjectMocks
	private CaseService spyCaseService;
	
	@Mock
	private CaseRepository mockCaseRepository;

	@Mock
	private PatientRepository mockPatientRepository;
	
	@Mock
	private TestScenarioRepository mockTestScenarioRepository;
	
	Observation observation;
	Immunization immunization;
	MedicationAdministration medication;
	Condition condition;
	List<Resource> resourcesObservationsOnly, resourcesImmunizationsOnly, resourcesMedicationsOnly, 
		resourcesMultiple, resourcesUnknownType;
	
	CaseObservation caseObservation;
	CaseImmunization caseImmunization;
	CaseMedication caseMedication;
	Cases triageCase;
	
	@Before
	public void setup() {
		spyCaseService = spy(new CaseService());
		MockitoAnnotations.initMocks(this);
		observation = mock(Observation.class);
		immunization = mock(Immunization.class);
		medication = mock(MedicationAdministration.class);
		condition = mock(Condition.class);
		
		resourcesObservationsOnly = new ArrayList<>();
		resourcesImmunizationsOnly = new ArrayList<>();
		resourcesMedicationsOnly = new ArrayList<>();
		resourcesMultiple = new ArrayList<>();
		resourcesUnknownType = new ArrayList<>();	
		
		resourcesObservationsOnly.add(observation);
		resourcesImmunizationsOnly.add(immunization);
		resourcesMedicationsOnly.add(medication);
		resourcesMultiple.add(observation);
		resourcesMultiple.add(immunization);
		resourcesMultiple.add(medication);
		resourcesUnknownType.add(condition);
		
		caseObservation = mock(CaseObservation.class);
		caseImmunization = mock(CaseImmunization.class);
		caseMedication = mock(CaseMedication.class);
		triageCase = mock(Cases.class);

		when(mockCaseRepository.findById(1L)).thenReturn(Optional.of(triageCase));
		when(mockCaseRepository.save(any(Cases.class))).thenReturn(triageCase);
		when(observation.getValue()).thenReturn(new BooleanType(true));
		doReturn(caseObservation).when(spyCaseService).createCaseObservation(observation);
		doReturn(caseImmunization).when(spyCaseService).createCaseImmunization(immunization);
		doReturn(caseMedication).when(spyCaseService).createCaseMedication(medication);
	}

	@Test
	public void testCaseObservationsStoredWhenOutputDataContainsObservationsOnly() {
		spyCaseService.updateCase(1L, resourcesObservationsOnly, "123456789");
		
		verify(spyCaseService, times(1)).createCaseObservation(any());
		verify(spyCaseService, times(0)).createCaseImmunization(any());
		verify(spyCaseService, times(0)).createCaseMedication(any());
	}
	
	@Test
	public void testCaseImmunizationsStoredWhenOutputDataContainsImmunizationsOnly() {
		spyCaseService.updateCase(1L, resourcesImmunizationsOnly, "123456789");
		
		verify(spyCaseService, times(0)).createCaseObservation(any());
		verify(spyCaseService, times(1)).createCaseImmunization(any());
		verify(spyCaseService, times(0)).createCaseMedication(any());
	}
	
	@Test
	public void testCaseMedicationsStoredWhenOutputDataContainsMedicationsOnly() {
		spyCaseService.updateCase(1L, resourcesMedicationsOnly, "123456789");
		
		verify(spyCaseService, times(0)).createCaseObservation(any());
		verify(spyCaseService, times(0)).createCaseImmunization(any());
		verify(spyCaseService, times(1)).createCaseMedication(any());
	}
	
	@Test
	public void testAllCaseDataStoredWhenOutputDataContainsMultipleResources() {
		spyCaseService.updateCase(1L, resourcesMultiple, "123456789");
		
		verify(spyCaseService, times(1)).createCaseObservation(any());
		verify(spyCaseService, times(1)).createCaseImmunization(any());
		verify(spyCaseService, times(1)).createCaseMedication(any());
	}

}
