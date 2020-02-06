package uk.nhs.ctp.service.resolver;

import static uk.nhs.ctp.utils.ResourceProviderUtils.getResource;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.CareConnectCarePlan;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Parameters.ParametersParameterComponent;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.SystemConstants;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.service.dto.CarePlanDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@AllArgsConstructor
@Component
public class ResponseResolverImpl implements ResponseResolver {

  private ResourceExtractor resourceExtractor;
  private IParser fhirParser;
  private FhirContext fhirContext;
  private SwitchTriggerResolver switchTriggerResolver;

  @Override
  public CdssResult resolve(Resource resource, CdssSupplier cdssSupplier, SettingsDTO settings, String patientId) {
    GuidanceResponse guidanceResponse = resourceExtractor.extractGuidanceResponse(resource);
    List<Resource> extractedResources = resourceExtractor.extractResources(resource, cdssSupplier);

    CdssResult cdssResult = new CdssResult();
    extractedResources
        .forEach(child -> addContained(child, cdssSupplier, guidanceResponse));

    cdssResult.setOutputData(getOutputData(guidanceResponse));
    cdssResult.setSessionId(getSessionID(guidanceResponse));
    cdssResult.setContained(guidanceResponse.getContained());
    cdssResult.setServiceDefinitionId(guidanceResponse.getModule().getReferenceElement().getIdPart());

    switch (guidanceResponse.getStatus()) {
      case SUCCESS:
        cdssResult.setResult(getResult(guidanceResponse));
        cdssResult.setSwitchTrigger(switchTriggerResolver.getSwitchTrigger(guidanceResponse, settings, patientId));
        break;
      case DATAREQUESTED:
      case DATAREQUIRED:
        cdssResult.setQuestionnaireRef(getQuestionnaireReference(guidanceResponse));
        break;
      case FAILURE:
        cdssResult.setOperationOutcome(getResource(fhirContext,
            cdssSupplier.getBaseUrl(), OperationOutcome.class,
            guidanceResponse.getEvaluationMessageFirstRep().getReference()));
        break;
      default:
        throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR, "Error parsing guidance response status: " + guidanceResponse.getStatus());
    }

    cdssResult.setReferralRequest(
        getResource(guidanceResponse.getContained(), ReferralRequest.class));

    cdssResult.setCareAdvice(
        ResourceProviderUtils
            .getResources(guidanceResponse.getContained(), CareConnectCarePlan.class)
            .stream().map(CarePlanDTO::new).collect(Collectors.toList()));

    return cdssResult;
  }

  private void addContained(Resource resource, CdssSupplier cdssSupplier,
      GuidanceResponse guidanceResponse) {
    guidanceResponse.addContained(resource);
    List<String> references;
    if (resource instanceof ActivityDefinition) {
      ActivityDefinition activityDefinition = (ActivityDefinition) resource;
      references = Collections.singletonList((activityDefinition).getLibraryFirstRep().getReference());
    }
    else if (resource instanceof ReferralRequest) {
      ReferralRequest referralRequest = (ReferralRequest) resource;
      references = Arrays.asList(
          referralRequest.getBasedOnFirstRep().getReference(),
          referralRequest.getRelevantHistoryFirstRep().getReference()
      );
    }
    else {
      return;
    }

    references.forEach(childReference -> {
      if (childReference != null) {
        guidanceResponse.addContained(getResource(fhirContext,
            cdssSupplier.getBaseUrl(), ResourceProviderUtils.getResourceType(childReference),
            childReference));
      }
    });
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

  @Override
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

  @Override
  public String getQuestionnaireReference(GuidanceResponse guidanceResponse) {

    if (guidanceResponse.hasDataRequirement()) {
      DataRequirementCodeFilterComponent requirement = guidanceResponse
          .getDataRequirementFirstRep()
          .getCodeFilterFirstRep();

      if (requirement.getValueSetStringType() != null) {
        return requirement.getValueSetStringType().getValueAsString();
      }
      else if (requirement.getValueSetReference() != null) {
        return requirement.getValueSetReference().getReference();
      }

    } else if (guidanceResponse.getStatus().equals(GuidanceResponseStatus.DATAREQUIRED)) {
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Invalid guidance response: " + fhirParser.encodeResourceToString(guidanceResponse));
    }
    return null;
  }

}
