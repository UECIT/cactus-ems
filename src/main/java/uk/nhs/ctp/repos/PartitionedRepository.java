package uk.nhs.ctp.repos;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.nhs.ctp.entities.SupplierPartitioned;

@NoRepositoryBean
public interface PartitionedRepository<T extends SupplierPartitioned, I extends Serializable> extends JpaRepository<T , I> {

  List<T> findAllBySupplierId(String supplierId);

  Optional<T> getOneByIdAndSupplierId(Long id, String supplierId);

  void deleteByIdAndSupplierId(Long id, String supplierId);
}
