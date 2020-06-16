package uk.nhs.ctp.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.security.CactusToken;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.EmsSupplierService;

@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierTokenResolver {

  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${blob.server}")
  private String blobServer;

  @Value("${ems.fhir.server}")
  private String emsFhirServer;

  @Value("${dos.server}")
  private String dosServer;

  private final CdssSupplierService cdssSupplierService;
  private final EmsSupplierService emsSupplierService;

  /**
   * Returns an auth token based on the URL for the request.
   * This method looks up tokens with the following precedence:
   * 1. If the request URL is for a CACTUS service, a cactus token will be returned.
   * 2. If the request URL is for a registered CDSS or EMS, the token for that service will be returned.
   * 3. An empty optional.
   *
   * @param requestUrl the service being requested
   * @return the token for that service.
   */
  public Optional<String> resolve(String requestUrl) {
    final List<String> cactusServices =
        Arrays.asList(fhirServer, blobServer, emsFhirServer, dosServer);

    log.info("Resolve token for {}", requestUrl);

    if (cactusServices.stream().anyMatch(requestUrl::startsWith)) {
      log.info("Using Cactus token");
      return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(auth -> (CactusToken) auth.getCredentials())
          .map(CactusToken::getToken);
    }

    log.info("Looking up token in supplier records");
    Supplier<Optional<String>> emsProviderAuthTokenSupplier = () -> emsSupplierService
        .findEmsSupplierByBaseUrl(requestUrl)
        .map(EmsSupplier::getAuthToken);
    return cdssSupplierService.findCdssSupplierByBaseUrl(requestUrl)
        .map(CdssSupplier::getAuthToken)
        .or(emsProviderAuthTokenSupplier);

  }

}
