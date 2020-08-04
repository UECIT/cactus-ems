package uk.nhs.ctp.controllers;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.model.Practitioner;
import uk.nhs.ctp.registry.Registry;
import uk.nhs.ctp.service.dto.PractitionerDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/practitioner")
@RequiredArgsConstructor
public class PractitionerController {

  private final Registry<Practitioner> practitionerRegistry;

  @GetMapping(path = "/all")
  public @ResponseBody List<PractitionerDTO> getAllPractitioners() {
    return practitionerRegistry.getAll()
        .stream()
        .map(PractitionerController::transform)
        .collect(Collectors.toUnmodifiableList());
  }

  private static PractitionerDTO transform(Practitioner practitioner) {
    return new PractitionerDTO(
        practitioner.getId(),
        practitioner.getName().getFullName()
    );
  }
}
