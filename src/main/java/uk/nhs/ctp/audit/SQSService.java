package uk.nhs.ctp.audit;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditSession;

@Service
@Slf4j
@RequiredArgsConstructor
public class SQSService {

  private static final String SENDER = "sender";
  private static final String STRING = "String";
  private static final String SUPPLIER = "supplierId";

  @Value("${sqs.audit.queue}")
  private String loggingQueue;

  @Value("${service.name}")
  private String serviceName;

  private final AmazonSQS sqsClient;
  private final TokenAuthenticationService authenticationService;

  public void sendAudit(AuditSession session) {

    var supplierId = authenticationService.getCurrentSupplierId();
    if (StringUtils.isEmpty(loggingQueue) || supplierId.isEmpty()) {
      // SupplierID doesn't exist for /login requests so don't log
      // Nowhere to send audits, log to console for now
      log.info(session.toString());
      return;
    }
    try {
      SendMessageRequest request = new SendMessageRequest()
          .withMessageGroupId(supplierId.get())
          .withMessageDeduplicationId(UUID.randomUUID().toString())
          .addMessageAttributesEntry(SENDER, new MessageAttributeValue()
              .withDataType(STRING)
              .withStringValue(serviceName))
          .addMessageAttributesEntry(SUPPLIER, new MessageAttributeValue()
              .withDataType(STRING)
              .withStringValue(supplierId.get()))
          .withQueueUrl(loggingQueue)
          .withMessageBody(new ObjectMapper().writeValueAsString(session));
      sqsClient.sendMessage(request);
    } catch (Exception e) {
      log.error("an error occurred sending audit session {} to SQS", session, e);
    }
  }

}