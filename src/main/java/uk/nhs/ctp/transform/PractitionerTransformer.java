package uk.nhs.ctp.transform;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.CareConnectPractitioner;
import org.hl7.fhir.dstu3.model.Practitioner.PractitionerQualificationComponent;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.model.Practitioner;
import uk.nhs.ctp.service.fhir.ReferenceService;

@Component
@RequiredArgsConstructor
public class PractitionerTransformer
    implements Transformer<Practitioner, CareConnectPractitioner> {

  private final ReferenceService referenceService;
  private final HumanNameTransformer nameTransformer;
  private final IdentifierTransformer identifierTransformer;
  private final AddressTransformer addressTransformer;
  private final CommunicationPreferencesTransformer communicationTransformer;
  private final ContactPointTransformer contactPointTransformer;

  @Override
  public CareConnectPractitioner transform(Practitioner from) {
    var practitioner = new CareConnectPractitioner();

    practitioner.setId(referenceService.buildId(ResourceType.Practitioner, from.getId()));
    practitioner.addName(nameTransformer.transform(from.getName()));
    // calling "practitioner::addIdentifier" specifically as it's the only one
    // implemented by the CareConnectPractitioner profile for now
    identifierTransformer.transform(from.getIdentifiers()).forEach(practitioner::addIdentifier);
    practitioner.addAddress(addressTransformer.transform(from.getAddress()));
    practitioner.setTelecom(contactPointTransformer.transform(from.getContact()));
    practitioner.setNhsCommunication(
        communicationTransformer.transform(from.getCommunicationPreferences()));

    if (from.getQualification() != null) {
      practitioner.addQualification(
          new PractitionerQualificationComponent(from.getQualification().toCodeableConcept()));
    }

    return practitioner;
  }
}
