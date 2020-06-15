package uk.nhs.ctp.transform.two;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import java.util.Collections;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.nhs.ctp.service.dto.ConditionDTO;
import uk.nhs.ctp.service.dto.ReferralRequestDTO;
import uk.nhs.ctp.service.fhir.StorageService;
import uk.nhs.ctp.testhelper.fixtures.ConditionFixtures;
import uk.nhs.ctp.testhelper.fixtures.ReferralRequestFixtures;
import uk.nhs.ctp.transform.ConditionDTOTransformer;

@RunWith(MockitoJUnitRunner.class)
public class ReferralRequestDTOTwoTransformerTest {

  @InjectMocks
  private ReferralRequestDTOTwoTransformer transformer;

  @Mock
  private ConditionDTOTransformer conditionDTOTransformer;
  @Mock
  private StorageService storageService;

  @Test
  public void shouldTransformReferralRequest() {
    ReferralRequest referralRequest = ReferralRequestFixtures.fhirReferralRequest();
    Condition reasonRef = ConditionFixtures.fhirCondition();
    ConditionDTO expectedReason = ConditionDTO.builder()
        .onset("onset")
        .condition("reason")
        .build();
    ConditionDTO expectedSupporting = ConditionDTO.builder()
        .onset("onset")
        .condition("supporting")
        .build();
    var supportingInfo = Collections.singletonList(ConditionFixtures.fhirCondition());
    when(storageService.findResource("reason/reference", Condition.class))
        .thenReturn(reasonRef);
    when(storageService
        .findResources(Collections.singletonList("supporting/reference"), Condition.class))
        .thenReturn(supportingInfo);
    when(conditionDTOTransformer.transform(reasonRef))
        .thenReturn(expectedReason);
    when(conditionDTOTransformer.transform(getOnlyElement(supportingInfo)))
        .thenReturn(expectedSupporting);

    ReferralRequestDTO result = transformer.transform(referralRequest);

    ReferralRequestDTO expected = ReferralRequestDTO.builder()
        .resourceId("referral/id")
        .contextReference("context/reference")
        .description("Referral Description")
        .occurrence("Start: 4 Feb 2001 03:02:01 - End: 1 Jun 2005 02:03:04")
        .priority("Routine")
        .status("Active")
        .relevantHistory("history display")
        .reasonReference(expectedReason)
        .supportingInfo(Collections.singletonList(expectedSupporting))
        .build();

    assertThat(result, sameBeanAs(expected)
      .with("action", is("Next action not yet supported in Cactus EMS for CDS API v2.0")));
  }

}