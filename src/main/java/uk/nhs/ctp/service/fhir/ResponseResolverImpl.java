package uk.nhs.ctp.service.fhir;

import static uk.nhs.ctp.utils.ResourceProviderUtils.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.Base;
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
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.service.dto.CarePlanDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.SettingsDTO;

@AllArgsConstructor
@Component
public class ResponseResolverImpl implements ResponseResolver {

  private ResourceExtractor resourceExtractor;
  private FhirContext fhirContext;
  private SwitchTriggerResolver switchTriggerResolver;
  private ReferenceService referenceService;

  @Override
  public CdssResult resolve(Resource resource, CdssSupplier cdssSupplier, SettingsDTO settings,
      String patientId) {
    GuidanceResponse guidanceResponse = resourceExtractor.extractGuidanceResponse(resource);
    referenceService.resolveRelative(cdssSupplier.getBaseUrl(), guidanceResponse);

    List<Resource> extractedResources = resourceExtractor.extractResources(resource, cdssSupplier);

    CdssResult cdssResult = new CdssResult();
    extractedResources.forEach(child -> addContained(child, cdssSupplier, guidanceResponse));

    cdssResult.setOutputData(getOutputData(guidanceResponse));
    cdssResult.setSessionId(getSessionID(guidanceResponse));
    cdssResult.setContained(guidanceResponse.getContained());
    cdssResult
        .setServiceDefinitionId(guidanceResponse.getModule().getReferenceElement().getIdPart());

    switch (guidanceResponse.getStatus()) {
      case SUCCESS:
        cdssResult.setResult(getResult(guidanceResponse));
        cdssResult.setSwitchTrigger(
            switchTriggerResolver.getSwitchTrigger(guidanceResponse, settings, patientId));
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
        throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error parsing guidance response status: " + guidanceResponse.getStatus());
    }

    cdssResult.setReferralRequest(
        getResource(guidanceResponse.getContained(), ReferralRequest.class));

    cdssResult.setCareAdvice(
        getResources(guidanceResponse.getContained(), CareConnectCarePlan.class)
            .stream().map(CarePlanDTO::new).collect(Collectors.toList()));

    return cdssResult;
  }

  private void addContained(Resource resource, CdssSupplier cdssSupplier,
      GuidanceResponse guidanceResponse) {
    guidanceResponse.addContained(resource);
    List<String> references;
    if (resource instanceof ActivityDefinition) {
      var activityDefinition = (ActivityDefinition) resource;
      references = singletonList((activityDefinition).getLibraryFirstRep().getReference());
    } else if (resource instanceof ReferralRequest) {
      var referralRequest = (ReferralRequest) resource;
      references = asList(
          referralRequest.getBasedOnFirstRep().getReference(),
          referralRequest.getRelevantHistoryFirstRep().getReference()
      );
    } else {
      return;
    }

    references.forEach(childReference -> {
      if (childReference != null) {
        guidanceResponse.addContained(
            getResource(fhirContext,
            cdssSupplier.getBaseUrl(),
            getResourceType(childReference),
            childReference));
      }
    });
  }

  private Parameters getOutputData(GuidanceResponse guidanceResponse) {
    // TODO: outputParameters could be a reference
    // TODO: things inside it could be references too
    var parameters = (Parameters) guidanceResponse.getOutputParameters().getResource();
    if (parameters == null) {
      return new Parameters();
    }

    return parameters;
  }

  public String getSessionID(GuidanceResponse guidanceResponse) {
    return Optional.ofNullable(guidanceResponse.getOutputParameters().getResource())
        .map(parameters -> castToType(parameters, Parameters.class))
        .map(Parameters::getParameter)
        .map(parameters -> getParameterByName(parameters, "sessionId"))
        .map(ParametersParameterComponent::getValue)
        .map(Base::primitiveValue)
        .orElse(null);
  }

  @Override
  public RequestGroup getResult(GuidanceResponse guidanceResponse) {

    if (guidanceResponse.hasResult() && guidanceResponse.getResult().getResource() != null) {
      // get RequestGroup resource out of the result
      return castToType(guidanceResponse.getResult().getResource(), RequestGroup.class);
    }

    if (guidanceResponse.hasResult()) {
      RequestGroup requestGroup = new RequestGroup();
      for (Resource resource : guidanceResponse.getContained()) {
        if (resource instanceof ReferralRequest) {
          ReferralRequest referralRequest = castToType(resource, ReferralRequest.class);
          requestGroup.addAction().setResource(new Reference(referralRequest));
        }
        if (resource instanceof CarePlan) {
          CarePlan carePlan = castToType(resource, CarePlan.class);
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
      } else if (requirement.getValueSetReference() != null) {
        return requirement.getValueSetReference().getReference();
      }

    } else if (guidanceResponse.getStatus().equals(GuidanceResponseStatus.DATAREQUIRED)) {
      IParser fhirParser = fhirContext.newJsonParser();
      throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR,
          "Invalid guidance response: " + fhirParser.encodeResourceToString(guidanceResponse));
    }
    return null;
  }

}
