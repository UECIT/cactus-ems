package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isFhir;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.AllergyIntolerance.AllergyIntoleranceCriticality;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Immunization;
import org.hl7.fhir.dstu3.model.Immunization.ImmunizationStatus;
import org.hl7.fhir.dstu3.model.Medication;
import org.hl7.fhir.dstu3.model.Narrative;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.RequestGroup;
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
import uk.nhs.ctp.enums.Gender;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.PractitionerDTO;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.testhelper.fixtures.PatientFixtures;

@RunWith(MockitoJUnitRunner.class)
public class CaseServiceTest {

  @InjectMocks
  private CaseService caseService;

  @Mock
  private CaseRepository caseRepository;

  @Mock
  private GenericResourceLocator resourceLocator;

  @Mock
  private StorageService storageService;

  @Mock
  private TokenAuthenticationService authService;

  @Mock
  private NarrativeService narrativeService;

  @Mock
  private ReferenceService referenceService;

  @Mock
  private Clock clock;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private static final String SUPPLIER = "supplierId";
  private static final Instant FIXED_INSTANT =
      LocalDateTime.of(2004, 3, 3, 4, 3).toInstant(ZoneOffset.UTC);

  @Before
  public void setup() {
    when(clock.instant()).thenReturn(FIXED_INSTANT);
    when(authService.requireSupplierId()).thenReturn(SUPPLIER);
    when(narrativeService.buildNarrative(anyString())).thenReturn(new Narrative());
  }

  @Test
  public void findCase_caseExists() {
    Cases expected = new Cases();
    expected.setPatientId("some/patient");
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.of(expected));

    Cases actual = caseService.findCase(4L);

