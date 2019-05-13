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

import uk.nhs.ctp.service.dto.HandoverRequestDTO;
import uk.nhs.ctp.service.handover.LocalResourceHandoverService;
import uk.nhs.ctp.service.handover.RemoteResourceHandoverService;

@CrossOrigin
@RestController
@RequestMapping(path = "/handover")
public class HandoverController {
	
	@Autowired
	private RemoteResourceHandoverService remoteResourceHandoverService;
	
	@Autowired
	private LocalResourceHandoverService localResourceHandoverService;
	
	@PostMapping
	public @ResponseBody String getHandoverMessage(@RequestBody HandoverRequestDTO request) 
			throws MalformedURLException, ClassNotFoundException, JsonProcessingException {
		
		return request.hasRemoteUrl() ? 
				remoteResourceHandoverService.getHandoverMessage(request) :
				localResourceHandoverService.getHandoverMessage(request);
	}

}
