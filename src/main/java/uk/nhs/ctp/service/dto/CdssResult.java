package uk.nhs.ctp.service.dto;

import java.util.List;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.dstu3.model.Parameters;
import org.hl7.fhir.dstu3.model.ReferralRequest;
import org.hl7.fhir.dstu3.model.RequestGroup;
import org.hl7.fhir.dstu3.model.Resource;
import uk.nhs.ctp.enums.CdsApiVersion;

@Data
public class CdssResult {

  private Parameters outputData;
  private RequestGroup result;
  private String questionnaireRef;
  private String serviceDefinitionId;
  private String switchTrigger;
  private ReferralRequest referralRequest;
  private List<CarePlanDTO> careAdvice;
  private List<Resource> contained;
  private OperationOutcome operationOutcome;

  private String requestId;
  private CdsApiVersion apiVersion;

  public boolean hasOutputData() {
    return this.outputData != null && !this.outputData.getParameter().isEmpty();
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

  public boolean hasReferralRequest() {
    return this.referralRequest != null;
  }

  public boolean hasCareAdvice() {
    return CollectionUtils.isNotEmpty(this.careAdvice);
  }

}
