package uk.nhs.ctp.repos;

import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.nhs.ctp.entities.SupplierPartitioned;

@NoRepositoryBean
public interface PartitionedRepository<T extends SupplierPartitioned> extends JpaRepository<T , Long> {

  Collection<T> findAllBySupplierId(String supplierId);

}
