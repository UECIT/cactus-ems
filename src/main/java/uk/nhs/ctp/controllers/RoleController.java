package uk.nhs.ctp.controllers;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import uk.nhs.ctp.entities.Skillset;
import uk.nhs.ctp.service.SkillsetService;

@CrossOrigin
@RestController
@RequestMapping(path = "/role")
@AllArgsConstructor
public class RoleController {

  private SkillsetService skillsetService;

  @GetMapping()
  public @ResponseBody
  List<Skillset> getAllSkillsets() {
    return skillsetService.getAll();
  }

}
