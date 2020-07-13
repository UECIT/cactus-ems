package uk.nhs.ctp.service;

import ca.uhn.fhir.context.FhirContext;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Identifier;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.hl7.fhir.dstu3.model.ServiceDefinition;
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
  private final FhirContext fhirContext;

  public void checkValidity(String patientId) {
    Patient patient = resourceLocator.findResource(patientId);

    registeredGp(patient).ifPresentOrElse(
        gp -> invokeValidity(gp, patient),
        () -> log.warn("Patient {} has no registered gp. Skipping $isValid call", patientId)
    );
  }

  private void invokeValidity(Organization gp, Patient patient) {
    Optional<Identifier> odsCode = odsIdentifier(gp);
    if (odsCode.isEmpty()) {
      log.warn("GP {} for patient {} has no ODS code", gp.getId(), patient.getId());
      return;
    }
    cdssSupplierRepository.findAllBySupplierId(authService.requireSupplierId()).stream()
        .parallel()
        .forEach(supplier -> {
          BooleanType isValidResponse =
              (BooleanType)fhirContext.newRestfulGenericClient(supplier.getBaseUrl())
                  .operation()
                  .onType(ServiceDefinition.class)
                  .named("$isValid")
                  .withParameter(Parameters.class,
                      "requestId", new IdType(UUID.randomUUID().toString()))
                  .andParameter("ODSCode", odsCode.get())
                  .andParameter("evaluateAtDateTime", new DateTimeType(new Date()))
                  .andParameter("dateOfBirth", new DateTimeType(patient.getBirthDate()))
                  .execute()
                  .getParameterFirstRep()
                  .getValue();
          log.info("Supplier {} returned {} from $isValid check", supplier.getBaseUrl(), isValidResponse.booleanValue());
        });
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
