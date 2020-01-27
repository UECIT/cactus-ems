package uk.nhs.ctp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class PatientController {

  /*
   * Endpoints to retrieve, update and delete patients
   */

  @Autowired
  private PatientRepository patientRepository;

  @GetMapping(path = "/all")
  public @ResponseBody
  List<PatientEntity> getAllPatients() {
    return patientRepository.findAll();
  }
}
