package uk.nhs.ctp.service.builder;

import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.CareConnectIdentifier;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.ReferenceService;

@Component
@AllArgsConstructor
public class CareConnectOrganizationBuilder {

  private final ReferenceService referenceService;

  // TODO construct an appropriate organisation from a database record
  public CareConnectOrganization build() {
    CareConnectIdentifier identifier = new CareConnectIdentifier();
    identifier.setSystem("https://fhir.nhs.uk/Id/ods-organization-code");
    identifier
        .setType(new CodeableConcept().addCoding(new Coding()
            .setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
            .setDisplay("OC")
            .setCode("OC")));

    CareConnectOrganization organization = new CareConnectOrganization();
    organization.setId(referenceService.buildId(ResourceType.Organization, 1));
    organization.addIdentifier(identifier);
    organization.setName("East Road Pharmacy");

    return organization;
  }
}
