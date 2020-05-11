package uk.nhs.ctp.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.experimental.UtilityClass;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Bundle.BundleEntryComponent;
import org.hl7.fhir.dstu3.model.OperationOutcome.IssueType;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Resource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import uk.nhs.ctp.OperationOutcomeFactory;
import uk.nhs.ctp.SystemCode;

@UtilityClass
public class ResourceProviderUtils {
	
	public ParametersParameterComponent getParameterByName(
			List<ParametersParameterComponent> parameters,
			String parameterName) {

		var filteredParameters = getParametersByName(parameters, parameterName);
		if (filteredParameters == null || filteredParameters.size() == 0) {
			return null;
		}

		if (filteredParameters.size() == 1) {
			return filteredParameters.get(0);
		}

		throw OperationOutcomeFactory.buildOperationOutcomeException(
				new InvalidRequestException("The parameter " + parameterName + " can be set only once"),
				SystemCode.BAD_REQUEST, IssueType.INVALID);
	}

	List<ParametersParameterComponent> getParametersByName(
			List<ParametersParameterComponent> parameters,
			String parameterName) {

		return parameters.stream()
				.filter(p -> parameterName.equals(p.getName()))
				.collect(Collectors.toList());
	}

	public <T> T castToType(Object object, Class<T> type) {
		if (type.isInstance(object)) {
			return type.cast(object);
		}
		throw OperationOutcomeFactory.buildOperationOutcomeException(
				new InvalidRequestException("Invalid parameter type in request body. Should be " + type.toString()),
				SystemCode.BAD_REQUEST, IssueType.INVALID);
	}

	public <T extends Resource> T getResource(List<Resource> resources, Class<T> resourceClass) {
		return resources.stream()
        .filter(resourceClass::isInstance)
        .findFirst()
        .map(resourceClass::cast)
        .orElse(null);
	}
	
	public <T extends Resource> T getResource(Collection<Reference> references, Class<T> resourceClass) {
		return references.stream()
        .filter(resourceClass::isInstance)
        .findFirst()
				.map(resourceClass::cast)
        .orElse(null);
	}
	
	public <T extends Resource> T getResource(Bundle bundle, Class<T> resourceClass) {
		return bundle.getEntry()
        .stream()
        .map(BundleEntryComponent::getResource)
        .filter(resourceClass::isInstance)
        .findFirst()
				.map(resourceClass::cast)
        .orElse(null);
	}
	
	public <T extends Resource> List<T> getResources(Bundle bundle, Class<T> resourceClass) {
		return getResources(
		    bundle.getEntry()
            .stream()
            .map(BundleEntryComponent::getResource)
            .collect(Collectors.toList()),
        resourceClass);
	}

	public <T extends Resource> List<T> getResources(List<Resource> resources, Class<T> resourceClass) {
		return resources.stream()
				.filter(resourceClass::isInstance)
				.map(resourceClass::cast)
				.collect(Collectors.toList());
	}
	
	public static <T extends IBaseResource> T getResource(
			FhirContext ctx,
			String baseUrl,
			Class<T> resourceClass,
			String resourceUrl) {
		IGenericClient client = ctx.newRestfulGenericClient(baseUrl);
    return RetryUtils.retry(() -> client.read()
				.resource(resourceClass)
				.withUrl(resourceUrl)
				.execute(),
				baseUrl);
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
