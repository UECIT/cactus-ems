package uk.nhs.ctp.controllers;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.PatientEntity;
import uk.nhs.ctp.repos.PatientRepository;
import uk.nhs.ctp.service.GenericResourceLocator;
import uk.nhs.ctp.service.ReferenceService;
import uk.nhs.ctp.service.dto.PatientDTO;
import uk.nhs.ctp.transform.PatientTransformer;

@CrossOrigin
@RestController
@RequestMapping(path = "/patient")
@RequiredArgsConstructor
public class PatientController {

  private final PatientRepository patientRepository;
  private final GenericResourceLocator resourceLocator;
  private final ReferenceService referenceService;
  private final PatientTransformer patientTransformer;

  @GetMapping(path = "/all")
  public @ResponseBody
  List<PatientDTO> getAllPatients() {
    List<PatientEntity> all = patientRepository.findAll();
    return all.stream()
        .map(patientTransformer::transform)
        .collect(Collectors.toList());
  }

  @GetMapping
  public @ResponseBody
  PatientDTO getPatient(@RequestParam String patientRef, @RequestParam String encounterRef) {
    Patient patientResource = resourceLocator
        .findResource(new Reference(patientRef), new IdType(encounterRef));
    return patientTransformer.transform(patientResource);
  }
}
