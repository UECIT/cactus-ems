package uk.nhs.ctp.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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

@Service
@RequiredArgsConstructor
public class CdssSupplierService {

  private final UserRepository userRepository;
  private final CdssSupplierRepository cdssSupplierRepository;
  private final ServiceDefinitionRepository serviceDefinitionRepository;

  /**
   * Returns a list of CDSS suppliers that the user has access to.
   *
   * @param username Username
   * @return {@link CdssSupplierDTO}
   */
  public List<CdssSupplierDTO> getCdssSuppliers(String username) {
    UserEntity userEntity = userRepository.findByUsername(username);

    List<CdssSupplier> suppliers;

    if (userEntity.getRole().equals(SystemConstants.ROLE_NHS)
        || userEntity.getRole().equals(SystemConstants.ROLE_ADMIN)) {
      //TODO: CDSCT-139
      suppliers = cdssSupplierRepository.findAllBySupplierId(null);
    } else if (userEntity.getRole().equals(SystemConstants.ROLE_CDSS)) {
      suppliers = userEntity.getCdssSuppliers();
    } else {
      throw new EMSException(HttpStatus.FORBIDDEN, "User has invalid role");
    }

    return convertToSupplierDTO(suppliers);
  }

  public List<CdssSupplier> getCdssSuppliersUnfiltered(String username) {
    UserEntity userEntity = userRepository.findByUsername(username);

    List<CdssSupplier> suppliers;

    if (userEntity.getRole().equals(SystemConstants.ROLE_NHS)
        || userEntity.getRole().equals(SystemConstants.ROLE_ADMIN)) {
      //TODO: CDSCT-139
      suppliers = cdssSupplierRepository.findAllBySupplierId(null);
    } else if (userEntity.getRole().equals(SystemConstants.ROLE_CDSS)) {
      suppliers = userEntity.getCdssSuppliers();
    } else {
      throw new EMSException(HttpStatus.FORBIDDEN, "User has invalid role");
    }

    return suppliers;
  }

  protected List<CdssSupplierDTO> convertToSupplierDTO(List<CdssSupplier> suppliers) {
    return suppliers.stream()
        .map(CdssSupplierDTO::new)
        .collect(Collectors.toList());
  }

  public CdssSupplier getCdssSupplier(Long id) {
    return findBySupplierId(id);
  }

  protected CdssSupplier findBySupplierId(Long id) {
    return cdssSupplierRepository.findOne(id);
  }

  public CdssSupplier createCdssSupplier(NewCdssSupplierDTO newCdssSupplierDTO) {
    CdssSupplier cdssSupplier = new CdssSupplier();
    cdssSupplier.setName(newCdssSupplierDTO.getName());
    cdssSupplier.setBaseUrl(newCdssSupplierDTO.getBaseUrl());
    cdssSupplier.setReferencingType(newCdssSupplierDTO.getReferencingType());
    cdssSupplier = cdssSupplierRepository.save(cdssSupplier);

    // for each service definition DTO, loop through and create a service definition
    // and add it to the service definition list.
    for (ServiceDefinitionDTO serviceDefinitionDTO : newCdssSupplierDTO.getServiceDefinitions()) {
      ServiceDefinition newServiceDefinition = new ServiceDefinition();
      newServiceDefinition.setDescription(serviceDefinitionDTO.getDescription());
      newServiceDefinition.setServiceDefinitionId(serviceDefinitionDTO.getServiceDefinitionId());
      newServiceDefinition.setCdssSupplierId(cdssSupplier.getId());
      // call service definition repository and save the service definition.
      serviceDefinitionRepository.save(newServiceDefinition);
    }

    return cdssSupplierRepository.getOne(cdssSupplier.getId());
  }

  public CdssSupplier updateCdssSupplier(CdssSupplier cdssSupplier) {

    // save new/update service definitions
    for (ServiceDefinition serviceDefinition : cdssSupplier.getServiceDefinitions()) {
      serviceDefinition.setCdssSupplierId(cdssSupplier.getId());
    }

    cdssSupplierRepository.saveAndFlush(cdssSupplier);

    return cdssSupplierRepository.findOne(cdssSupplier.getId());
  }

  public void deleteCdssSupplier(Long cdssSupplierId) {
    cdssSupplierRepository.delete(cdssSupplierId);
  }
}
