package uk.nhs.ctp.service;

import java.io.IOException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import com.fasterxml.jackson.databind.ObjectMapper;

import dos.wsdl.AgeFormatType;
import dos.wsdl.ArrayOfInt;
import dos.wsdl.Case;
import dos.wsdl.CheckCapacitySummary;
import dos.wsdl.CheckCapacitySummaryResponse;
import dos.wsdl.ObjectFactory;
import dos.wsdl.UserInfo;
import uk.nhs.ctp.service.dto.DoSRequestDTO;
import uk.nhs.ctp.service.report.dos.rest.DosApiResponseDTO;

@Service
public class DoSService extends WebServiceGatewaySupport {

	private static final Logger LOG = LoggerFactory.getLogger(DoSService.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Value("${dos.username}")
	private String dosUsername;
	
	@Value("${dos.password}")
	private String dosPassword;

	public CheckCapacitySummaryResponse getDoSSOAPService(DoSRequestDTO doSRequestDTO) {

		ObjectFactory ob = new ObjectFactory();

		CheckCapacitySummary request = ob.createCheckCapacitySummary();
		UserInfo user = ob.createUserInfo();
		user.setUsername(dosUsername);
		user.setPassword(dosPassword);
		request.setUserInfo(user);

		Case cas = ob.createCase();
		cas.setCaseRef("hackday");
		cas.setCaseId("hackdayTest-DW");
		cas.setPostcode(doSRequestDTO.getPostcode());
		cas.setSurgery("UNK");
		cas.setAge((short) 1);
		cas.setAgeFormat(AgeFormatType.AGE_GROUP);
		cas.setDisposition(doSRequestDTO.getDisposition());
		cas.setSymptomGroup(doSRequestDTO.getSymptomGroup());

		ArrayOfInt symptomDiscriminatorList = ob.createArrayOfInt();
		symptomDiscriminatorList.getInt().add(doSRequestDTO.getSymptomDiscriminatorInt());

		cas.setSymptomDiscriminatorList(symptomDiscriminatorList);
		cas.setSearchDistance(doSRequestDTO.getSearchDistance());
		cas.setGender(doSRequestDTO.getGender());
		request.setC(cas);
		CheckCapacitySummaryResponse response = (CheckCapacitySummaryResponse) getWebServiceTemplate()
				.marshalSendAndReceive(request);
		return response;
	}

	public DosApiResponseDTO getDoSRESTService(DoSRequestDTO requestDTO) {
		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(null,
				createHeaders(dosUsername, dosPassword));

		ResponseEntity<String> response = restTemplate.exchange(requestDTO.getRESTUrl(), HttpMethod.GET, requestEntity,
				String.class);

		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(response.getBody(), DosApiResponseDTO.class);
		} catch (IOException e) {
			LOG.error("Can't convert response to DosApiResponse.");
		}
		return null;
	}

	HttpHeaders createHeaders(String username, String password) {
		HttpHeaders headers = new HttpHeaders();

		// auth
		String auth = username + ":" + password;
		String authHeader = "Basic " + Base64.getUrlEncoder().withoutPadding().encodeToString(auth.getBytes());
		headers.add("Authorization", authHeader);

		return headers;
	}

}
