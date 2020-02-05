package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.ReferralRequestEntity;

@Repository
public interface ReferralRequestRepository extends JpaRepository<ReferralRequestEntity, Long> {
  ReferralRequestEntity findByCaseEntity_Id(Long caseId);
}
