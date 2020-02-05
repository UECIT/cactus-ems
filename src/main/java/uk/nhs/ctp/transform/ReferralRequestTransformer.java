package uk.nhs.ctp.transform;

import ca.uhn.fhir.parser.IParser;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.ReferralRequestEntity;

@Service
@AllArgsConstructor
public class ReferralRequestTransformer implements
    Transformer<ReferralRequest, ReferralRequestEntity> {

  IParser fhirParser;

  @Override
  public ReferralRequestEntity transform(ReferralRequest referralRequest) {
    ReferralRequestEntity referralRequestEntity = new ReferralRequestEntity();
    referralRequestEntity.setResource(fhirParser.encodeResourceToString(referralRequest));
    return referralRequestEntity;
  }
}
