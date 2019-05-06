package uk.nhs.ctp.controllers;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.nhs.ctp.service.ResourceService;

@CrossOrigin
@RestController
@RequestMapping(path = "/resource")
public class ResourceController {
	
	@Autowired
	private ResourceService resourceService;
	
	@PostMapping
	public @ResponseBody String getResource(@RequestBody String url) throws MalformedURLException, ClassNotFoundException {
		return resourceService.getResource(url);
	}

}
