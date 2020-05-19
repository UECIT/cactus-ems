package uk.nhs.ctp.repos;

import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.CdssSupplier;

@Repository
public interface CdssSupplierRepository extends PartitionedRepository<CdssSupplier> {

}