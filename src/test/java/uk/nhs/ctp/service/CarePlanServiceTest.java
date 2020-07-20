package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static uk.nhs.ctp.testhelper.matchers.FhirMatchers.isFhir;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.CarePlan.CarePlanStatus;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.fhir.AuthenticatedStorageService;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.testhelper.MockingUtils;

@RunWith(MockitoJUnitRunner.class)
public class CarePlanServiceTest {

  @InjectMocks
  private CarePlanService carePlanService;

  @Mock
  private StorageService storageService;

  @Mock
  private AuthenticatedStorageService authenticatedStorageService;

  @Mock
  private ReferenceService referenceService;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private IGenericClient mockClient;

  @Before
  public void setup() {
    when(storageService.getClient()).thenReturn(mockClient);
  }

  @Test
  public void shouldFetchCarePlansByCaseId() {
    CarePlan expected = new CarePlan()
        .setContext(new Reference("123"))
        .setDescription("desc");
    Bundle returns = new Bundle()
        .addEntry(new BundleEntryComponent()
          .setResource(expected));
    MockingUtils.mockSearch(mockClient, CarePlan.class, returns);

    List<CarePlan> results = carePlanService.getByCaseId(3L);

    verify(referenceService).buildId(ResourceType.Encounter, 3L);
    assertThat(results, contains(expected));
  }

  @Test
  public void shouldReturnEmptyListNoCarePlansFound() {
    MockingUtils.mockSearch(mockClient, CarePlan.class, new Bundle());

    List<CarePlan> results = carePlanService.getByCaseId(3L);

    verify(referenceService).buildId(ResourceType.Encounter, 3L);
    assertThat(results, empty());
  }

  @Test
  public void shouldUpdateExternalCarePlans() {
    var carePlan1 = new CarePlan();
    carePlan1.setId("id1");
    carePlan1.setStatus(CarePlanStatus.ACTIVE);
    var carePlan2 = new CarePlan();
    carePlan2.setId("id2");
    carePlan2.setStatus(CarePlanStatus.DRAFT);

    var completedCarePlan1 = carePlan1.copy().setStatus(CarePlanStatus.COMPLETED);
    var completedCarePlan2 = carePlan2.copy().setStatus(CarePlanStatus.COMPLETED);

    when(authenticatedStorageService.get("id1", CarePlan.class)).thenReturn(carePlan1);
    when(authenticatedStorageService.get("id2", CarePlan.class)).thenReturn(carePlan2);

    carePlanService.completeCarePlans(new String[]{"id1", "id2"});

    verify(authenticatedStorageService, times(0)).upsert(argThat(isFhir(completedCarePlan1)));
    verify(authenticatedStorageService).upsert(argThat(isFhir(completedCarePlan2)));
  }

}