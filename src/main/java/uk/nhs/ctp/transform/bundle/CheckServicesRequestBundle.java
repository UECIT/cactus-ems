package uk.nhs.ctp.transform.bundle;

import lombok.Builder;
import lombok.Value;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;

@Value
@Builder
public class CheckServicesRequestBundle {
  Patient patient;
  ReferralRequest referralRequest;
  String requestId;
}
