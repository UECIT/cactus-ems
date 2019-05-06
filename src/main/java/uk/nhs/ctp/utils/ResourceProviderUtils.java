package uk.nhs.ctp.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import uk.nhs.ctp.OperationOutcomeFactory;
import uk.nhs.ctp.SystemCode;

@Component
public class ResourceProviderUtils {

	public static ParametersParameterComponent getParameterByName(List<ParametersParameterComponent> parameters,
			String parameterName) {
		ParametersParameterComponent parameter = null;

		List<ParametersParameterComponent> filteredParameters = getParametersByName(parameters, parameterName);

		if (filteredParameters != null) {
			if (filteredParameters.size() == 1) {
				parameter = filteredParameters.get(0);
			} else if (filteredParameters.size() > 1) {
				throw OperationOutcomeFactory.buildOperationOutcomeException(
						new InvalidRequestException("The parameter " + parameterName + " cannot be set more than once"),
						SystemCode.BAD_REQUEST, IssueType.INVALID);
			}
		}

		return parameter;
	}

	public static List<ParametersParameterComponent> getParametersByName(List<ParametersParameterComponent> parameters,
			String parameterName) {

		return parameters.stream().filter(currentParameter -> parameterName.equals(currentParameter.getName()))
				.collect(Collectors.toList());
	}

	public static <T> T castToType(Object object, Class<T> type) {
		if (type.isInstance(object)) {
			return type.cast(object);
		}
		throw OperationOutcomeFactory.buildOperationOutcomeException(
				new InvalidRequestException("Invalid parameter type in request body. Should be " + type.toString()),
				SystemCode.BAD_REQUEST, IssueType.INVALID);
	}
	
	public static <T extends Resource> T getResource(Resource resource, Class<T> resourceClass) {
        T t = null;
        
        try {
            t = resourceClass.cast(resource);
        } catch (ClassCastException e) {
        }
        
        return t;
	}
	
	public static <T extends Resource> T getResource(List<Resource> resources, Class<T> resourceClass) {
		Optional<Resource> resource = 
				resources.stream().filter(obj -> obj.getClass().equals(resourceClass)).findFirst();
		
		return resource.isPresent() ? getResource(resource.get(), resourceClass) : null;
	}
	
	public static <T extends Resource> T getResource(Bundle bundle, Class<T> resourceClass) {
		Optional<BundleEntryComponent> resource = bundle.getEntry().stream().filter(obj -> 
				obj.getResource().getClass().equals(resourceClass)).findFirst();
		
		return resource.isPresent() ? getResource(resource.get().getResource(), resourceClass) : null;
	}
	
	public static <T extends Resource> List<T> getResources(List<Resource> resources, Class<T> resourceClass) {
		List<T> typedResources = new ArrayList<>();
		
		resources.stream()
				.filter(obj -> obj.getClass().equals(resourceClass))
				.forEach(obj -> typedResources.add(getResource(obj, resourceClass)));
		
		return typedResources;
	}
	
	public static <T extends Resource> T getResource (String baseUrl, Class<T> resourceClass, String resourceUrl) {
		FhirContext ctx = FhirContext.forDstu3();
		IGenericClient client = ctx.newRestfulGenericClient(baseUrl);
		T resource = client.read().resource(resourceClass).withUrl(resourceUrl).execute();
		
		return resource;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Resource> Class<T> getResourceType(String reference) {
		try {
			return (Class<T>) Class.forName(Resource.class.getPackage().getName() + "." + reference.split("/")[0]);
		} catch (ClassNotFoundException e) {
			return (Class<T>) Resource.class;
		}
	}

}
