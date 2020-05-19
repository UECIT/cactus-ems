package uk.nhs.ctp.repos;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.nhs.ctp.entities.SupplierPartitioned;

@NoRepositoryBean
public interface PartitionedRepository<T extends SupplierPartitioned> extends JpaRepository<T , Long> {

  List<T> findAllBySupplierId(String supplierId);

}
