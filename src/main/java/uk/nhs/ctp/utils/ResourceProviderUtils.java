package uk.nhs.ctp.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Component;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import uk.nhs.ctp.OperationOutcomeFactory;
import uk.nhs.ctp.SystemCode;

@Component
public class ResourceProviderUtils {

	public static Reference getParameterAsReference(
			List<ParametersParameterComponent> parameters,
			String parameterName) {

		return castToType(getParameterByName(parameters, parameterName).getValue(), Reference.class);
	}
	
	public static <T extends Resource> T getParameterAsResource(
			List<ParametersParameterComponent> parameters, String parameterName, Class<T> resourceClass) {

		return getResource(getParameterByName(parameters, parameterName).getResource(), resourceClass);
	}
	
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

	static List<ParametersParameterComponent> getParametersByName(List<ParametersParameterComponent> parameters,
			String parameterName) {

		return parameters.stream()
				.filter(p -> parameterName.equals(p.getName()))
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
		try {
			return resourceClass.cast(resource);
		} catch (ClassCastException ignored) {
		  return null;
		}
	}
	
	public static <T extends Resource> T getResource(IBaseResource resource, Class<T> resourceClass) {
		return getResource((Resource)resource, resourceClass);
	}
	
	public static <T extends Resource> T getResource(List<Resource> resources, Class<T> resourceClass) {
		return resources.stream()
        .filter(resourceClass::isInstance)
        .findFirst()
        .map(value -> getResource(value, resourceClass))
        .orElse(null);
	}
	
	public static <T extends Resource> T getResource(Collection<Reference> references, Class<T> resourceClass) {
		return references.stream()
        .filter(resourceClass::isInstance)
        .findFirst()
        .map(value -> getResource(value.getResource(), resourceClass))
        .orElse(null);
	}
	
	public static <T extends Resource> T getResource(Bundle bundle, Class<T> resourceClass) {
		return bundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(resourceClass::isInstance)
        .findFirst()
        .map(component -> getResource(component, resourceClass))
        .orElse(null);
	}
	
	public static <T extends Resource> List<T> getResources(Bundle bundle, Class<T> resourceClass) {
		return getResources(
		    bundle.getEntry()
            .stream()
            .map(BundleEntryComponent::getResource)
            .collect(Collectors.toList()),
        resourceClass);
	}
	

	
	public static <T extends Resource> List<T> getResources(List<Resource> resources, Class<T> resourceClass) {
		List<T> typedResources = new ArrayList<>();
		
		resources.stream()
				.filter(obj -> obj.getClass().equals(resourceClass))
				.forEach(obj -> typedResources.add(getResource(obj, resourceClass)));
		
		return typedResources;
	}
	
	public static <T extends Resource> T getResource (FhirContext ctx, String baseUrl, Class<T> resourceClass, String resourceUrl) {
		IGenericClient client = ctx.newRestfulGenericClient(baseUrl);
    return client.read().resource(resourceClass).withUrl(resourceUrl).execute();
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
