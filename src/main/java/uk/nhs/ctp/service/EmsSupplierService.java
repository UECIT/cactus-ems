package uk.nhs.ctp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@Service
@RequiredArgsConstructor
public class EmsSupplierService {

  private final EmsSupplierRepository emsSupplierRepository;
  private final TokenAuthenticationService tokenAuthenticationService;

  public List<EmsSupplier> getAll() {
    return emsSupplierRepository
        .findAllBySupplierId(tokenAuthenticationService.requireSupplierId());
  }

  public EmsSupplier crupdate(EmsSupplier updated) {
    return emsSupplierRepository.saveAndFlush(updated);
  }

  public void delete(Long id) {
    emsSupplierRepository.delete(id);
  }

}
