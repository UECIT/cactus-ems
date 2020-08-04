package uk.nhs.ctp.controllers;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(path = "/environment")
public class EnvironmentController {

  @Value("${cds.api.version}")
  private String apiVersion;

  @Value("${environment.name}")
  private String envName;

  @Value("${app.version}")
  private String appVersion;


  @GetMapping("properties")
  public @ResponseBody Map<String, String> getProps() {
    return Map.ofEntries(
        new SimpleEntry<>("apiVersion", apiVersion),
        new SimpleEntry<>("name", envName),
        new SimpleEntry<>("appVersion", appVersion)
    );
  }


}
