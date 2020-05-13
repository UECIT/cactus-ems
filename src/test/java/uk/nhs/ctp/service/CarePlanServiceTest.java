package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
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

}