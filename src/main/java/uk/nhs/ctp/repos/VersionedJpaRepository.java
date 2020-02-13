package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.nhs.ctp.entities.IdVersion;

public interface VersionedJpaRepository<T> extends JpaRepository<T, IdVersion> {
  T findFirstByIdVersion_IdOrderByIdVersion_VersionDesc(Long id);
}
