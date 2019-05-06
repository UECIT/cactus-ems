package uk.nhs.ctp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.Enumerations.DataType;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.exceptions.FHIRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.parser.IParser;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.SystemURL;
import uk.nhs.ctp.entities.ServiceDefinition;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.CdssSupplierRepository;
import uk.nhs.ctp.repos.ServiceDefinitionRepository;
import uk.nhs.ctp.service.dto.CarePlanDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.utils.ResourceProviderUtils;

/**
 * @author mayt
 *
 */
@Service
public class GuidanceResponseService {

	@Autowired
	private ServiceDefinitionRepository serviceDefRepo;

	@Autowired
	private CdssSupplierRepository cdssSupplierRepo;

	@Autowired
	private CdssService cdssService;
	
	@Autowired
	private IParser fhirParser;
	
	private Map<Class<? extends Resource>, Function<Resource, List<String>>> referenceFunctions;
	
	public GuidanceResponseService() {
		referenceFunctions = new HashMap<>();
		
		referenceFunctions.put(ActivityDefinition.class, (resource) -> {
			List<String> references = new ArrayList<>();
			references.add(((ActivityDefinition)resource).getLibraryFirstRep().getReference());
			return references;
		});
		
		referenceFunctions.put(ReferralRequest.class, (resource) -> {
			List<String> references = new ArrayList<>();
			references.add(((ReferralRequest)resource).getBasedOnFirstRep().getReference());
			references.add(((ReferralRequest)resource).getRelevantHistoryFirstRep().getReference());
			return references;
		});
			
	}

	/**
	 * Builds a summary result from the GuidanceResponse
	 * 
	 * @param guidanceResponse {@link GuidanceResponse}
	 * @param cdssSupplierId
	 * @return {@link CdssResult}
	 * @throws FHIRException
	 */
	public CdssResult processGuidanceResponse(GuidanceResponse guidanceResponse, Long cdssSupplierId, Long caseId) {
		CdssResult cdssResult = new CdssResult();
		getContained(guidanceResponse, cdssSupplierId);
		
		cdssResult.setServiceDefinitionId(guidanceResponse.getModule().getReferenceElement().getIdPart());
		cdssResult.setOutputData(getOutputData(guidanceResponse));
		cdssResult.setSessionId(getSessionID(guidanceResponse));
		
		if (guidanceResponse.getStatus() == GuidanceResponseStatus.SUCCESS) {
			cdssResult.setResult(getResult(guidanceResponse));
			setTrigger(guidanceResponse, cdssResult);
		}

		cdssResult.setContained(guidanceResponse.getContained());
		cdssResult.setReferralRequest(
				ResourceProviderUtils.getResource(guidanceResponse.getContained(), ReferralRequest.class));
		
		cdssResult.setProcedureRequest(
				ResourceProviderUtils.getResource(guidanceResponse.getContained(), ProcedureRequest.class));
		
		cdssResult.setCareAdvice(
				ResourceProviderUtils.getResources(guidanceResponse.getContained(), CarePlan.class)
					.stream().map(plan -> new CarePlanDTO(plan)).collect(Collectors.toList()));
		
		// Add support for data-requested
		if (guidanceResponse.getStatus() == GuidanceResponseStatus.DATAREQUIRED || guidanceResponse.getStatus() == GuidanceResponseStatus.DATAREQUESTED) {
			cdssResult.setQuestionnaireId(getQuestionnaireReference(guidanceResponse));
		}

		return cdssResult;
	}
		
