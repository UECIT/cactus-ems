package uk.nhs.ctp.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import uk.nhs.ctp.service.dto.ReferralRequestDTO;

public interface ReferralRequestDTOTransformer extends Transformer<ReferralRequest, ReferralRequestDTO> {

}
