package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;

@Data
public class CdssResult {

  private List<Resource> outputData;
  private RequestGroup result;
  private String questionnaireRef;
  private String serviceDefinitionId;
  private String switchTrigger;
  private ReferralRequest referralRequest;
  private List<CarePlanDTO> careAdvice;
  private String sessionId;
  private List<Resource> contained;
  private OperationOutcome operationOutcome;

  public boolean hasOutputData() {
    return CollectionUtils.isNotEmpty(this.outputData);
  }

  public boolean hasResult() {
    return this.result != null;
  }

  public boolean hasTrigger() {
    return this.switchTrigger != null;
  }

  public boolean hasQuestionnaire() {
    return this.questionnaireRef != null;
  }

  public boolean isInProgress() {
    return this.hasOutputData() && !this.hasResult() && !this.hasQuestionnaire() && !this
        .hasOperationOutcome();
  }

  private boolean hasOperationOutcome() {
    return this.operationOutcome != null;
  }

  public boolean hasReferralRequest() {
    return this.referralRequest != null;
  }

  public boolean hasCareAdvice() {
    return CollectionUtils.isNotEmpty(this.careAdvice);
  }

}
