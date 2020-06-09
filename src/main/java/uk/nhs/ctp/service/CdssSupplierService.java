package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.entities.ServiceDefinition;
import uk.nhs.ctp.entities.UserEntity;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.ServiceDefinitionRepository;
import uk.nhs.ctp.repos.UserRepository;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.dto.NewCdssSupplierDTO;
import uk.nhs.ctp.service.dto.ServiceDefinitionDTO;
import uk.nhs.ctp.transform.CdssSupplierDTOTransformer;

@Service
@RequiredArgsConstructor
public class CdssSupplierService {

  private final UserRepository userRepository;
  private final CdssSupplierRepository cdssSupplierRepository;
  private final ServiceDefinitionRepository serviceDefinitionRepository;
  private final TokenAuthenticationService authService;
  private final CdssSupplierDTOTransformer cdssTransformer;

  /**
   * Returns a list of CDSS suppliers that the user has access to.
   *
   * @param username Username
   * @return {@link CdssSupplierDTO}
   */
  public List<CdssSupplierDTO> getCdssSuppliers(String username) {
    UserEntity userEntity = userRepository.findByUsername(username);
    authService.requireSupplierId(userEntity.getSupplierId());

    List<CdssSupplier> suppliers;

    if (userEntity.getRole().equals(SystemConstants.ROLE_NHS)
        || userEntity.getRole().equals(SystemConstants.ROLE_ADMIN)
        || userEntity.getRole().equals(SystemConstants.ROLE_SUPPLIER_ADMIN)) {
      suppliers = cdssSupplierRepository
          .findAllBySupplierId(authService.requireSupplierId());
    } else {
      throw new EMSException(HttpStatus.FORBIDDEN, "User has invalid role");
    }

    return suppliers.stream()
        .map(cdssTransformer::transform)
        .collect(Collectors.toList());
  }

  public CdssSupplier getCdssSupplier(Long id) {
    return cdssSupplierRepository.getOneByIdAndSupplierId(id, authService.requireSupplierId())
        .orElseThrow(EMSException::notFound);
  }

  public CdssSupplier createCdssSupplier(NewCdssSupplierDTO newCdssSupplierDTO) {
    String supplierId = authService.requireSupplierId();

    CdssSupplier cdssSupplier = new CdssSupplier();
    cdssSupplier.setName(newCdssSupplierDTO.getName());
    cdssSupplier.setBaseUrl(newCdssSupplierDTO.getBaseUrl());
    cdssSupplier.setInputDataRefType(newCdssSupplierDTO.getInputDataRefType());
    cdssSupplier.setInputParamsRefType(newCdssSupplierDTO.getInputParamsRefType());
    cdssSupplier.setSupportedVersion(newCdssSupplierDTO.getSupportedVersion());
    cdssSupplier.setSupplierId(supplierId);
    cdssSupplier = cdssSupplierRepository.save(cdssSupplier);

    // for each service definition DTO, loop through and create a service definition
    // and add it to the service definition list.
    for (ServiceDefinitionDTO serviceDefinitionDTO : newCdssSupplierDTO.getServiceDefinitions()) {
      ServiceDefinition newServiceDefinition = new ServiceDefinition();
      newServiceDefinition.setDescription(serviceDefinitionDTO.getDescription());
      newServiceDefinition.setServiceDefinitionId(serviceDefinitionDTO.getServiceDefinitionId());
      newServiceDefinition.setCdssSupplierId(cdssSupplier.getId());
      newServiceDefinition.setSupplierId(supplierId);
      // call service definition repository and save the service definition.
      serviceDefinitionRepository.save(newServiceDefinition);
    }

    return cdssSupplierRepository.getOne(cdssSupplier.getId());
  }

  public CdssSupplier updateCdssSupplier(CdssSupplier cdssSupplier) {
    String supplierId = authService.requireSupplierId();
    cdssSupplierRepository.getOneByIdAndSupplierId(cdssSupplier.getId(), supplierId)
        .orElseThrow(EMSException::notFound);

    cdssSupplier.setSupplierId(supplierId);

    // save new/update service definitions
    // TODO check service definition supplierIDs match
    for (ServiceDefinition serviceDefinition : cdssSupplier.getServiceDefinitions()) {
      serviceDefinition.setSupplierId(supplierId);
      serviceDefinition.setCdssSupplierId(cdssSupplier.getId());
    }

    return cdssSupplierRepository.saveAndFlush(cdssSupplier);
  }

  @Transactional
  public void deleteCdssSupplier(Long id) {
    cdssSupplierRepository.deleteByIdAndSupplierId(id, authService.requireSupplierId());
  }
}
