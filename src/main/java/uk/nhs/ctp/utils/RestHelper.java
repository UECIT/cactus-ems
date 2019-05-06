package uk.nhs.ctp.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class RestHelper {

	private RestTemplate template;

	public RestTemplate getTemplate() {
		return template;
	}

	public void setTemplate(RestTemplate template) {
		this.template = template;
	}

	public RestHelper() {
		template = new RestTemplate();
		List<HttpMessageConverter<?>> messageConverters = template.getMessageConverters();
		messageConverters.add(getFHIRConverter());
	}

	/*
	 * Mapper for FHIR resource from JSON
	 */
	private MappingJackson2HttpMessageConverter getFHIRConverter() {

		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		converter.setObjectMapper(objectMapper);

		MediaType fhir = new MediaType("application", "json+fhir");

		List<MediaType> jacksonTypes = new ArrayList<>(converter.getSupportedMediaTypes());

		jacksonTypes.add(fhir);

		converter.setSupportedMediaTypes(jacksonTypes);

		return converter;
	}
}
