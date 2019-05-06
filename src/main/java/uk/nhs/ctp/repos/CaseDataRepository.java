package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.ctp.entities.CaseObservation;

@Repository
public interface CaseDataRepository extends JpaRepository<CaseObservation, Long> {
}
