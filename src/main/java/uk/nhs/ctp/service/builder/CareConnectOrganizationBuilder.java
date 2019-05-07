package uk.nhs.ctp.service.builder;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.stereotype.Component;

import datatypes.Identifier;
import resources.CareConnectOrganization;
import uk.nhs.ctp.entities.Cases;

@Component
public class CareConnectOrganizationBuilder {

	public CareConnectOrganization build(Cases caseEntity) {
		Identifier identifier = new Identifier();
		identifier.setSystem("https://fhir.nhs.uk/Id/ods-organization-code");
		identifier
			.setType(new CodeableConcept().addCoding(new Coding()
			.setSystem("https://fhir.nhs.uk/Id/ods-organization-code")
			.setDisplay("OC")
			.setCode("OC")));
		
		CareConnectOrganization organization = new CareConnectOrganization();
		organization.addIdentifier(identifier);
		organization.setName("East Road Pharmacy");
		
		return organization;
	}
}
