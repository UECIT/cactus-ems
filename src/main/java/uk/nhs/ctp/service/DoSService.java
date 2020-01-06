package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.param.ReferenceParam;
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

	public DoSService(FhirContext fhirContext, HealthcareServiceTransformer healthcareServiceTransformer) {
		this.fhirContext = fhirContext;
		this.healthcareServiceTransformer = healthcareServiceTransformer;
	}

	public List<HealthcareService> getDoS(String id) {

		return fhirContext.newRestfulClient(IRestfulClient.class, dosServer)
				.searchForHealthcareServices(new ReferenceParam(id))
				.getEntry().stream()
				.map(entry -> (org.hl7.fhir.dstu3.model.HealthcareService) entry.getResource())
				.map(healthcareServiceTransformer::transform)
				.collect(Collectors.toList());

	}

	interface IRestfulClient extends ca.uhn.fhir.rest.client.api.IRestfulClient {
		@Search(type = org.hl7.fhir.dstu3.model.HealthcareService.class)
		Bundle searchForHealthcareServices(
				@RequiredParam(name = ReferralRequest.SP_SUBJECT) ReferenceParam referralRequestParam);
	}
}