    assertThat(actual, is(expected));
  }

  @Test
  public void findCase_notFound() {
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.empty());

    expectedException.expect(EMSException.class);
    caseService.findCase(4L);
  }

  @Test
  public void getCaseParameters_caseExists() {
    Cases caseEntity = new Cases();
    CaseParameter expected = new CaseParameter();
    expected.setReference("Some/Resource");
    caseEntity.addParameter(expected);
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.of(caseEntity));

    List<CaseParameter> actual = caseService.getCaseParameters(4L);

    assertThat(actual, contains(expected));
  }

  @Test
  public void getCaseParameters_notFound() {
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.empty());

    expectedException.expect(EMSException.class);
    caseService.getCaseParameters(4L);
  }

  @Test
  public void createCase_noPractitioner() {
    String patientRef = "Patient/1234";
    Cases expected = new Cases();
    expected.setPatientId(patientRef);
    expected.setSupplierId(SUPPLIER);
    when(caseRepository.saveAndFlush(argThat(samePropertyValuesAs(expected))))
        .thenReturn(expected);

    Cases createdCase = caseService.createCase(patientRef, null);

    assertThat(createdCase, is(expected));
  }

  @Test
  public void createCase_withPractitioner() {
    String patientRef = "Patient/1234";
    PractitionerDTO practitioner = new PractitionerDTO("gp", "Arthur C Clarke");
    Cases expected = new Cases();
    expected.setPatientId(patientRef);
    expected.setSupplierId(SUPPLIER);
    expected.setPractitionerId("gp");
    when(caseRepository.saveAndFlush(argThat(samePropertyValuesAs(expected))))
        .thenReturn(expected);

    Cases createdCase = caseService.createCase(patientRef, practitioner);

    assertThat(createdCase, is(expected));
  }

  @Test
  public void createCase_notAPatientReference() {
    String patientRef = "NotAPatient/1234";

    expectedException.expect(IllegalArgumentException.class);
    caseService.createCase(patientRef, null);

    verifyZeroInteractions(caseRepository);
  }

  @Test
  public void setupCaseDetails_fullPatient() {
    Patient patient = PatientFixtures.phillipKDick();
    String patientId = "Patient/Ref";
    Cases initialCase = new Cases();
    initialCase.setPatientId(patientId);
    initialCase.setId(2L);
    Reference subjectRef = new Reference(patientId);
    Reference contextRef = new Reference("Encounter/2");
    Observation genderObs = buildGenderObs(subjectRef, contextRef, Gender.MALE);
    Observation ageObs = buildAgeObs(subjectRef, contextRef, "1928-12-16");

    when(resourceLocator.findResource(patientId)).thenReturn(patient);
    when(referenceService.buildRef(ResourceType.Patient, patientId))
        .thenReturn(subjectRef);
    when(referenceService.buildRef(ResourceType.Encounter, 2L))
        .thenReturn(contextRef);
    String genderObsId = "GenderObsId";
    String ageObsId = "AgeObsId";
    when(storageService.storeExternal(argThat(isFhir(genderObs))))
        .thenReturn(genderObsId);
    when(storageService.storeExternal(argThat(isFhir(ageObs))))
        .thenReturn(ageObsId);

    caseService.setupCaseDetails(initialCase, "Patient/Ref");

    Cases updatedCase = new Cases();
    updatedCase.setId(2L);
    updatedCase.setPatientId(patientId);
    updatedCase.setFirstName("Phillip Kindred");
    updatedCase.setLastName("Dick");
    updatedCase.setNhsNumber("123456");
    updatedCase.setCreatedDate(Date.from(FIXED_INSTANT));
    CaseParameter genderParam = new CaseParameter();
    genderParam.setReference("GenderObsId");
    genderParam.setTimestamp(Date.from(FIXED_INSTANT));
    updatedCase.addParameter(genderParam);
    CaseParameter ageParam = new CaseParameter();
    ageParam.setReference("AgeObsId");
    ageParam.setTimestamp(Date.from(FIXED_INSTANT));
    updatedCase.addParameter(ageParam);

    verify(caseRepository).saveAndFlush(updatedCase);
  }

  @Test
  public void setupCaseDetails_minimumPatient() {
    Patient patient = PatientFixtures.minimumPatient();
    String patientId = "Patient/Ref";
    Cases initialCase = new Cases();
    initialCase.setPatientId(patientId);
    initialCase.setId(2L);
    Reference subjectRef = new Reference(patientId);
    Reference contextRef = new Reference("Encounter/2");

    when(resourceLocator.findResource(patientId)).thenReturn(patient);
    when(referenceService.buildRef(ResourceType.Patient, patientId))
        .thenReturn(subjectRef);
    when(referenceService.buildRef(ResourceType.Encounter, 2L))
        .thenReturn(contextRef);

    caseService.setupCaseDetails(initialCase, patientId);

    Cases updatedCase = new Cases();
    updatedCase.setCreatedDate(Date.from(FIXED_INSTANT));
    updatedCase.setId(2L);
    updatedCase.setPatientId(patientId);

    verifyZeroInteractions(storageService);
    verify(caseRepository).saveAndFlush(updatedCase);
  }

  @Test
  public void updateCase_softDeletesExistingParams() {
    Cases caseEntity = new Cases();
    CaseParameter parameter = new CaseParameter();
    parameter.setReference("Some/Resource");
    parameter.setDeleted(false);
    caseEntity.addParameter(parameter);
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.of(caseEntity));

    caseService.updateCase(4L, new CdssResult());

    Cases expected = new Cases();
    CaseParameter deleted = new CaseParameter();
    deleted.setReference("Some/Resource");
    deleted.setDeleted(true);
    expected.addParameter(deleted);
    verify(caseRepository).saveAndFlush(expected);
  }

  @Test
  public void updateCase_addsReferenceParams() {
    Cases caseEntity = new Cases();
    CdssResult result = new CdssResult();
    result.setOutputData(new Parameters()
      .addParameter(new ParametersParameterComponent()
        .setName("ref")
        .setValue(new Reference("Some/Resource"))));
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER)).thenReturn(Optional.of(caseEntity));

    caseService.updateCase(4L, result);

    Cases expected = new Cases();
    CaseParameter param = new CaseParameter();
    param.setReference("Some/Resource");
    param.setTimestamp(Date.from(FIXED_INSTANT));
    param.setDeleted(false);
    expected.addParameter(param);

    verify(caseRepository).saveAndFlush(expected);
    verifyZeroInteractions(storageService);
  }

  @Test
  public void updateCase_savesAndAddsResourceParams() {
    Cases caseEntity = new Cases();
    CdssResult result = new CdssResult();
    Medication resource = new Medication()
        .setIsBrand(true)
        .setIsOverTheCounter(true);
    result.setOutputData(new Parameters()
        .addParameter(new ParametersParameterComponent()
            .setName("ref")
            .setResource(resource)));

    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER)).thenReturn(Optional.of(caseEntity));
    when(storageService.storeExternal(resource))
        .thenReturn("Some/Resource");

    caseService.updateCase(4L, result);

    Cases expected = new Cases();
    CaseParameter param = new CaseParameter();
    param.setReference("Some/Resource");
    param.setTimestamp(Date.from(FIXED_INSTANT));
    param.setDeleted(false);
    expected.addParameter(param);

    verify(caseRepository).saveAndFlush(expected);
  }

  @Test
  public void updateCase_skipsUnsaveableParams() {
    Cases caseEntity = new Cases();
    CdssResult result = new CdssResult();
    result.setOutputData(new Parameters()
        .addParameter(new ParametersParameterComponent()
            .setName("ref")
            .setValue(new StringType("Can't save this"))));
    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER)).thenReturn(Optional.of(caseEntity));

    caseService.updateCase(4L, result);

    Cases expected = new Cases();

    verify(caseRepository).saveAndFlush(expected);
    verifyZeroInteractions(storageService);
  }

  @Test
  public void updateCase_withResult() {
    Cases caseEntity = new Cases();
    caseEntity.setTriageComplete(false);
    CdssResult result = new CdssResult();
    result.setResult(new RequestGroup());

    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER)).thenReturn(Optional.of(caseEntity));

    caseService.updateCase(4L, result);

    Cases expected = new Cases();
    expected.setTriageComplete(true);
    expected.setClosedDate(Date.from(FIXED_INSTANT));

    verify(caseRepository).saveAndFlush(expected);
    verifyZeroInteractions(storageService);
  }

  @Test
  public void updateCase_mix() {
    Cases caseEntity = new Cases();
    caseEntity.setTriageComplete(false);
    Immunization resource = new Immunization();
    resource.setStatus(ImmunizationStatus.COMPLETED);
    CdssResult result = new CdssResult();
    result.setOutputData(new Parameters()
        .addParameter(new ParametersParameterComponent()
            .setName("ref")
            .setValue(new Reference("Some/Resource1")))
        .addParameter(new ParametersParameterComponent()
            .setName("ref")
            .setResource(resource))
        .addParameter(new ParametersParameterComponent()
            .setName("ref")
            .setValue(new StringType("Can't save this"))));
    result.setResult(new RequestGroup());

    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER)).thenReturn(Optional.of(caseEntity));
    when(storageService.storeExternal(resource))
        .thenReturn("Some/Resource2");

    caseService.updateCase(4L, result);

    Cases expected = new Cases();
    CaseParameter param = new CaseParameter();
    param.setReference("Some/Resource1");
    param.setTimestamp(Date.from(FIXED_INSTANT));
    param.setDeleted(false);
    expected.addParameter(param);
    CaseParameter param2 = new CaseParameter();
    param2.setReference("Some/Resource2");
    param2.setTimestamp(Date.from(FIXED_INSTANT));
    param2.setDeleted(false);
    expected.addParameter(param2);
    expected.setTriageComplete(true);
    expected.setClosedDate(Date.from(FIXED_INSTANT));

    verify(caseRepository).saveAndFlush(expected);
  }

  @Test
  public void addResourceToCase() {
    AllergyIntolerance resource = new AllergyIntolerance();
    resource.setCriticality(AllergyIntoleranceCriticality.HIGH);

    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.of(new Cases()));
    when(storageService.storeExternal(resource))
        .thenReturn("Some/Resource");

    caseService.addResourceToCaseInputData(4L, resource);

    Cases expected = new Cases();
    CaseParameter param = new CaseParameter();
    param.setTimestamp(Date.from(FIXED_INSTANT));
    param.setDeleted(false);
    param.setReference("Some/Resource");
    expected.addParameter(param);

    verify(caseRepository).saveAndFlush(expected);
  }

  @Test
  public void addResourceToCase_notFound() {
    AllergyIntolerance resource = new AllergyIntolerance();
    resource.setCriticality(AllergyIntoleranceCriticality.HIGH);

    when(caseRepository.getOneByIdAndSupplierId(4L, SUPPLIER))
        .thenReturn(Optional.empty());

    expectedException.expect(EMSException.class);
    caseService.addResourceToCaseInputData(4L, resource);

    verifyZeroInteractions(storageService);
  }

  private Observation buildGenderObs(Reference subject, Reference context, Gender gender) {
    return testObservation(subject, context)
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "263495000", "Gender")))
        .setValue(gender.toCodeableConcept());
  }

  private Observation buildAgeObs(Reference subject, Reference context, String dateOfBirth) {
    return testObservation(subject, context)
        .setCode(new CodeableConcept(new Coding(SystemURL.SNOMED, "397669002", "Age")))
        .setValue(new StringType(dateOfBirth));
  }

  private Observation testObservation(Reference subject, Reference context) {
    Observation genderObs = new Observation()
        .setStatus(ObservationStatus.FINAL)
        .setIssued(Date.from(FIXED_INSTANT))
        .setSubject(subject)
        .setContext(context);
    genderObs.setText(new Narrative());
    return genderObs;
  }
}