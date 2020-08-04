package uk.nhs.ctp.service;

import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import java.util.Collections;
import java.util.Optional;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.CareConnectPatient;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterStatus;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.config.FHIRConfig;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.CompositionEntity;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.transform.CompositionEntityTransformer;
import uk.nhs.ctp.transform.CompositionTransformer;

@RunWith(MockitoJUnitRunner.class)
public class CompositionServiceTest {

  public static final String SUPPLIER = "supplier";
  @Mock
  private ReferenceService referenceService;

  @Mock
  private GenericResourceLocator resourceLocator;

  @Mock
  private CaseRepository caseRepository;

  @Mock
  private CompositionTransformer compositionTransformer;

  @Mock
  private CompositionEntityTransformer compositionEntityTransformer;

  @Mock()
  private NarrativeService narrativeService;

  @Mock
  private ReferralRequestService referralRequestService;

  @Mock
  private CarePlanService carePlanService;

  @Mock
  private FhirContext mockedFhirContext;

  @Mock
  private TokenAuthenticationService authService;

  @InjectMocks
  private CompositionService compositionService;

  private CdssResult cdssResult;

  private FhirContext fhirContext;

  @Before
  public void setup() {
    fhirContext = new FHIRConfig(null, null).fhirContext();
    when(mockedFhirContext.newJsonParser()).thenReturn(fhirContext.newJsonParser());

    buildInterimCase();

    when(referenceService.buildRef(ResourceType.Patient, "Patient/1"))
        .thenReturn(new Reference("Patient/1"));
    when(referenceService.buildRef(ResourceType.Encounter, 1))
        .thenReturn(new Reference("Encounter/1"));
    when(referenceService.buildRef(ResourceType.Device, EMSDeviceService.MAIN_ID))
        .thenReturn(new Reference("Device/" + EMSDeviceService.MAIN_ID));
    when(referenceService.buildRef(ResourceType.Observation, 1))
        .thenReturn(new Reference("Observation/1"));

    when(compositionTransformer.transform(Mockito.any(Composition.class)))
        .thenReturn(new CompositionEntity());

    when(compositionEntityTransformer.transform(Mockito.any(CompositionEntity.class)))
        .thenReturn(new Composition());

    //TODO: CDSCT-129 improve test coverage of this class, these mocks are just to get it to pass
    when(referralRequestService.getByCaseId(1L)).thenReturn(Collections.emptyList());
    when(carePlanService.getByCaseId(1L)).thenReturn(Collections.emptyList());

    when(authService.requireSupplierId()).thenReturn(SUPPLIER);
    doNothing().when(authService).requireSupplierId(SUPPLIER);
    doThrow(AuthenticationException.class).when(authService).requireSupplierId(any());
  }

  @Test
  public void composition_contains_initial_details() {
    compositionService.crupdate(1L, cdssResult);

    verify(referenceService, times(1))
        .buildRef(ResourceType.Encounter, 1);
    verify(referenceService, times(1))
        .buildRef(ResourceType.Patient, "Patient/1");
  }

  @Test
  public void composition_contains_referenced_resources() {
    compositionService.crupdate(1L, cdssResult);

    var caseCaptor = ArgumentCaptor.forClass(Cases.class);
    verify(caseRepository, times(1)).saveAndFlush(caseCaptor.capture());
    var caseComposition = caseCaptor.getValue().getCompositions().get(0);

    assertNotNull(caseComposition);
    var composition = fhirContext.newJsonParser()
        .parseResource(Composition.class, caseComposition.getResource());

    assertThat(composition.getSection(), contains(isInterimSection()));
  }

  private void buildInterimCase() {
    CareConnectPatient patient = new CareConnectPatient();
    patient.setId("Patient/1");
    locateResource("Patient/1", patient);

    Encounter encounter = new Encounter();
    encounter.setId("Encounter/1");
    locateResource("Encounter/1", encounter);
    encounter.setStatus(EncounterStatus.TRIAGED);
    encounter.setSubject(new Reference("Patient/1"));

    Observation observation = new Observation()
        .setCode(new CodeableConcept()
            .addCoding(new Coding()
                .setSystem("test")
                .setCode("observation")
                .setDisplay("Test observation")
            ));
    observation.setId("Observation/1");
    locateResource("Observation/1", observation);
    CaseObservation caseObservation = new CaseObservation();
    caseObservation.setId(1L);
    caseObservation.setDisplay("test code");
    caseObservation.setValueDisplay("Test observation");

    Cases caseEntity = new Cases();
    caseEntity.setId(1L);
    caseEntity.setPatientId("Patient/1");
    caseEntity.addObservation(caseObservation);
    when(caseRepository.getOneByIdAndSupplierId(1L, SUPPLIER)).thenReturn(Optional.of(caseEntity));

    cdssResult = new CdssResult();
    cdssResult.setRequestId("Request/id");
  }

  private Matcher<Object> isInterimSection() {
    return both(hasProperty("title", containsString("Request/id")))
        .and(hasProperty("entry", contains(isReferenceTo("Observation/1"))));
  }

  private void locateResource(String id, DomainResource resource) {
    when(resourceLocator.findResource(argThat(isReferenceTo(id)))).thenReturn(resource);
  }

  private static Matcher<Reference> isReferenceTo(String id) {
    return hasProperty("reference", endsWith(id));
  }
}