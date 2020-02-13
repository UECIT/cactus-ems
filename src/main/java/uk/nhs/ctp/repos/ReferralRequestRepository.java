package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.ReferralRequestEntity;

@Repository
public interface ReferralRequestRepository extends JpaRepository<ReferralRequestEntity, Long> {
  ReferralRequestEntity findByEncounterEntity_Id(Long caseId); // Needs to Be ID and Version (Currently throwing PropertyReferenceException)
}
