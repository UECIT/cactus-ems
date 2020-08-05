package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Observation.ObservationStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.entities.CaseParameter;
import uk.nhs.ctp.service.fhir.StorageService;

@RunWith(MockitoJUnitRunner.class)
public class ObservationServiceTest {

  @InjectMocks
  private ObservationService observationService;

  @Mock
  private StorageService storageService;

  @Mock
  private CaseService caseService;

  @Test
  public void getOne_searchesStorageService() {
    IdType id = new IdType("some id");
    Observation expected = new Observation()
        .setStatus(ObservationStatus.FINAL);
    when(storageService.findResource(id)).thenReturn(expected);

    Observation actual = observationService.getOne(id);

    assertThat(actual, is(expected));
  }

  @Test
  public void getByCaseId_searchesStorageService() {
    long caseId = 4L;
    IdType obsId = new IdType("Observation/1234");
    CaseParameter obs = new CaseParameter();
    obs.setReference(obsId.getValue());
    Observation expected = new Observation()
        .setStatus(ObservationStatus.FINAL);

    when(caseService.getCaseParameters(caseId)).thenReturn(Collections.singletonList(obs));
    when(storageService.findResource(obsId)).thenReturn(expected);

    List<Observation> results = observationService.getByCaseId(caseId);

    assertThat(results, contains(expected));
  }

  @Test
  public void getByCaseId_ignoresDeletedObservations() {
    long caseId = 4L;
    IdType obsId = new IdType("Observation/1234");
    CaseParameter obs = new CaseParameter();
    obs.setReference(obsId.getValue());
    obs.setDeleted(true);

    when(caseService.getCaseParameters(caseId)).thenReturn(Collections.singletonList(obs));

    List<Observation> results = observationService.getByCaseId(caseId);

    verifyZeroInteractions(storageService);
    assertThat(results, empty());
  }

  @Test
  public void getByCaseId_ignoresNonObservationParameters() {
    long caseId = 4L;
    IdType medId = new IdType("Medication/1234");
    CaseParameter med = new CaseParameter();
    med.setReference(medId.getValue());

    when(caseService.getCaseParameters(caseId)).thenReturn(Collections.singletonList(med));

    List<Observation> results = observationService.getByCaseId(caseId);

    verifyZeroInteractions(storageService);
    assertThat(results, empty());
  }

  @Test
  public void getByCaseId_mix_returnsNonDeletedObservationParameters() {
    long caseId = 4L;
    IdType obsId = new IdType("Observation/1234");
    CaseParameter obs = new CaseParameter();
    obs.setReference(obsId.getValue());
    CaseParameter obsDeleted = new CaseParameter();
    obsDeleted.setReference(obsId.getValue());
    obsDeleted.setDeleted(true);
    CaseParameter med = new CaseParameter();
    med.setReference("Medication/1234");

    Observation expected = new Observation()
        .setStatus(ObservationStatus.FINAL);

    when(caseService.getCaseParameters(caseId)).thenReturn(Arrays.asList(obs, obsDeleted, med));
    when(storageService.findResource(obsId)).thenReturn(expected);

    List<Observation> results = observationService.getByCaseId(caseId);

    assertThat(results, contains(expected));
  }

}