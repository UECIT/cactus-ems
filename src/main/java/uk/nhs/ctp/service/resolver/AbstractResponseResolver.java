package uk.nhs.ctp.service.resolver;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DataRequirement;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.repos.ServiceDefinitionRepository;
import uk.nhs.ctp.service.CdssService;
import uk.nhs.ctp.service.CdssSupplierService;
import uk.nhs.ctp.service.dto.CarePlanDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.CdssSupplierDTO;
import uk.nhs.ctp.service.search.SearchParameters;
import uk.nhs.ctp.utils.ResourceProviderUtils;

public abstract class AbstractResponseResolver<RESOURCE extends Resource> implements
    ResponseResolver<RESOURCE> {

  @Autowired
  private CdssService cdssService;

  @Autowired
  private CdssSupplierService cdssSupplierService;

  @Autowired
  private ServiceDefinitionRepository serviceDefinitionRepository;

  @Autowired
  private IParser fhirParser;

  @Autowired
  private FhirContext fhirContext;

  private Map<Class<? extends Resource>, Function<Resource, List<String>>> referenceFunctions;

  public AbstractResponseResolver() {
    referenceFunctions = new HashMap<>();

    referenceFunctions.put(ActivityDefinition.class, (resource) -> {
      List<String> references = new ArrayList<>();
      references.add(((ActivityDefinition) resource).getLibraryFirstRep().getReference());
      return references;
    });

    referenceFunctions.put(ReferralRequest.class, (resource) -> {
      List<String> references = new ArrayList<>();
      references.add(((ReferralRequest) resource).getBasedOnFirstRep().getReference());
      references.add(((ReferralRequest) resource).getRelevantHistoryFirstRep().getReference());
      return references;
    });

  }

  public CdssResult resolve(Resource resource, CdssSupplier cdssSupplier) {
    GuidanceResponse guidanceResponse = extractGuidanceResponse(resource);
    List<Resource> extractedResources = extractResources(resource, cdssSupplier);

    CdssResult cdssResult = new CdssResult();
    extractedResources.stream()
        .forEach(child -> addContained(child, cdssSupplier, guidanceResponse));

    cdssResult.setOutputData(getOutputData(guidanceResponse));
    cdssResult.setSessionId(getSessionID(guidanceResponse));
    cdssResult.setContained(guidanceResponse.getContained());
    cdssResult
        .setServiceDefinitionId(guidanceResponse.getModule().getReferenceElement().getIdPart());

    if (guidanceResponse.getStatus() == GuidanceResponseStatus.SUCCESS) {
      cdssResult.setResult(getResult(guidanceResponse));
      cdssResult.setSwitchTrigger(getTrigger(guidanceResponse));
    }

    cdssResult.setReferralRequest(
        ResourceProviderUtils.getResource(guidanceResponse.getContained(), ReferralRequest.class));

    cdssResult.setProcedureRequest(
        ResourceProviderUtils.getResource(guidanceResponse.getContained(), ProcedureRequest.class));

    cdssResult.setCareAdvice(
        ResourceProviderUtils
            .getResources(guidanceResponse.getContained(), CareConnectCarePlan.class)
            .stream().map(plan -> new CarePlanDTO(plan)).collect(Collectors.toList()));

    // Add support for data-requested
    if (guidanceResponse.getStatus() == GuidanceResponseStatus.DATAREQUIRED
        || guidanceResponse.getStatus() == GuidanceResponseStatus.DATAREQUESTED) {
      cdssResult.setQuestionnaireId(getQuestionnaireReference(guidanceResponse));
    }

    return cdssResult;
  }

  private void addContained(Resource resource, CdssSupplier cdssSupplier,
      GuidanceResponse guidanceResponse) {
    guidanceResponse.addContained(resource);

    try {
      if (referenceFunctions.containsKey(resource.getClass())) {
        List<String> childReferences = referenceFunctions.get(resource.getClass()).apply(resource);
        childReferences.stream().forEach(childReference -> {
          if (childReference != null) {
            guidanceResponse.addContained(ResourceProviderUtils.getResource(fhirContext,
                cdssSupplier.getBaseUrl(), ResourceProviderUtils.getResourceType(childReference),
                childReference));
          }
        });
      }
    } catch (Exception e) {
    }
  }

  private List<Resource> getOutputData(GuidanceResponse guidanceResponse) {
    List<Resource> outputResources = new ArrayList<>();

    Parameters parameters = new Parameters();
    if (guidanceResponse.hasOutputParameters()
        && guidanceResponse.getOutputParameters().getResource() != null) {
      parameters = ResourceProviderUtils
          .castToType(guidanceResponse.getOutputParameters().getResource(), Parameters.class);

    }
    if (guidanceResponse.hasOutputParameters()
        && guidanceResponse.getOutputParameters().getResource() == null) {
      for (Resource resource : guidanceResponse.getContained()) {
        if (resource instanceof Parameters) {
          parameters = ResourceProviderUtils.castToType(resource, Parameters.class);
        }
      }
    }

    // Get extra "Output Parameters" and store them somewhere
    for (ParametersParameterComponent parameter : parameters.getParameter()) {
      if (parameter.getName().equalsIgnoreCase(SystemConstants.OUTPUT_DATA)
          && parameter.getResource() != null) {
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

  public RequestGroup getResult(GuidanceResponse guidanceResponse) {

    if (guidanceResponse.hasResult() && guidanceResponse.getResult().getResource() != null) {
      // get RequestGroup resource out of the result
      RequestGroup requestGroup = ResourceProviderUtils
          .castToType(guidanceResponse.getResult().getResource(),
              RequestGroup.class);
      return requestGroup;
    } else if (guidanceResponse.hasResult()) {
      RequestGroup requestGroup = new RequestGroup();
      for (Resource resource : guidanceResponse.getContained()) {
        if (resource instanceof ReferralRequest) {
          ReferralRequest referralRequest = ResourceProviderUtils
              .castToType(resource, ReferralRequest.class);
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

  private String getTrigger(GuidanceResponse guidanceResponse) {
    Optional<DataRequirement> optional = guidanceResponse.getDataRequirement().stream().filter(
        data -> data.getType().equals("CareConnectObservation")).findFirst();

    if (optional.isPresent()) {
      List<String> triggerCodes = optional.get().getCodeFilter().stream()
          .map(filter -> "CareConnectObservation$" + filter.getValueCodingFirstRep().getCode())
          .collect(Collectors.toList());

      List<CdssSupplierDTO> serviceDefinitionBySupplier = cdssService
          .queryServiceDefinitions(SearchParameters.builder()
              .query("triage")
              .typeCode(triggerCodes)
              .build());

      Optional<String> matchedService = serviceDefinitionBySupplier
          .stream()
          .findFirst()
          .flatMap(supplier -> supplier.getServiceDefinitions().stream()
              .findFirst()
              .map(sd -> supplier.getId() + "/" + sd.getServiceDefinitionId())
          );

      return matchedService.orElse(null);
    }

    return null;
  }

  public String getQuestionnaireReference(GuidanceResponse guidanceResponse) {
    if (guidanceResponse.hasDataRequirement()) {

      try {
        return guidanceResponse.getDataRequirementFirstRep()
            .getCodeFilterFirstRep().getValueSetStringType().getValueAsString();
      } catch (Exception e) {
        e.printStackTrace();
      }

      try {
        return guidanceResponse.getDataRequirementFirstRep()
            .getCodeFilterFirstRep().getValueSetReference().getReference();
      } catch (Exception e) {
        e.printStackTrace();
      }

    } else if (guidanceResponse.getStatus().equals(GuidanceResponseStatus.DATAREQUIRED)) {
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Invalid guidance response: " + fhirParser.encodeResourceToString(guidanceResponse));
    }
    return null;
  }

  public abstract Class<RESOURCE> getResourceClass();

  protected abstract GuidanceResponse extractGuidanceResponse(Resource resource);

  protected abstract List<Resource> extractResources(Resource resource, CdssSupplier cdssSupplier);

}
