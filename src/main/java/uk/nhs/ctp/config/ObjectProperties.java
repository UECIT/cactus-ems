package uk.nhs.ctp.config;

import java.util.ArrayList;
import java.util.List;

import org.hl7.fhir.dstu3.model.Coding;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("ambulance.request")
public class ObjectProperties {
	
	private List<Coding> flags = new ArrayList<>();

	public List<Coding> getFlags() {
		return flags;
	}

	public void setFlags(List<Coding> flags) {
		this.flags = flags;
	}

}
