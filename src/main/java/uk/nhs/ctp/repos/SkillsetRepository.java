package uk.nhs.ctp.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.ctp.entities.Skillset;

@Repository
public interface SkillsetRepository  extends JpaRepository<Skillset, Long> {

}
