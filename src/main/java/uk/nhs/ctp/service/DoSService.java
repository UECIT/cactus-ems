package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;
import uk.nhs.ctp.transform.CheckServicesRequestTransformer;
import uk.nhs.ctp.transform.CheckServicesResponseTransformer;
import uk.nhs.ctp.transform.bundle.CheckServicesRequestBundle;
import uk.nhs.ctp.transform.bundle.CheckServicesResponseBundle;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class DoSService {

	@Value("${dos.server}")
	private String dosServer;

	@Value("${ems.fhir.server}")
	private String emsServer;

	private final FhirContext fhirContext;
	private final CheckServicesRequestTransformer requestTransformer;
	private final CheckServicesResponseTransformer responseTransformer;

	public List<HealthcareServiceDTO> getDoS(String referralRequestRef, String patientRef, String requestId) {

		IGenericClient fhirClient = fhirContext.newRestfulGenericClient(emsServer);

		var referralRequest = RetryUtils.retry(() -> fhirClient.read()
				.resource(ReferralRequest.class)
				.withUrl(referralRequestRef)
				.execute(),
				emsServer);

		var patient = RetryUtils.retry(() -> fhirClient.read()
				.resource(Patient.class)
				.withUrl(patientRef)
				.execute(),
				emsServer);

		var requestBundle = CheckServicesRequestBundle.builder()
				.referralRequest(referralRequest)
				.patient(patient)
				.requestId(requestId)
				.build();

		return Stream.of(dosServer, emsServer)
				.map(dos -> new CheckServicesResponseBundle(dos, callDos(dos, requestBundle)))
				.flatMap(responseTransformer::transform)
				.collect(Collectors.toList());

	}

	private Parameters callDos(String dos, CheckServicesRequestBundle bundle) {
		try {
			return RetryUtils.retry(() -> fhirContext.newRestfulGenericClient(dos)
					.operation()
					.onServer()
					.named("$check-services")
					.withParameters(requestTransformer.transform(bundle))
					.returnResourceType(Parameters.class)
					.execute(),
					dos);
		} catch (Exception e) {
			log.warn("Error calling DOS: " + dos, e);
			return null;
		}
	}
}
