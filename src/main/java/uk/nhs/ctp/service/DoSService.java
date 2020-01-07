package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.Operation;
import ca.uhn.fhir.rest.annotation.OperationParam;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.dto.HealthcareService;
import uk.nhs.ctp.transform.HealthcareServiceTransformer;

@Service
public class DoSService {

	private static final Logger LOG = LoggerFactory.getLogger(DoSService.class);

	@Value("${dos.server}")
	private String dosServer;

	private FhirContext fhirContext;
	private HealthcareServiceTransformer healthcareServiceTransformer;
	private IGenericClient fhirClient;

	public DoSService(FhirContext fhirContext, HealthcareServiceTransformer healthcareServiceTransformer, IGenericClient fhirClient) {
		this.fhirContext = fhirContext;
		this.healthcareServiceTransformer = healthcareServiceTransformer;
		this.fhirClient = fhirClient;
	}

	public List<HealthcareService> getDoS(String referralRequestRef) {

		ReferralRequest referralRequest = fhirClient.read()
				.resource(ReferralRequest.class)
				.withUrl(referralRequestRef)
				.execute();

		return fhirContext.newRestfulClient(IRestfulClient.class, dosServer)
				.searchForHealthcareServices(referralRequest)
				.getEntry().stream()
				.map(entry -> (org.hl7.fhir.dstu3.model.HealthcareService) entry.getResource())
				.map(healthcareServiceTransformer::transform)
				.collect(Collectors.toList());

	}

	interface IRestfulClient extends ca.uhn.fhir.rest.client.api.IRestfulClient {
		@Operation(name = "$check-services", type = org.hl7.fhir.dstu3.model.HealthcareService.class)
		Bundle searchForHealthcareServices(
				@OperationParam(name = "referralRequest") ReferralRequest referralRequest);
	}
}
