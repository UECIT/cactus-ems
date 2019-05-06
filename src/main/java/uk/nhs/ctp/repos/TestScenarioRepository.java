package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.TestScenario;

@Repository
public interface TestScenarioRepository extends JpaRepository<TestScenario, Long> {
	TestScenario findByPatientId(Long patientId);
}
