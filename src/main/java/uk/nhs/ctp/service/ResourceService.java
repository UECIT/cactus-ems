package uk.nhs.ctp.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;

@Service
public class ResourceService {
	
	@Autowired
	private FhirContext ctx;
	
	public String getResource(String url) throws MalformedURLException, ClassNotFoundException {
		IGenericClient client = ctx.newRestfulGenericClient(buildBaseUrl(url));
		Resource resource = client.read().resource(getResourceType(url)).withUrl(url).execute();
		return ctx.newJsonParser().encodeResourceToString(resource);
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends Resource> getResourceType(String reference) throws ClassNotFoundException {
		reference = reference.replace("http://", "");
		return (Class<? extends Resource>) Class.forName(Resource.class.getPackage().getName() + "." + reference.split("/")[2]);
	}
	
	public String buildBaseUrl(String url) throws MalformedURLException {
		URL uri = new URL(url);
		if (url.contains("/fhir")) {
			return uri.getProtocol() + "://" + uri.getAuthority() + "/fhir";
		} else {
			return uri.getProtocol() + "://" + uri.getAuthority();
		}
	}

}
