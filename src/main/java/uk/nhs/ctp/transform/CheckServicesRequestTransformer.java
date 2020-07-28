package uk.nhs.ctp.transform;

import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.CareConnectLocation;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Encounter.EncounterParticipantComponent;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.builder.ParametersBuilder;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.transform.bundle.CheckServicesRequestBundle;

@Component
@RequiredArgsConstructor
public class CheckServicesRequestTransformer implements
    Transformer<CheckServicesRequestBundle, Parameters> {

  private final GenericResourceLocator resourceLocator;

  @Override
  public Parameters transform(CheckServicesRequestBundle bundle) {
    var patient = bundle.getPatient();
    var referralRequest = bundle.getReferralRequest();
    var location = new CareConnectLocation().setAddress(new Address().setPostalCode("IM8 2TG"));

    Encounter encounter = resourceLocator.findResource(
        referralRequest.getContext(),
        referralRequest.getIdElement());

    Resource requester = encounter.getParticipant()
        .stream()
        .map(EncounterParticipantComponent::getIndividual)
        .findFirst()
        .map(i -> resourceLocator.<Resource>findResource(i, encounter.getIdElement()))
        .orElse(patient);

    Organization registeredGP = patient.getGeneralPractitioner()
        .stream()
        .filter(ref -> new IdType(ref.getReference()).getResourceType().contains("Organization"))
        .findFirst()
        .map(ref -> resourceLocator.<Organization>findResource(ref, patient.getIdElement()))
        .orElse(null);

    var inputParameters = new ParametersBuilder()
        .add("forceSearchDistance", new BooleanType(true))
        .build();

    return new ParametersBuilder()
        .add("patient", patient)
        .add("referralRequest", referralRequest)
        .add("requestId", new IdType(bundle.getRequestId()))
        .add("location", location)
        .add("requester", requester)
        .add("searchDistance", new IntegerType(46))
        .add("registeredGP", registeredGP)
        .add("inputParameters", inputParameters)
        .build();
  }
}
