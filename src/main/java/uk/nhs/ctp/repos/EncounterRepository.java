package uk.nhs.ctp.repos;

import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.EncounterEntity;

@Repository
public interface EncounterRepository extends VersionedJpaRepository<EncounterEntity> {

}
