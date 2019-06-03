package uk.nhs.ctp.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller("/document")
public class DocumentController {
	
	@GetMapping
    public String get(@RequestParam(name="id", required=true) String id) {
        return id;
    }
}
