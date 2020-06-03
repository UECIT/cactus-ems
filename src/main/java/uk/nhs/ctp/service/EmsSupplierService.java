package uk.nhs.ctp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

  public EmsSupplier crupdate(EmsSupplier updated) {
    String supplierId = authService.requireSupplierId();

    emsSupplierRepository
        .getOneByIdAndSupplierId(updated.getId(), supplierId)
        .orElseThrow(EMSException::notFound);

    updated.setSupplierId(supplierId);

    return emsSupplierRepository.saveAndFlush(updated);
  }

  public void delete(Long id) {
    emsSupplierRepository.deleteByIdAndSupplierId(id, authService.requireSupplierId());
  }

}
