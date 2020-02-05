package uk.nhs.ctp.resourceProvider;

import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.server.IResourceProvider;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.ReferralRequestEntity;
import uk.nhs.ctp.repos.ReferralRequestRepository;
import uk.nhs.ctp.transform.ReferralRequestEntityTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@AllArgsConstructor
public class ReferralRequestProvider implements IResourceProvider {

  private ReferralRequestRepository referralRequestRepository;
  private ReferralRequestEntityTransformer referralRequestEntityTransformer;

  @Read
  public ReferralRequest get(@IdParam IdType id) {
    ReferralRequestEntity referralRequestEntity =
        referralRequestRepository.findOne(id.getIdPartAsLong());
    ErrorHandlingUtils.checkEntityExists(referralRequestEntity, "ReferralRequest");

    return referralRequestEntityTransformer.transform(referralRequestEntity);
  }

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return ReferralRequest.class;
  }
}
