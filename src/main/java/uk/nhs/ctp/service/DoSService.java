package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;
import uk.nhs.ctp.transform.HealthcareServiceInTransformer;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoSService {

	@Value("${dos.server}")
	private String dosServer;

	@Value("${ems.server}")
	private String emsServer;

	private final FhirContext fhirContext;
	private final HealthcareServiceInTransformer healthcareServiceInTransformer;
	private final IGenericClient fhirClient;

	public List<HealthcareServiceDTO> getDoS(String referralRequestRef) {

		ReferralRequest referralRequest = fhirClient.read()
				.resource(ReferralRequest.class)
				.withUrl(referralRequestRef)
				.execute();

		return Stream.of(dosServer, emsServer + "/fhir")
				.flatMap(dos -> callDos(dos, referralRequest))
				.collect(Collectors.toList());

	}

	private Stream<HealthcareServiceDTO> callDos(String dos, ReferralRequest referralRequest) {
		try {
			return fhirContext.newRestfulGenericClient(dos)
				.operation()
				.onType(HealthcareService.class)
				.named("$check-services")
				.withParameter(Parameters.class, "referralRequest", referralRequest)
				.returnResourceType(Bundle.class)
				.execute()
				.getEntry()
				.stream()
				.map(entry -> {
					HealthcareService resource = (HealthcareService) entry.getResource();

					// Establish full URL of resource
					if (entry.hasFullUrl()) {
						resource.setId(entry.getFullUrl());
					} else if (resource.hasId()) {
						IdType fullId = resource.getIdElement()
								.withServerBase(dos, resource.getResourceType().name());
						resource.setId(fullId);
					}

					return resource;
				})
				.map(healthcareServiceInTransformer::transform);
		} catch (Exception e) {
			log.warn("Error calling DOS: " + dos, e);
			return Stream.empty();
		}

	}
}
