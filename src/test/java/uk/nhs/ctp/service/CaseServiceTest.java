package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;

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
  }

  @Test
  public void findCase_caseExists() {
    Cases expected = new Cases();
    expected.setPatientId("some/patient");
    expected.setAddress("Some place");
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

  }

  @Test
  public void getCaseParameters_notFound() {

  }

  @Test
  public void createCase_noPractitioner() {

  }

  @Test
  public void createCase_withPractitioner() {

  }

  @Test
  public void createCase_notAPatientReference() {

  }

  @Test
  public void setupCaseDetails_fullPatient() {

  }

  @Test
  public void setupCaseDetails_minimumPatient() {

  }

  @Test
  public void updateCase_softDeletesExistingParams() {

  }

  @Test
  public void updateCase_addsReferenceParams() {

  }

  @Test
  public void updateCase_savesAndAddsResourceParams() {

  }

  @Test
  public void updateCase_skipsUnsaveableParams() {

  }

  @Test
  public void updateCase_withResult() {

  }

  @Test
  public void updateCase_mix() {

  }

  @Test
  public void addResourceToCase() {

  }

  @Test
  public void addResourceToCase_notFound() {

  }
}