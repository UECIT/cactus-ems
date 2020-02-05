package uk.nhs.ctp.controllers;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;

@CrossOrigin
@RestController
@RequestMapping(path = "/patient")
@RequiredArgsConstructor
public class PatientController {

  private final PatientRepository patientRepository;

  @GetMapping(path = "/all")
  public @ResponseBody
  List<PatientEntity> getAllPatients() {
    return patientRepository.findAll();
  }
}
