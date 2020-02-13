package uk.nhs.ctp.transform;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.util.StringJoiner;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.IdVersion;
import uk.nhs.ctp.entities.ReferralRequestEntity;

@Service
@AllArgsConstructor
public class ReferralRequestEntityTransformer implements
    Transformer<ReferralRequestEntity, ReferralRequest> {

  FhirContext fhirContext;

  @Override
  public ReferralRequest transform(ReferralRequestEntity referralRequestEntity) {
    IParser fhirParser = fhirContext.newJsonParser();
    ReferralRequest referralRequest = fhirParser
        .parseResource(ReferralRequest.class, referralRequestEntity.getResource());

    // Ensure IDs are set correctly
    if (referralRequestEntity.getId() != null) {
      referralRequest.setId("ReferralRequest/" + referralRequestEntity.getId());
    }

    if (referralRequestEntity.getEncounterEntity() != null) {
      IdVersion idVersion = referralRequestEntity.getEncounterEntity().getIdVersion();
      String refString = new StringJoiner("/")
          .add("Encounter")
          .add(idVersion.getId().toString())
          .add("_history")
          .add(idVersion.getVersion().toString())
          .toString();

      Reference caseRef = new Reference(refString);
      referralRequest.setContext(caseRef);
    }

    return referralRequest;
  }
}
