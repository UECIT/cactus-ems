package uk.nhs.ctp.service.builder;

import org.hl7.fhir.dstu3.model.CareConnectIdentifier;
import org.hl7.fhir.dstu3.model.CareConnectOrganization;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.PatientEntity;

@Component
public class CareConnectOrganizationBuilder {

  public CareConnectOrganization build(PatientEntity patientEntity) {
    CareConnectIdentifier identifier = new CareConnectIdentifier();
    identifier.setSystem("https://fhir.nhs.uk/Id/ods-organization-code");
    identifier
        .setType(new CodeableConcept().addCoding(new Coding()
            .setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
            .setDisplay("OC")
            .setCode("OC")));

    // TODO construct an appropriate organisation from the patient record
    CareConnectOrganization organization = new CareConnectOrganization();
    organization.addIdentifier(identifier);
    organization.setName("East Road Pharmacy");

    return organization;
  }
}
