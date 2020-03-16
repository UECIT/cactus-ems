package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.google.common.base.Preconditions;
import java.util.function.Consumer;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.repos.ReferralRequestRepository;
import uk.nhs.ctp.service.fhir.ReferenceService;
import uk.nhs.ctp.transform.ReferralRequestEntityTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
public class ReferralRequestService {

  private ReferralRequestEntityTransformer referralRequestEntityTransformer;
  private ReferralRequestRepository referralRequestRepository;
  private FhirContext fhirContext;
  private ReferenceService referenceService;

  public ReferralRequest makeAbsolute(ReferralRequest referralRequest) {
    IdType idElement = referralRequest.getIdElement();
    Preconditions.checkArgument(idElement.isAbsolute(), "Referral request must have absolute ID");
    String baseUrl = idElement.getBaseUrl();

    ReferralRequest copy = referralRequest.copy();
    copy.setIdElement(null);
    referenceService.resolveRelative(baseUrl, copy);

    return copy;
  }

  /**
   * Applies an update to the embedded ReferralRequest resource by deserializing it, applying the
   * update function and then re-serializing it.
   */
  public void update(ReferralRequestEntity entity, Consumer<ReferralRequest> updateFn) {
    ReferralRequest referralRequest = referralRequestEntityTransformer.transform(entity);
    updateFn.accept(referralRequest);
    IParser fhirParser = fhirContext.newJsonParser();
    entity.setResource(fhirParser.encodeResourceToString(referralRequest));
  }

  @Transactional
  public ReferralRequest get(Long id) {
    ReferralRequestEntity referralRequestEntity =
        referralRequestRepository.findOne(id);
    ErrorHandlingUtils.checkEntityExists(referralRequestEntity, "ReferralRequest");

    return referralRequestEntityTransformer.transform(referralRequestEntity);
  }
}
