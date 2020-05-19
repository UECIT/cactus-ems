package uk.nhs.ctp.repos;

import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.ServiceDefinition;

@Repository
public interface ServiceDefinitionRepository extends PartitionedRepository<ServiceDefinition, Long> {

}
