package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.validation.constraints.NotNull;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.enums.AuditEntryType;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.service.search.SearchParameters;

@Service
public class CdssService {

  private static final Logger LOG = LoggerFactory.getLogger(CdssService.class);

  private final CdssSupplierRepository cdssSupplierRepository;
  private final AuditService auditService;
  private final FhirContext fhirContext;

  private HttpHeaders headers;
  private RestTemplate restTemplate;

  public CdssService(
      CdssSupplierRepository cdssSupplierRepository,
      AuditService auditService,
      RestTemplate restTemplate,
      FhirContext fhirContext) {
    this.cdssSupplierRepository = cdssSupplierRepository;
    this.auditService = auditService;
    this.restTemplate = restTemplate;
    this.fhirContext = fhirContext;

    headers = new HttpHeaders();
    headers.setContentType(MediaType.valueOf(SystemConstants.APPLICATION_FHIR_JSON));
    headers.add("Authorization", SystemConstants.AUTH_TOKEN);
  }

  /**
   * Sends request to CDSS Supplier (ServiceDefintion $evaluate).
   *
   * @param parameters Request Body {@link Parameters}
   * @return {@link GuidanceResponse}
   * @throws JsonProcessingException
   */
  public GuidanceResponse evaluateServiceDefinition(
      Parameters parameters,
      Long cdssSupplierId,
      String serviceDefinitionId,
      Long caseId,
      ReferencingContext referencingContext) throws JsonProcessingException {

    IParser fhirParser = fhirContext.newJsonParser();
    String requestBody = fhirParser.encodeResourceToString(parameters);

    GuidanceResponse response = fhirContext.newRestfulGenericClient(getBaseUrl(cdssSupplierId))
        .operation()
        .onInstance(new IdType(SystemConstants.SERVICE_DEFINITION, serviceDefinitionId))
        .named(SystemConstants.EVALUATE)
        .withParameters(parameters)
        .returnResourceType(GuidanceResponse.class)
        .execute();

    var responseBody = fhirParser.encodeResourceToString(response);
    auditService.createAuditEntry(caseId, requestBody, responseBody, AuditEntryType.RESULT);
    return response;
  }

  /**
   * Sends request to CDSS Supplier for a ServiceDefinition.
   *
   * @return {@link ServiceDefinition}
   * @throws ca.uhn.fhir.parser.DataFormatException
   */
  public ServiceDefinition getServiceDefinition(String serviceDefId, String cdssSupplierId) {
    String url = getBaseUrl(Long.valueOf(cdssSupplierId)) + "/"
        + SystemConstants.SERVICE_DEFINITION + "/" + serviceDefId + "/";
    String responseBody = sendHttpRequest(url, HttpMethod.GET, new HttpEntity<>(headers));

    IParser fhirParser = fhirContext.newJsonParser();
    return (ServiceDefinition) fhirParser.parseResource(responseBody);
  }

  /**
   * Performs a query against the 'triage' named query for each defined CDSS
   *
   * @return
   */
  public List<CdssSupplierDTO> queryServiceDefinitions(@NotNull SearchParameters parameters) {
    return cdssSupplierRepository.findAll().parallelStream()
        .map(supplier -> queryServiceDefinitions(supplier, parameters))
        .filter(Objects::nonNull)
        .filter(supplier -> !CollectionUtils.isEmpty(supplier.getServiceDefinitions()))
        .collect(Collectors.toList());
  }

  public CdssSupplierDTO queryServiceDefinitions(
      @NotNull CdssSupplier supplier, @NotNull SearchParameters parameters) {

    String url = buildSearchUrl(
        String.format("%s/ServiceDefinition", supplier.getBaseUrl()), parameters);
    CdssSupplierDTO supplierDTO = new CdssSupplierDTO(supplier);
    try {
      String responseBody = sendHttpRequest(url, HttpMethod.GET,
          new HttpEntity<>(headers));
      IParser fhirParser = fhirContext.newJsonParser();
      Bundle bundle = (Bundle) fhirParser.parseResource(responseBody);

      List<ServiceDefinitionDTO> serviceDefinitions = bundle.getEntry().stream()
          .map(entry -> (ServiceDefinition) entry.getResource())
          .map(ServiceDefinitionDTO::new)
          .collect(Collectors.toList());
      supplierDTO.setServiceDefinitions(serviceDefinitions);

    } catch (Exception e) {
      LOG.error("Unable to fetch service definitions from {}: {}. "
          + "Falling back to local definitions", supplier.getName(), collectMessages(e));

      // Triage search not allowed if search is not supported
      if ("triage".equals(parameters.getQuery())) {
        supplierDTO.setServiceDefinitions(Collections.emptyList());
      }
    }
    return supplierDTO;
  }

  private String buildSearchUrl(String path, SearchParameters parameters) {
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(path);
    uriBuilder.queryParams(parameters.toMultiValueMap());
    return uriBuilder.build().toUriString();
  }

  private String collectMessages(Throwable e) {
    StringBuilder sb = new StringBuilder();
    HashSet<Throwable> seen = new HashSet<>();
    while (e != null && !seen.contains(e)) {
      if (sb.length() > 0) {
        sb.append(": ");
      }
      sb.append(e.getMessage());
      seen.add(e);
      e = e.getCause();
    }
    return sb.toString();
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

    String responseBody = sendHttpRequest(getBaseUrl(cdssSupplierId) + questionnaireRef,
        HttpMethod.GET,
        new HttpEntity<>(headers));

    auditService
        .updateAuditEntry(caseId, getBaseUrl(cdssSupplierId) + questionnaireRef, responseBody);

    IParser fhirParser = fhirContext.newJsonParser();
    return fhirParser.parseResource(Questionnaire.class, responseBody);
  }

  private String sendHttpRequest(String url, HttpMethod httpMethod, HttpEntity<String> request) {

    HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

      public boolean verify(String hostname, SSLSession sslSession) {
        return hostname.equals("localhost");
      }
    });

    LOG.info("Sent a " + httpMethod + " request to " + url);
    try {
      ResponseEntity<String> response = restTemplate
          .exchange(url, httpMethod, request, String.class);

      if (response.getStatusCode() == HttpStatus.OK) {
        return response.getBody();
      } else {
        throw new EMSException(response.getStatusCode(), response.getBody());
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Error communicating with CDSS", e);
    }
  }
}
