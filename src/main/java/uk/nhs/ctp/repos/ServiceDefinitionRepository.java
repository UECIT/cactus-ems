package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.ServiceDefinition;

import java.util.List;

@Repository
public interface ServiceDefinitionRepository extends JpaRepository<ServiceDefinition, Long> {

	@Query(value = "select sd.* from service_definition sd,\n" + "  scenario_cdss_service_definition sdCDSS,\n"
			+ "  cdss_supplier cdss\n" + "where sd.id = sdCDSS.service_definition_id\n"
			+ "and cdss.id = sdCDSS.cdss_supplier_id\n" + "and cdss.display_name = ?1", nativeQuery = true)
	List<ServiceDefinition> getAllServiceDefinitionsForSupplier(String supplier);
}
