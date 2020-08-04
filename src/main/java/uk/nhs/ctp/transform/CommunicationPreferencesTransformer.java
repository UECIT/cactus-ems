package uk.nhs.ctp.transform;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner.NhsCommunicationExtension;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.CommunicationPreferences;

@Component
public class CommunicationPreferencesTransformer
  implements Transformer<CommunicationPreferences, NhsCommunicationExtension> {

  @Override
  public NhsCommunicationExtension transform(CommunicationPreferences from) {
    if (from == null) {
      return null;
    }

    var extension = new NhsCommunicationExtension();

    extension.setPreferred(new BooleanType(from.isPreferred()));
    extension.setInterpreterRequired(new BooleanType(from.isInterpreterRequired()));
    extension.setLanguage(from.getLanguage().toCodeableConcept());
    extension.setModeOfCommunication(from.getCommunicationMode().toCodeableConcept());
    extension.setCommunicationProficiency(from.getCommunicationProficiency().toCodeableConcept());

    return extension;
  }
}
