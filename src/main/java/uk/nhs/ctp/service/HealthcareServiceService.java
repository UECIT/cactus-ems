package uk.nhs.ctp.service;

import com.google.common.base.Preconditions;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.hl7.fhir.dstu3.model.HealthcareService;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.service.dto.HealthcareServiceDTO;
import uk.nhs.ctp.transform.HealthcareServiceOutTransformer;
import uk.nhs.ctp.utils.ErrorHandlingUtils;

@Service
@RequiredArgsConstructor
public class HealthcareServiceService {

  private final Registry<HealthcareServiceDTO> healthcareServiceRegistry;
  private final HealthcareServiceOutTransformer healthcareServiceTransformer;

  @Value("${ems.frontend}")
  private String emsUi;

  public HealthcareService get(IdType id) {
    String encounterId = new String(Base64.decodeBase64(id.getIdPart()), StandardCharsets.UTF_8);

    return healthcareServiceRegistry.getAll().stream()
        .findFirst()
        .map(hs -> setEndpointAndId(hs, encounterId))
        .map(healthcareServiceTransformer::transform)
        .orElseThrow(() -> new EMSException(HttpStatus.NOT_FOUND, id + " not found"));
  }

  public List<HealthcareService> getAll(Reference context) {

    ErrorHandlingUtils.checkEntityExists(context, "ReferralRequest Encounter reference");

    IdType encounterId = new IdType(context.getReference());
    Preconditions.checkArgument(encounterId.isAbsolute(), "Encounter reference must be absolute");

    return healthcareServiceRegistry.getAll().stream()
        .map(hs -> setEndpointAndId(hs, encounterId.getValue()))
        .map(healthcareServiceTransformer::transform)
        .collect(Collectors.toList());
  }

  private HealthcareServiceDTO setEndpointAndId(HealthcareServiceDTO healthcareService, String encounterId) {
    String encodedEncounterRefBase64 = Base64.encodeBase64URLSafeString(encounterId.getBytes(StandardCharsets.UTF_8));
    String encodedEncounterRef = URLEncoder.encode(encounterId, StandardCharsets.UTF_8);
    String endpoint = emsUi
        + "/#/main?encounterId="
        + encodedEncounterRef;
    return healthcareService.toBuilder()
        .id(encodedEncounterRefBase64)
        .endpoint(endpoint)
        .build();
  }
}
