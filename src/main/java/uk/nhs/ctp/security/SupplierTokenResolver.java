package uk.nhs.ctp.security;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import uk.nhs.cactus.common.security.CactusToken;

@Component
@RequiredArgsConstructor
public class SupplierTokenResolver {

  @Value("${fhir.server}")
  private String fhirServer;

  @Value("${blob.server}")
  private String blobServer;

  @Value("${ems.fhir.server}")
  private String emsFhirServer;

  @Value("${cactus.cdss}")
  private String cactusCdss;

  @Value("${dos.server}")
  private String dosServer;

  public Optional<String> resolve(String requestUrl) {
    final List<String> cactusServices =
        Arrays.asList(fhirServer, blobServer, emsFhirServer, cactusCdss, dosServer);

    boolean isCactus = cactusServices.stream()
        .anyMatch(requestUrl::startsWith);

    if (isCactus) {
      return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
          .map(auth -> (CactusToken) auth.getCredentials())
          .map(CactusToken::getToken);
    }

    return Optional.empty();
  }

}
