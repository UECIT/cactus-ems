package uk.nhs.ctp.service;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.entities.Skillset;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.repos.SkillsetRepository;

@Service
@AllArgsConstructor
public class SkillsetService {

	private SkillsetRepository skillsetRepository;

	public List<Skillset> getAll() {
		return skillsetRepository.findAll();
	}

}
