package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Questionnaire;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.service.search.SearchParameters;
import uk.nhs.ctp.transform.CdssSupplierDTOTransformer;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@Slf4j
@RequiredArgsConstructor
public class CdssService {

  private static final Logger LOG = LoggerFactory.getLogger(CdssService.class);

  private final CdssSupplierRepository cdssSupplierRepository;
  private final FhirContext fhirContext;
  private final TokenAuthenticationService tokenAuthenticationService;
  private final CdssSupplierDTOTransformer cdssTransformer;
  private final RestTemplate restTemplate;

  /**
   * Sends request to CDSS Supplier (ServiceDefintion $evaluate).
   *
   * @param parameters Request Body {@link Parameters}
   * @return {@link GuidanceResponse}
   */
  public GuidanceResponse evaluateServiceDefinition(
      Parameters parameters,
      Long cdssSupplierId,
      String serviceDefinitionId
  ) {
    String baseUrl = getBaseUrl(cdssSupplierId);
    IGenericClient fhirClient = fhirContext.newRestfulGenericClient(baseUrl);
    return RetryUtils.retry(() -> fhirClient
            .operation()
            .onInstance(new IdType(SystemConstants.SERVICE_DEFINITION, serviceDefinitionId))
            .named(SystemConstants.EVALUATE)
            .withParameters(parameters)
            .returnResourceType(GuidanceResponse.class)
            .execute(),
        baseUrl);
  }

  /**
   * Sends request to CDSS Supplier for a ServiceDefinition.
   *
   * @return {@link ServiceDefinition}
   * @throws ca.uhn.fhir.parser.DataFormatException
   */
  public ServiceDefinition getServiceDefinition(Long cdssSupplierId, String serviceDefId) {
    String baseUrl = getBaseUrl(cdssSupplierId);
    return RetryUtils.retry(() ->
            fhirContext.newRestfulGenericClient(baseUrl)
                .read()
                .resource(ServiceDefinition.class)
                .withId(serviceDefId)
                .execute(),
        baseUrl
    );
  }

  public byte[] getImage(Long cdssSupplierId, String imageId) {
    //TODO: CDSCT-233 Flimsy: imageId will be a url for 2.0 to an unauthed server or a 'binary' fhir resource
    String url = getBaseUrl(cdssSupplierId).replace("/fhir", "/image/") + imageId;
    return RetryUtils.retry(
        () -> restTemplate.getForObject(url, byte[].class), url);
  }

  /**
   * Performs a query against the 'triage' named query for each defined CDSS
   *
   * @return
   */
  public List<CdssSupplierDTO> queryServiceDefinitions(@NotNull SearchParameters parameters) {
    return cdssSupplierRepository
        .findAllBySupplierId(tokenAuthenticationService.requireSupplierId())
        .stream() //TODO: More efficient in parallel NCTH-536
        .map(supplier -> queryServiceDefinitions(supplier, parameters))
        .filter(Objects::nonNull)
        .filter(supplier -> !CollectionUtils.isEmpty(supplier.getServiceDefinitions()))
        .collect(Collectors.toList());
  }

  public CdssSupplierDTO queryServiceDefinitions(
      @NotNull CdssSupplier supplier, @NotNull SearchParameters parameters) {

    String baseUrl = supplier.getBaseUrl();
    String url = buildSearchUrl(
        String.format("%s/ServiceDefinition", baseUrl), parameters);
    CdssSupplierDTO supplierDTO = cdssTransformer.transform(supplier);
    try {
      Bundle bundle = RetryUtils.retry(() ->
              fhirContext.newRestfulGenericClient(baseUrl).search()
                  .byUrl(url)
                  .returnBundle(Bundle.class)
                  .execute(),
          baseUrl
      );

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

  private String getBaseUrl(Long id) {
    return cdssSupplierRepository
        .getOneByIdAndSupplierId(id, tokenAuthenticationService.requireSupplierId())
        .orElseThrow(EMSException::notFound)
        .getBaseUrl();
  }

  /**
   * Sends request to CDSS Supplier (Read Questionnaire).
   *
   * @param questionnaireRef Questionnaire Reference {@link String}
   * @return {@link Questionnaire}
   */
  public Questionnaire getQuestionnaire(Long cdssSupplierId, String questionnaireRef) {
    String baseUrl = getBaseUrl(cdssSupplierId);
    return RetryUtils.retry(() ->
            fhirContext.newRestfulGenericClient(baseUrl).read()
                .resource(Questionnaire.class)
                .withId(questionnaireRef)
                .execute(),
        baseUrl);
  }

}
