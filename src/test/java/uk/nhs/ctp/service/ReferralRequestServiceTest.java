package uk.nhs.ctp.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.CustomTypeSafeMatcher;
import org.hamcrest.Matcher;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.CodeDTO;
import uk.nhs.ctp.service.dto.SelectedServiceRequestDTO;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.testhelper.MockingUtils;

@RunWith(MockitoJUnitRunner.class)
public class ReferralRequestServiceTest {

  @InjectMocks
  private ReferralRequestService referralRequestService;

  @Mock
  private AppointmentService appointmentService;

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
    ReferralRequest expected = new ReferralRequest()
        .setContext(new Reference("123"))
        .setDescription("desc");
    Bundle returns = new Bundle()
        .addEntry(new BundleEntryComponent()
            .setResource(expected));
    MockingUtils.mockSearch(mockClient, ReferralRequest.class, returns);

    List<ReferralRequest> results = referralRequestService.getByCaseId(3L);

    verify(referenceService).buildId(ResourceType.Encounter, 3L);
    assertThat(results, contains(expected));
  }

  @Test
  public void shouldReturnEmptyListNoCarePlansFound() {
    MockingUtils.mockSearch(mockClient, ReferralRequest.class, new Bundle());

    List<ReferralRequest> results = referralRequestService.getByCaseId(3L);

    verify(referenceService).buildId(ResourceType.Encounter, 3L);
    assertThat(results, empty());
  }

  @Test
  public void shouldUpdateServiceRequested() {
    SelectedServiceRequestDTO serviceRequestDTO = new SelectedServiceRequestDTO();
    CodeDTO serviceType1 = new CodeDTO("code", "display", "system");
    CodeDTO serviceType2 = new CodeDTO("code2", "display2", "system2");
    serviceRequestDTO.setCaseId(4L);
    serviceRequestDTO.setServiceTypes(Arrays.asList(serviceType1, serviceType2));
    serviceRequestDTO.setSelectedServiceId("Service/123");
    Bundle returns = new Bundle()
        .addEntry(new BundleEntryComponent().setResource(new ReferralRequest()));
    MockingUtils.mockSearch(mockClient, ReferralRequest.class, returns);

    referralRequestService.updateServiceRequested(serviceRequestDTO);

    ArgumentCaptor<ReferralRequest> captured = ArgumentCaptor.forClass(ReferralRequest.class);
    verify(appointmentService).create(captured.capture());
    assertThat(captured.getValue().getServiceRequested(),
        contains(isCodeDto(serviceType1), isCodeDto(serviceType2)));
    verify(storageService).updateExternal(captured.getValue());
  }

  private static Matcher<CodeableConcept> isCodeDto(CodeDTO codeDTO) {
    return new CustomTypeSafeMatcher<>(codeDTO.toString() + " as a codeable concept") {
      @Override
      protected boolean matchesSafely(CodeableConcept codeableConcept) {
        Coding coding = codeableConcept.getCodingFirstRep();
        return coding.getCode().equals(codeDTO.getCode())
            && coding.getSystem().equals(codeDTO.getSystem())
            && coding.getDisplay().equals(codeDTO.getDisplay());
      }
    };
  }

}