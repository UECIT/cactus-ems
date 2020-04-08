package uk.nhs.ctp.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.MedicationAdministration;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Parameters;
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
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CaseImmunization;
import uk.nhs.ctp.entities.CaseMedication;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.SelectedServiceRequestDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.transform.CaseObservationTransformer;
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

  @Mock
  private CaseRepository mockCaseRepository;
  @Mock
  private PatientRepository mockPatientRepository;
  @Mock
  private GenericResourceLocator resourceLocator;
  @Mock
  private StorageService storageService;
  @Mock
  private AppointmentService appointmentService;

  @Mock
  private CaseObservationTransformer caseObservationTransformer;

  @Mock
  Observation observation;
  @Mock
  CaseObservation caseObservation;
  @Mock
  CaseImmunization caseImmunization;
  @Mock
  CaseMedication caseMedication;
  @Mock
  Condition condition;

  @InjectMocks
  private CaseService spyCaseService;

  PatientEntity patient;
  Cases triageCase;
  Immunization immunization;
  MedicationAdministration medication;

  List<Resource> resourcesObservationsOnly, resourcesImmunizationsOnly, resourcesUnknownType;
  Parameters resourcesMedicationsOnly, resourcesMultiple;

  @Before
  public void setup() {
    spyCaseService = spy(new CaseService(
        mockCaseRepository,
        resourceLocator,
        storageService,
        caseObservationTransformer,
        referralRequestService,
        referralRequestTransformer,
        appointmentService
    ));

    MockitoAnnotations.initMocks(this);
    patient = new PatientEntity();
    patient.setId(1L);
    patient.setFirstName("Joe");
    patient.setLastName("Bloggs");
    patient.setGender("male");

    triageCase = new Cases();
    triageCase.setId(1L);
    triageCase.setPatientId("Patient/1");

    resourcesObservationsOnly = new ArrayList<>();
    resourcesImmunizationsOnly = new ArrayList<>();
    resourcesMedicationsOnly = new Parameters();
    resourcesMultiple = new Parameters();
    resourcesUnknownType = new ArrayList<>();

    resourcesObservationsOnly.add(observation);

    CodeableConcept concept = new CodeableConcept()
        .addCoding(new Coding("system", "code", "display"));

    immunization = new Immunization().setVaccineCode(concept);
    resourcesImmunizationsOnly.add(immunization);

    medication = new MedicationAdministration().setMedication(concept);
    resourcesMedicationsOnly.addParameter()
        .setResource(medication)
        .setName(SystemConstants.OUTPUT_DATA);

    resourcesMultiple.addParameter().setResource(observation);
    resourcesMultiple.addParameter().setResource(immunization);
    resourcesMultiple.addParameter().setResource(medication);
    resourcesMultiple.getParameter().forEach(p -> p.setName(SystemConstants.OUTPUT_DATA));
    resourcesUnknownType.add(condition);

    when(mockPatientRepository.findOne(1L)).thenReturn(patient);
    when(mockCaseRepository.findOne(1L)).thenReturn(triageCase);
    when(mockCaseRepository.save(any(Cases.class))).thenReturn(triageCase);
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

    SelectedServiceRequestDTO serviceRequestDTO = new SelectedServiceRequestDTO();
    serviceRequestDTO.setCaseId(1L);
    serviceRequestDTO.setSelectedServiceId("HealthcareService/5");
    serviceRequestDTO.setServiceTypes(Collections.emptyList());
    Cases cases = spyCaseService.updateSelectedService(serviceRequestDTO);
    referralRequestEntity = cases.getReferralRequest();
    ReferralRequest referralRequest = referralRequestEntityTransformer
        .transform(referralRequestEntity);

    assertEquals("HealthcareService/5", referralRequest.getRecipientFirstRep().getReference());
  }
}
