package uk.nhs.ctp.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import uk.nhs.ctp.service.DoSService;

@Configuration
public class DosConfig {

	@Bean
	public Jaxb2Marshaller marshaller() {
		Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
		// this package must match the package in the <generatePackage> specified in
		// pom.xml
		marshaller.setContextPath("dos.wsdl");
		Map<String, DefaultNamespacePrefixMapper> properties = new HashMap<>();
		properties.put("com.sun.xml.bind.namespacePrefixMapper", new DefaultNamespacePrefixMapper());
		marshaller.setMarshallerProperties(properties);
		return marshaller;
	}

	@Bean
	public DoSService dosService(Jaxb2Marshaller marshaller) {
		DoSService client = new DoSService();
		client.setDefaultUri("https://uat.pathwaysdos.nhs.uk/app/api/webservices");
		client.setMarshaller(marshaller);
		client.setUnmarshaller(marshaller);
		return client;
	}

}