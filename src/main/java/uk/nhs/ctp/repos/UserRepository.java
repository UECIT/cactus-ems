package uk.nhs.ctp.repos;

import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.UserEntity;

@Repository
public interface UserRepository extends PartitionedRepository<UserEntity, String> {
	UserEntity findByUsername(String username);
}
