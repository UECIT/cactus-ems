package uk.nhs.ctp.service;

import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@Service
@RequiredArgsConstructor
public class EmsSupplierService {

  private final EmsSupplierRepository emsSupplierRepository;
  private final TokenAuthenticationService authService;

  public List<EmsSupplier> getAll() {
    return emsSupplierRepository
        .findAllBySupplierId(authService.requireSupplierId());
  }

  public Optional<EmsSupplier> findEmsSupplierByBaseUrl(String baseUrl) {
    return emsSupplierRepository
        .getOneBySupplierIdAndBaseUrl(authService.requireSupplierId(), baseUrl);
  }

  public EmsSupplier crupdate(EmsSupplier updated) {
    String supplierId = authService.requireSupplierId();

    EmsSupplier existingEntry = emsSupplierRepository
        .getOneByIdAndSupplierId(updated.getId(), supplierId)
        .orElse(null);

    if (existingEntry == null && updated.getId() != null) {
      throw EMSException.notFound();
    }

    updated.setSupplierId(supplierId);

    return emsSupplierRepository.saveAndFlush(updated);
  }

  @Transactional
  public void delete(Long id) {
    emsSupplierRepository.deleteByIdAndSupplierId(id, authService.requireSupplierId());
  }

}
