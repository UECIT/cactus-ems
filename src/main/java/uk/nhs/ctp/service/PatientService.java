package uk.nhs.ctp.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;

@Service
public class PatientService {

	@Autowired
	private PatientRepository patientRepository;

	public List<PatientEntity> getAllPatients() {
		return patientRepository.findAll();
	}

	public PatientEntity findById(long id) {
		return patientRepository.findById(id);
	}

}
