package uk.nhs.ctp.service.fhir;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.hl7.fhir.dstu3.model.ActivityDefinition;
import org.hl7.fhir.dstu3.model.CarePlan;
import org.hl7.fhir.dstu3.model.DataRequirement.DataRequirementCodeFilterComponent;
import org.hl7.fhir.dstu3.model.GuidanceResponse;
import org.hl7.fhir.dstu3.model.GuidanceResponse.GuidanceResponseStatus;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import org.hl7.fhir.dstu3.model.ResourceType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.CdssSupplier;
import uk.nhs.ctp.exception.EMSException;
import uk.nhs.ctp.service.dto.CarePlanDTO;
import uk.nhs.ctp.service.dto.CdssResult;
import uk.nhs.ctp.service.dto.SettingsDTO;
import uk.nhs.ctp.utils.ResourceProviderUtils;

@AllArgsConstructor
@Component
public class ResponseResolverImpl implements ResponseResolver {

  private GuidanceResponseResourceExtractor resourceExtractor;
  private FhirContext fhirContext;
  private SwitchTriggerResolver switchTriggerResolver;
  private ReferenceService referenceService;

  @Override
  public CdssResult resolve(GuidanceResponse guidanceResponse, CdssSupplier cdssSupplier, SettingsDTO settings,
      String patientId) {
    referenceService.resolveRelative(cdssSupplier.getBaseUrl(), guidanceResponse);

    List<Resource> extractedResources = resourceExtractor.extractResources(guidanceResponse, cdssSupplier);

    CdssResult cdssResult = new CdssResult();
    extractedResources.forEach(child -> addContained(child, cdssSupplier, guidanceResponse));

    cdssResult.setRequestId(guidanceResponse.getRequestId());
    cdssResult.setOutputData(getOutputData(guidanceResponse));
    cdssResult.setContained(guidanceResponse.getContained());
    cdssResult.setServiceDefinitionId(
        guidanceResponse.getModule().getReferenceElement().getIdPart());

    switch (guidanceResponse.getStatus()) {
      case SUCCESS:
        cdssResult.setResult(getResult(guidanceResponse));
        cdssResult.setSwitchTrigger(
            switchTriggerResolver.getSwitchTrigger(guidanceResponse, settings, patientId));
        break;
      case DATAREQUESTED:
      case DATAREQUIRED:
        var questionnaireRef = referenceService.buildId(
            cdssSupplier.getBaseUrl(),
            ResourceType.Questionnaire,
            getQuestionnaireId(guidanceResponse));
        cdssResult.setQuestionnaireRef(questionnaireRef);
        break;
      case FAILURE:
        cdssResult.setOperationOutcome(ResourceProviderUtils.getResource(fhirContext,
            cdssSupplier.getBaseUrl(), OperationOutcome.class,
            guidanceResponse.getEvaluationMessageFirstRep().getReference()));
        break;
      default:
        throw new EMSException(HttpStatus.INTERNAL_SERVER_ERROR,
            "Error parsing guidance response status: " + guidanceResponse.getStatus());
    }

    cdssResult.setReferralRequest(
        ResourceProviderUtils.getResource(guidanceResponse.getContained(), ReferralRequest.class));

    cdssResult.setCareAdvice(
        ResourceProviderUtils.getResources(guidanceResponse.getContained(), CarePlan.class)
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
            ResourceProviderUtils.getResource(fhirContext,
                cdssSupplier.getBaseUrl(),
                ResourceProviderUtils.getResourceType(childReference),
                childReference));
      }
    });
  }

  private Parameters getOutputData(GuidanceResponse guidanceResponse) {
    var parameters = (Parameters) guidanceResponse.getOutputParameters().getResource();
    return parameters == null ? new Parameters() : parameters;
  }

  @Override
  public RequestGroup getResult(GuidanceResponse guidanceResponse) {

    if (guidanceResponse.hasResult() && guidanceResponse.getResult().getResource() != null) {
      // get RequestGroup resource out of the result
      return ResourceProviderUtils
          .castToType(guidanceResponse.getResult().getResource(), RequestGroup.class);
    }

    if (guidanceResponse.hasResult()) {
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
  public String getQuestionnaireId(GuidanceResponse guidanceResponse) {

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
