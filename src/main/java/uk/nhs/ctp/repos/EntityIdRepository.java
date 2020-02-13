package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.EntityId;

@Repository
public interface EntityIdRepository extends JpaRepository<EntityId, String>, EntityIdIncrementer {

}
