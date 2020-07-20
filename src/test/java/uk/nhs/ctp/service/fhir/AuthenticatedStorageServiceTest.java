package uk.nhs.ctp.service.fhir;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.referenceTo;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.IUpdateTyped;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.security.AuthenticatedFhirClientFactory;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticatedStorageServiceTest {

  private static final String FHIR_SERVER_URL = "http://some.fhir.place";

  @InjectMocks
  private AuthenticatedStorageService storageService;

  @Mock
  private AuthenticatedFhirClientFactory clientFactory;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient mockClient;

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Before
  public void setup() {
    ReflectionTestUtils.setField(storageService, "fhirServer", FHIR_SERVER_URL);
  }

  @Test
  public void create_shouldCreate() {
    Patient resourceToCreate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    IdType expectedId = new IdType("Patient/1");
    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    when((Object)mockClient.create()
        .resource(resourceToCreate)
        .execute()).thenReturn(new MethodOutcome(expectedId));

    Reference returned = storageService.create(resourceToCreate);

    assertThat(returned, referenceTo(resourceToCreate));
  }

  @Test
  public void create_shouldFail_withId() {
    Patient resourceToCreate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    resourceToCreate.setId("id");

    expectedException.expect(IllegalArgumentException.class);
    storageService.create(resourceToCreate);
  }

  @Test
  public void upsert_withNoId_shouldCreate() {
    Patient resourceToCreate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    IdType expectedId = new IdType("Patient/1");
    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    when((Object)mockClient.create()
        .resource(resourceToCreate)
        .execute()).thenReturn(new MethodOutcome(expectedId));

    Reference returned = storageService.upsert(resourceToCreate);

    assertThat(returned, referenceTo(resourceToCreate));
    verify(mockClient, never()).update();
  }

  @Test
  public void upsert_withRelativeId_shouldUpdate() {
    Patient resourceToUpdate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    resourceToUpdate.setId(new IdType("Patient/1"));

    when(clientFactory.getClient(FHIR_SERVER_URL))
        .thenReturn(mockClient);
    IUpdateTyped mockUpdate = mock(IUpdateTyped.class);
    when(mockClient.update().resource(resourceToUpdate))
        .thenReturn(mockUpdate);

    Reference returned = storageService.upsert(resourceToUpdate);

    assertThat(returned, referenceTo(resourceToUpdate));
    verify(mockUpdate).execute();
    verify(mockClient, never()).create();
  }

  @Test
  public void upsert_withAbsoluteId_shouldUpdate() {
    Patient resourceToUpdate = new Patient()
        .addName(new HumanName().addGiven("Pele"));
    resourceToUpdate.setId(new IdType("http://validServer.com/fhir/Patient/1"));

    when(clientFactory.getClient("http://validServer.com/fhir"))
        .thenReturn(mockClient);
    IUpdateTyped mockUpdate = mock(IUpdateTyped.class);
    when(mockClient.update().resource(resourceToUpdate))
        .thenReturn(mockUpdate);

    Reference returned = storageService.upsert(resourceToUpdate);

    assertThat(returned, referenceTo(resourceToUpdate));
    verify(mockUpdate).execute();
    verify(mockClient, never()).create();
  }
}