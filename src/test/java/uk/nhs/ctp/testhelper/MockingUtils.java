package uk.nhs.ctp.testhelper;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.gclient.ICriterion;
import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.instance.model.api.IBaseResource;

@UtilityClass
public class MockingUtils {

  public void mockSearch(IGenericClient mock, Class<? extends IBaseResource> type, Bundle returns) {
    when((Object)mock.search()
        .forResource(eq(type))
        .where(any(ICriterion.class))
        .returnBundle(any())
        .execute()).thenReturn(returns);
  }

}
