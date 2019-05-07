package uk.nhs.ctp.service;

import java.net.ConnectException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;

@Service
public class CdssService {
	private static final Logger LOG = LoggerFactory.getLogger(CdssService.class);

	@Value("${ems.request.bundle:false}")
	private boolean sendRequestAsBundle;
	
	@Autowired
	private CdssSupplierRepository cdssSupplierRepository;

	@Autowired
	private AuditService auditService;
	
	@Autowired
	private IParser fhirParser;

	private HttpHeaders headers;
	private RestTemplate restTemplate;

	public CdssService() {
		headers = new HttpHeaders();
		headers.setContentType(MediaType.valueOf(SystemConstants.APPLICATION_FHIR_JSON));
		headers.add("Authorization", SystemConstants.AUTH_TOKEN);
		restTemplate = new RestTemplate();
	}

	/**
	 * Sends request to CDSS Supplier (ServiceDefintion $evaluate).
	 * 
	 * @param parameters Request Body {@link Parameters}
	 * @return {@link GuidanceResponse}
	 * @throws JsonProcessingException
	 */
	public Resource evaluateServiceDefinition(Parameters parameters, Long cdssSupplierId,
			String serviceDefinitionId, Long caseId) throws ConnectException, JsonProcessingException {
			
		String requestBody = fhirParser.encodeResourceToString(sendRequestAsBundle ? 
				new Bundle().addEntry(new BundleEntryComponent().setResource(parameters)) : parameters);

		String responseBody = sendHttpRequest(getBaseUrl(cdssSupplierId) + "/" + SystemConstants.SERVICE_DEFINITION
				+ "/" + serviceDefinitionId + "/" + SystemConstants.EVALUATE, HttpMethod.POST,
				new HttpEntity<>(requestBody, headers));

		auditService.createAuditEntry(caseId, requestBody, responseBody);

		return (Resource) FhirContext.forDstu3().newJsonParser().parseResource(responseBody);
	}

	/**
	 * Sends request to CDSS Supplier for a ServiceDefinition.
	 * 
	 * @param switchDataRequirements Request Body {@link Parameters}
	 * @return {@link ServiceDefinition}
	 * @throws JsonProcessingException
	 */
	public ServiceDefinition getServiceDefinition(String serviceDefId, String cdssSupplierId) {
		String responseBody = sendHttpRequest(getBaseUrl(Long.valueOf(cdssSupplierId)) + "/"
				+ SystemConstants.SERVICE_DEFINITION + "/" + serviceDefId + "/", HttpMethod.GET,
				new HttpEntity<>(headers));

		return (ServiceDefinition) FhirContext.forDstu3().newJsonParser().parseResource(responseBody);
	}

	private String getBaseUrl(Long cdssSupplierId) {
		return cdssSupplierRepository.findOne(cdssSupplierId).getBaseUrl();
	}

	/**
	 * Sends request to CDSS Supplier (Read Questionnaire).
	 * 
	 * @param questionnaireRef Questionnaire Reference {@link String}
	 * @return {@link Questionnaire}
	 * @throws JsonProcessingException
	 */
	public Questionnaire getQuestionnaire(Long cdssSupplierId, String questionnaireRef, Long caseId)
			throws ConnectException, JsonProcessingException {

		String responseBody = sendHttpRequest(getBaseUrl(cdssSupplierId) + questionnaireRef, HttpMethod.GET,
				new HttpEntity<>(headers));

		auditService.updateAuditEntry(caseId, getBaseUrl(cdssSupplierId) + questionnaireRef, responseBody);

		return (Questionnaire) FhirContext.forDstu3().newJsonParser().parseResource(responseBody);
	}

	private String sendHttpRequest(String url, HttpMethod httpMethod, HttpEntity<String> request) {

		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			public boolean verify(String hostname, SSLSession sslSession) {
				return hostname.equals("localhost");
			}
		});

		LOG.info("Sent a " + httpMethod + " request to " + url);
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, httpMethod, request, String.class);

			if (response.getStatusCode() == HttpStatus.OK) {
				return response.getBody();
			} else {
				throw new EMSException(response.getStatusCode(), response.getBody());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to connect to CDSS Supplier", e);
		}
	}
}
