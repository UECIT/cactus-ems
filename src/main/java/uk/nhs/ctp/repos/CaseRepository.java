package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.Cases;

@Repository
public interface CaseRepository extends JpaRepository<Cases, Long> {
}
