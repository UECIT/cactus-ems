package uk.nhs.ctp.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.EmsSupplier;
import uk.nhs.ctp.repos.EmsSupplierRepository;

@Service
@RequiredArgsConstructor
public class EmsSupplierService {

  private final EmsSupplierRepository emsSupplierRepository;

  public List<EmsSupplier> getAll() {
    //TODO: CDSCT-139
    return emsSupplierRepository.findAllBySupplierId(null);
  }

  public EmsSupplier crupdate(EmsSupplier updated) {
    return emsSupplierRepository.saveAndFlush(updated);
  }

  public void delete(Long id) {
    emsSupplierRepository.delete(id);
  }

}
