package uk.nhs.ctp.repos;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.EmsSupplier;

@Repository
public interface EmsSupplierRepository extends PartitionedRepository<EmsSupplier, Long> {

  Optional<EmsSupplier> getOneBySupplierIdAndBaseUrl(String supplierId, String baseUrl);

}
