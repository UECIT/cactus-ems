package uk.nhs.ctp.repos;

import java.util.Optional;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.CdssSupplier;

@Repository
public interface CdssSupplierRepository extends PartitionedRepository<CdssSupplier, Long> {

  Optional<CdssSupplier> getOneBySupplierIdAndBaseUrl(String supplierId, String baseUrl);

}