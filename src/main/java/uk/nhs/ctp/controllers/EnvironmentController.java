package uk.nhs.ctp.controllers;

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

  @Value("${back.colour}")
  private String colour;


  @GetMapping("colour")
  public @ResponseBody String getColour() {
    return colour;
  }

  // no change
}
