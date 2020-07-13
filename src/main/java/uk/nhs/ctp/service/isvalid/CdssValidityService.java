package uk.nhs.ctp.service.isvalid;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.enums.IdentifierType;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.service.fhir.GenericResourceLocator;
import uk.nhs.ctp.utils.ResourceUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CdssValidityService {

  private final GenericResourceLocator resourceLocator;
  private final CdssSupplierRepository cdssSupplierRepository;
  private final TokenAuthenticationService authService;
  private final IsValidOperationService isValidOperationService;

  public Map<String, Boolean> checkValidity(String patientId) {
    Patient patient = resourceLocator.findResource(patientId);

    return registeredGp(patient)
        .map(gp -> invokeValidity(gp, patient))
        .orElse(Collections.emptyMap());
  }

  private Map<String, Boolean> invokeValidity(Organization gp, Patient patient) {
    Optional<Identifier> odsCode = odsIdentifier(gp);
    if (odsCode.isEmpty()) {
      log.warn("GP {} for patient {} has no ODS code", gp.getId(), patient.getId());
      return Collections.emptyMap();
    }
    var results = new HashMap<String, Boolean>();
    cdssSupplierRepository.findAllBySupplierId(authService.requireSupplierId()).stream()
        .parallel()
        .forEach(supplier -> {
          Boolean result = isValidOperationService.invokeIsValid(supplier, odsCode.get(), patient);
          results.put(supplier.getBaseUrl(), result);
        });
    return results;
  }

  private Optional<Identifier> odsIdentifier(Organization gp) {
    return gp.getIdentifier().stream()
        .filter(identifier -> identifier.getSystem().equals(IdentifierType.OC.getSystem()))
        .findFirst();
  }

  private Optional<Organization> registeredGp(Patient patient) {
    return patient.getGeneralPractitioner().stream()
        .filter(ResourceUtils.referenceTo(ResourceType.Organization))
        .map(ref -> (Organization) resourceLocator.findResource(ref, patient.getIdElement()))
        .findFirst();
  }

}
