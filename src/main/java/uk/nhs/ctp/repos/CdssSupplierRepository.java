package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.CdssSupplier;

@Repository
public interface CdssSupplierRepository extends JpaRepository<CdssSupplier, Long> {

}