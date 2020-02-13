package uk.nhs.ctp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.EncounterEntity;
import uk.nhs.ctp.entities.IdVersion;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.entities.TestScenario;
import uk.nhs.ctp.repos.EncounterRepository;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.repos.TestScenarioRepository;
import uk.nhs.ctp.service.builder.CareConnectPatientBuilder;
import uk.nhs.ctp.transform.CaseObservationTransformer;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.transform.ReferralRequestEntityTransformer;
import uk.nhs.ctp.transform.ReferralRequestTransformer;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CaseServiceTest {

  @Autowired
  private ReferralRequestService referralRequestService;
  @Autowired
  private ReferralRequestTransformer referralRequestTransformer;
  @Autowired
  private ReferralRequestEntityTransformer referralRequestEntityTransformer;
  @Autowired
  private CareConnectPatientBuilder careConnectPatientBuilder;

  @Mock
  private EncounterRepository mockEncounterRepository;
  @Mock
  private PatientRepository mockPatientRepository;
  @Mock
  private TestScenarioRepository mockTestScenarioRepository;
  @Mock
  private GenericResourceLocator resourceLocator;
  @Mock
  private StorageService storageService;
  @Mock
  private ReferenceService referenceService;

  @Mock
  private CaseObservationTransformer caseObservationTransformer;

  @Mock
  TestScenario testScenario;
  @Mock
  Observation observation;
  @Mock
  Immunization immunization;
  @Mock
  CaseObservation caseObservation;
  @Mock
  CaseImmunization caseImmunization;
  @Mock
  CaseMedication caseMedication;
  @Mock
  MedicationAdministration medication;
  @Mock
  Condition condition;

  @InjectMocks
  private CaseService spyCaseService;

  PatientEntity patient;
  EncounterEntity triageCase;

  List<Resource> resourcesObservationsOnly, resourcesImmunizationsOnly, resourcesMedicationsOnly,
      resourcesMultiple, resourcesUnknownType;

  @Before
  public void setup() {
    spyCaseService = spy(new CaseService(
        mockEncounterRepository,
        mockTestScenarioRepository,
        resourceLocator,
        storageService,
        caseObservationTransformer,
        referralRequestService,
        referralRequestTransformer,
        referenceService
    ));

    MockitoAnnotations.initMocks(this);
    patient = new PatientEntity();
    patient.setId(1L);
    patient.setFirstName("Joe");
    patient.setLastName("Bloggs");
    patient.setGender("male");

    triageCase = new EncounterEntity();
    triageCase.setIdVersion(new IdVersion(1L, 1L));
    triageCase.setPatientId("Patient/1");

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

    when(mockPatientRepository.findOne(1L)).thenReturn(patient);
    when(mockTestScenarioRepository.findByPatientId(1L)).thenReturn(testScenario);
    when(mockEncounterRepository.findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(1L))
        .thenReturn(triageCase);
    when(mockEncounterRepository.save(any(EncounterEntity.class))).thenReturn(triageCase);
    when(observation.getValue()).thenReturn(new BooleanType(true));
    when(caseObservationTransformer.transform(observation)).thenReturn(caseObservation);
    doReturn(caseImmunization).when(spyCaseService).createCaseImmunization(immunization);
    doReturn(caseMedication).when(spyCaseService).createCaseMedication(medication);

    when(storageService.storeExternal(any())).thenAnswer(new Answer<>() {
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
  }

  @Test
  public void testCaseMedicationsStoredWhenOutputDataContainsMedicationsOnly() {
    CdssResult response = new CdssResult();
    response.setOutputData(resourcesMedicationsOnly);
    spyCaseService.updateCase(1L, response, "123456789");

    verify(caseObservationTransformer, times(0)).transform(any());
    verify(spyCaseService, times(0)).createCaseImmunization(any());
    verify(spyCaseService, times(1)).createCaseMedication(any());
  }

  @Test
  public void testAllCaseDataStoredWhenOutputDataContainsMultipleResources() {
    CdssResult response = new CdssResult();
    response.setOutputData(resourcesMultiple);
    spyCaseService.updateCase(1L, response, "123456789");

    verify(caseObservationTransformer, times(1)).transform(any());
    verify(spyCaseService, times(1)).createCaseImmunization(any());
    verify(spyCaseService, times(1)).createCaseMedication(any());
  }

  @Test
  public void testUpdateSelectedService() {
    ReferralRequestEntity referralRequestEntity = referralRequestTransformer
        .transform(new ReferralRequest());
    triageCase.setReferralRequest(referralRequestEntity);

    EncounterEntity cases = spyCaseService.updateSelectedService(1L, "HealthcareService/5");
    referralRequestEntity = cases.getReferralRequest();
    ReferralRequest referralRequest = referralRequestEntityTransformer
        .transform(referralRequestEntity);

    assertEquals("HealthcareService/5", referralRequest.getRecipientFirstRep().getReference());
  }
}
