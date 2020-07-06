package uk.nhs.ctp.controllers;

import ca.uhn.fhir.context.FhirContext;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.NewCdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.service.search.SearchParameters;

@CrossOrigin
@RestController
@RequestMapping(path = "/cdss")
@RequiredArgsConstructor
public class CdssController {

  private final CdssSupplierService cdssSupplierService;
  private final CdssService cdssService;

  private final FhirContext fhirContext;

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPPLIER_ADMIN','ROLE_NHS','ROLE_CDSS')")
  @GetMapping
  public @ResponseBody
  List<CdssSupplierDTO> getCdssSuppliers(HttpServletRequest request) {
    return cdssSupplierService.getCdssSuppliers(request.getUserPrincipal().getName());
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPPLIER_ADMIN','ROLE_NHS','ROLE_CDSS')")
  @GetMapping("/{id}")
  public @ResponseBody
  CdssSupplier getCdssSupplier(@PathVariable Long id, HttpServletRequest request) {
    return cdssSupplierService.getCdssSupplier(id);
  }

  @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_SUPPLIER_ADMIN','ROLE_NHS','ROLE_CDSS')")
  @GetMapping("/{id}/ServiceDefinition")
  public @ResponseBody
  List<ServiceDefinitionDTO> getServiceDefinitions(@PathVariable Long id,
      HttpServletRequest request) {
    CdssSupplier cdssSupplier = cdssSupplierService.getCdssSupplier(id);
    CdssSupplierDTO cdssSupplierDTO = cdssService
        .queryServiceDefinitions(cdssSupplier, SearchParameters.builder().build());
    return cdssSupplierDTO.getServiceDefinitions();
  }

  /**
   * Proxy a request for a service definition through a cactus-authenticated client.
   * @param cdssId ID of the CDSS Supplier where the service definition is
   * @param serviceDefId ID of the service definition on the CDSS
   * @return the full FHIR ServiceDefinition resource.
   */
  @GetMapping(value = "/{cdssId}/{serviceDefId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public @ResponseBody
  String proxyServiceDefinition(
      @PathVariable Long cdssId,
      @PathVariable String serviceDefId) {
    // Jackson can't serialize the resource so we have to get HAPI to do it manually.
    return fhirContext.newJsonParser()
        .encodeResourceToString(cdssService.getServiceDefinition(cdssId, serviceDefId));
  }

  @PostMapping
  public @ResponseBody
  CdssSupplier createCdssSupplier(@RequestBody NewCdssSupplierDTO cdssSupplier) {
    return cdssSupplierService.createCdssSupplier(cdssSupplier);
  }

  @PutMapping
  public @ResponseBody
  CdssSupplier updateCdssSupplier(@RequestBody CdssSupplier cdssSupplier) {
    return cdssSupplierService.updateCdssSupplier(cdssSupplier);
  }

  @DeleteMapping(path = "/{id}")
  public @ResponseBody
  void deleteCdssSupplier(@PathVariable("id") Long id) {
    cdssSupplierService.deleteCdssSupplier(id);
  }
}
