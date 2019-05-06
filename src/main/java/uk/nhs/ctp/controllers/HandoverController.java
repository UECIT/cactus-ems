package uk.nhs.ctp.controllers;

import java.net.MalformedURLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import uk.nhs.ctp.service.HandoverService;
import uk.nhs.ctp.service.dto.HandoverRequestDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/handover")
public class HandoverController {
	
	@Autowired
	private HandoverService handoverService;
	
	@PostMapping
	public @ResponseBody String getHandoverMessage(@RequestBody HandoverRequestDTO handoverRequestDTO) 
			throws MalformedURLException, ClassNotFoundException, JsonProcessingException {
		
		return handoverService.getHandoverMessage(handoverRequestDTO.getResourceUrl(), handoverRequestDTO.getCaseId());
	}

}
