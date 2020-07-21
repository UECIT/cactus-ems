package uk.nhs.ctp.tkwvalidation.model;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpHeaders;
import uk.nhs.ctp.auditFinder.model.OperationType;
import uk.nhs.ctp.enums.CdsApiVersion;

@Value
@Builder
public class AuditMetadata {

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Headers {

    public static String SUPPLIER_ID = "Cactus-Supplier-ID";
    public static String API_VERSION = "Cactus-API-Version";
    public static String INTERACTION_TYPE = "Cactus-Interaction-Type";
    public static String INTERACTION_ID = "Cactus-Interaction-Id";
    public static String INTERACTION_DATE = "Cactus-Date";
    public static String SERVICE_ENDPOINT = "Cactus-Service-Endpoint";
  }

  // as provided during registration by MAIT
  String supplierId;

  // which version of the API is being validated (1.1 or 2.0)
  CdsApiVersion apiVersion;

  // the interaction being validated
  // e.g. $evaluate, service definition search, encounter report, $check-services
  OperationType interactionType;

  // a string identifying the specific interaction occurrence (i.e. group of related requests)
  // being validated - could be an encounter id or request id
  String interactionId;

  // date/time of interaction
  Instant interactionDate;

  // the URL of the service being audited for validation
  String serviceEndpoint;

  public HttpHeaders toHeaders() {
    var interactionDate = getInteractionDate()
        .atOffset(ZoneOffset.UTC)
        .format(DateTimeFormatter.ISO_INSTANT);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add(Headers.SUPPLIER_ID, getSupplierId());
    httpHeaders.add(Headers.API_VERSION, getApiVersion().getVersion());
    httpHeaders.add(Headers.INTERACTION_TYPE, getInteractionType().getName());
    // TODO CDSCT-400: enable the following line
//        httpHeaders.add();();(Headers.INTERACTION_ID, getInteractionId())
    httpHeaders.add(Headers.INTERACTION_DATE, interactionDate);
    httpHeaders.add(Headers.SERVICE_ENDPOINT, getServiceEndpoint());

    return httpHeaders;
  }
}
