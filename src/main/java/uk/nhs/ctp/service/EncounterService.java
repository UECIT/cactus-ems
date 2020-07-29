package uk.nhs.ctp.service;


import ca.uhn.fhir.context.FhirContext;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.CaseObservation;
import uk.nhs.ctp.entities.Cases;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.dto.EncounterHandoverDTO;
import uk.nhs.ctp.service.dto.EncounterReportInput;
import uk.nhs.ctp.transform.EncounterReportInputTransformer;
import uk.nhs.ctp.transform.EncounterTransformer;
import uk.nhs.ctp.transform.ObservationTransformer;
import uk.nhs.ctp.utils.ResourceProviderUtils;
import uk.nhs.ctp.utils.RetryUtils;

@Service
@AllArgsConstructor
@Slf4j
public class EncounterService {

  private final EncounterTransformer encounterTransformer;
  private final ObservationTransformer observationTransformer;
  private final CaseRepository caseRepository;
  private final EncounterReportInputTransformer encounterReportInputTransformer;
  private final EmsSupplierService emsSupplierService;
  private final FhirContext fhirContext;
  private final TokenAuthenticationService authService;

  public Encounter getEncounter(Long caseId) {
    Cases triageCase = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);
    Encounter encounter = encounterTransformer.transform(triageCase);
    encounter.setId(caseId.toString());
    return encounter;
  }

  @Transactional
  public List<Observation> getObservationsForEncounter(Long caseId) {
    List<CaseObservation> observations = caseRepository
        .getOneByIdAndSupplierId(caseId, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound)
        .getObservations();
    return observations.stream()
        .map(observationTransformer::transform)
        .collect(Collectors.toList());
  }

  public EncounterReportInput getEncounterReport(IdType encounterId) {
    String baseUrl = encounterId.getBaseUrl();
    Bundle encounterReportBundle = RetryUtils
        .retry(() -> fhirContext.newRestfulGenericClient(baseUrl)
                .search()
                .forResource(Encounter.class)
                .where(Encounter.RES_ID.exactly().identifier(encounterId.getIdPart()))
                .include(Encounter.INCLUDE_ALL.asRecursive())
                .revInclude(Encounter.INCLUDE_ALL.asRecursive())
                .returnBundle(Bundle.class)
                .execute(),
            baseUrl);

    Encounter encounter = ResourceProviderUtils.getResource(encounterReportBundle, Encounter.class);
    Patient patient = ResourceProviderUtils.getResource(encounterReportBundle, Patient.class);
    List<Observation> observations = ResourceProviderUtils
        .getResources(encounterReportBundle, Observation.class);
    return EncounterReportInput.builder()
        .encounter(encounter)
        .patient(patient)
        .observations(observations)
        .build();
  }

  public EncounterHandoverDTO getEncounterReportHandover(IdType encounterId) {
    EncounterReportInput input = getEncounterReport(encounterId);
    return encounterReportInputTransformer.transform(input);
  }

  public List<Encounter> getByPatientIdentifier(String system, String value) {
    return caseRepository.findAllBySupplierId(authService.requireSupplierId())
        .stream()
        .filter(caseEntity -> {
          if (caseEntity.getPatientId() == null) {
            return false;
          }
          IdType id = new IdType(caseEntity.getPatientId());

          //TODO: This seems inefficient, have to get the patient for each case!?
          try {
            String baseUrl = id.getBaseUrl();
            Patient patient = RetryUtils.retry(() -> fhirContext.newRestfulGenericClient(baseUrl)
                    .read().resource(Patient.class)
                    .withId(id)
                    .execute(),
                baseUrl);

            return patient.getIdentifier().stream()
                .anyMatch(identifier -> system.equals(identifier.getSystem())
                    && value.equals(identifier.getValue()));
          } catch (Exception e) {
            log.error("Unable to find patient {} for encounter {}: {}", id.getValue(),
                caseEntity.getId(), e.getMessage());
            return false;
          }

        })
        .map(encounterTransformer::transform)
        .collect(Collectors.toUnmodifiableList());
  }

  public List<String> searchEncounterIdsByPatientNhsNumber(String nhsNumber) {
    List<EmsSupplier> suppliers = emsSupplierService.getAll();

    return suppliers.stream()
        .map(supplier -> RetryUtils
            .retry(() -> fhirContext.newRestfulGenericClient(supplier.getBaseUrl())
                    .search()
                    .forResource(Encounter.class)
                    .where(Encounter.PATIENT
                        .hasChainedProperty(
                            Patient.IDENTIFIER.exactly()
                                .systemAndIdentifier(SystemURL.NHS_NUMBER, nhsNumber)))
                    .returnBundle(Bundle.class)
                    .execute(),
                supplier.getBaseUrl())
            .getEntry().stream()
            .map(BundleEntryComponent::getFullUrl)
            .collect(Collectors.toUnmodifiableList()))
        .flatMap(List::stream)
        .collect(Collectors.toUnmodifiableList());
  }

}
