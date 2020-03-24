package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.EmsSupplier;

@Repository
public interface EmsSupplierRepository extends JpaRepository<EmsSupplier, Long> {

}