	public void getContained(GuidanceResponse guidanceResponse, Long cdssSupplierId) {
		String baseUrl = cdssSupplierRepo.findOne(cdssSupplierId).getBaseUrl();
		
		if(guidanceResponse.hasResult()) {
			
			RequestGroup requestGroup = new RequestGroup();
			requestGroup = ResourceProviderUtils.getResource(guidanceResponse.getContained(), RequestGroup.class);
			requestGroup = requestGroup == null ? ResourceProviderUtils.getResource(
					baseUrl, RequestGroup.class, guidanceResponse.getResult().getReference()) : requestGroup;
			
			try {
				requestGroup.getAction().stream().forEach(child -> {
					String reference = child.getResource().getReference();
					Class<? extends Resource> resourceClass = ResourceProviderUtils.getResourceType(reference);
					Resource resource = ResourceProviderUtils.getResource(baseUrl, resourceClass, reference);
					
					guidanceResponse.addContained(resource);

					if (referenceFunctions.containsKey(resourceClass)) {
						List<String> childReferences = referenceFunctions.get(resourceClass).apply(resource);
						childReferences.stream().forEach(childReference -> {
							if (childReference != null) {
								guidanceResponse.addContained(
										ResourceProviderUtils.getResource(baseUrl, ResourceProviderUtils.getResourceType(childReference), childReference));
							}
						});
					}
					
				});
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
		if(guidanceResponse.hasOutputParameters()) {
			try {
				Parameters parameters = (Parameters) ResourceProviderUtils.getResource(baseUrl, Parameters.class, guidanceResponse.getOutputParameters().getReference());
				guidanceResponse.addContained(parameters);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	/**
	 * Checks for a "triggerDefinition"in the guidanceResponse and updates the cdssResult.
	 * 
	 * @param guidanceResponse
	 * @param cdssResult
	 */
	private void setTrigger(GuidanceResponse guidanceResponse, CdssResult cdssResult) {
		Optional<DataRequirement> optional = guidanceResponse.getDataRequirement().stream().filter(
				data -> data.getType().equals(DataType.TRIGGERDEFINITION.toCode())).findFirst();
		
		if (optional.isPresent()) {
			for (ServiceDefinition serviceDef : serviceDefRepo.findAll()) {
				org.hl7.fhir.dstu3.model.ServiceDefinition serviceDefinition = 
						cdssService.getServiceDefinition(
								serviceDef.getServiceDefinitionId(), serviceDef.getCdssSupplierId());
				
				DataRequirement triggerData = serviceDefinition.getTrigger().get(0).getEventData();

				if (optional.get().equalsShallow(triggerData)) {
					cdssResult.setSwitchTrigger(serviceDefinition.getId());
					break;
				}
				
			}
		}
	}

	/**
	 * Extracts the output data from the guidance response parameters
	 * 
	 * @param guidanceResponse {@link GuidanceResponse}
	 * @return {@link Resource}
	 */
	public List<Resource> getOutputData(GuidanceResponse guidanceResponse) {
		List<Resource> outputResources = new ArrayList<>();
		
		Parameters parameters = new Parameters();
		if (guidanceResponse.hasOutputParameters() && guidanceResponse.getOutputParameters().getResource() != null) {
			parameters = ResourceProviderUtils
					.castToType(guidanceResponse.getOutputParameters().getResource(), Parameters.class);

		}
		if (guidanceResponse.hasOutputParameters() && guidanceResponse.getOutputParameters().getResource() == null) {
			for (Resource resource : guidanceResponse.getContained()) {
				if (resource instanceof Parameters) {
					parameters = ResourceProviderUtils.castToType(resource, Parameters.class);
				}
			}
		}
		
		// Get extra "Output Parameters" and store them somewhere
		for (ParametersParameterComponent parameter : parameters.getParameter()) {
			if (parameter.getName().equalsIgnoreCase(SystemConstants.OUTPUT_DATA) && parameter.getResource() != null) {
				// if the parameter is outputData and contains a resource
				outputResources.add(parameter.getResource());
			} else {
				// if the parameter is anything else
				Parameters tempParameters = new Parameters();
				tempParameters.addParameter(parameter);
				outputResources.add(tempParameters);
			}
		}
					
		return outputResources;
	}

	public String getSessionID(GuidanceResponse guidanceResponse) {
		try {
			if (guidanceResponse.hasOutputParameters()
					&& guidanceResponse.getOutputParameters().getResource() != null) {
				Parameters parameters = ResourceProviderUtils
						.castToType(guidanceResponse.getOutputParameters().getResource(), Parameters.class);

				ParametersParameterComponent sessionIdParameter = ResourceProviderUtils
						.getParameterByName(parameters.getParameter(), "sessionId");
				return sessionIdParameter.getValue().primitiveValue();
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	/**
	 * Extracts the resulting care plan from the guidance response
	 * 
	 * @param guidanceResponse {@link GuidanceResponse}
	 * @return {@link CarePlan}
	 */
	public RequestGroup getResult(GuidanceResponse guidanceResponse) {
		
		if (guidanceResponse.hasResult() && guidanceResponse.getResult().getResource() != null) {
			// get RequestGroup resource out of the result
			RequestGroup requestGroup = ResourceProviderUtils.castToType(guidanceResponse.getResult().getResource(),
					RequestGroup.class);
			return requestGroup;
		} else if(guidanceResponse.hasResult()) {
			RequestGroup requestGroup = new RequestGroup();
			for (Resource resource : guidanceResponse.getContained()) {
				if (resource instanceof ReferralRequest) {
					ReferralRequest referralRequest = ResourceProviderUtils.castToType(resource, ReferralRequest.class);
					requestGroup.addAction().setResource(new Reference(referralRequest));
				}
				if (resource instanceof CarePlan) {
					CarePlan carePlan = ResourceProviderUtils.castToType(resource, CarePlan.class);
					requestGroup.addAction().setResource(new Reference(carePlan));
				}
			}
			return requestGroup;
		}
		return null;
	}

	/**
	 * Extracts the questionnaire reference from the GuidanceResponse
	 * 
	 * @param guidanceResponse {@link GuidanceResponse}
	 * @return {@link String}
	 */
	public String getQuestionnaireReference(GuidanceResponse guidanceResponse) {
		if (guidanceResponse.hasDataRequirement()) {
			List<Extension> questionnaireExtensions = guidanceResponse.getDataRequirementFirstRep()
					.getExtensionsByUrl(SystemURL.QUESTIONNAIRE);
			if (questionnaireExtensions != null && !questionnaireExtensions.isEmpty()) {
				Reference questionnaireRef = ResourceProviderUtils.castToType(questionnaireExtensions.get(0).getValue(),
						Reference.class);
				return questionnaireRef.getReference();
			}
		} else if (guidanceResponse.getStatus().equals(GuidanceResponseStatus.DATAREQUIRED)) {
			throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, 
					"Invalid guidance response: " + fhirParser.encodeResourceToString(guidanceResponse));
		}
		return null;
	}

}
