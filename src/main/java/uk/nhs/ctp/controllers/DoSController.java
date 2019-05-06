package uk.nhs.ctp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import uk.nhs.ctp.service.DoSService;
import uk.nhs.ctp.service.dto.DoSRequestDTO;

@CrossOrigin
@RestController
@RequestMapping(path = "/dos")
public class DoSController {

	@Autowired
	private DoSService dosService;

	@PostMapping
	public @ResponseBody Object getDoS(@RequestBody DoSRequestDTO requestDTO) throws Exception {
		if (requestDTO.getService().equalsIgnoreCase("soap")) {
			return dosService.getDoSSOAPService(requestDTO);
		} else if (requestDTO.getService().equalsIgnoreCase("rest")) {
			return dosService.getDoSRESTService(requestDTO);
		} else {
			throw new IllegalArgumentException("Unrecognised service requested: " + requestDTO.getService());
		}
	}

}
