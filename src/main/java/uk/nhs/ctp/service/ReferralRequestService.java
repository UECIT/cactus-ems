package uk.nhs.ctp.service;

import ca.uhn.fhir.parser.IParser;
import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.transform.ReferralRequestEntityTransformer;

@Service
@AllArgsConstructor
public class ReferralRequestService {

  private ReferralRequestEntityTransformer referralRequestEntityTransformer;
  private IParser fhirParser;

  /**
   * Applies an update to the embedded ReferralRequest resource by deserializing it, applying the
   * update function and then re-serializing it.
   *
   * @param entity
   * @param updateFn
   */
  public void update(ReferralRequestEntity entity, Consumer<ReferralRequest> updateFn) {
    ReferralRequest referralRequest = referralRequestEntityTransformer.transform(entity);
    updateFn.accept(referralRequest);
    entity.setResource(fhirParser.encodeResourceToString(referralRequest));
  }
}
