package uk.nhs.ctp.audit.sqs;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditSession;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("!dev")
public class AWSAuditSender implements AuditSender {

  private static final String SENDER = "sender";
  private static final String SUPPLIER = "supplierId";

  @Value("${sqs.audit.queue}")
  private String loggingQueue;

  @Value("${service.name}")
  private String serviceName;

  private final ObjectMapper mapper;
  private final AmazonSQS sqsClient;
  private final TokenAuthenticationService authenticationService;

  @Override
  public void sendAudit(AuditSession session) {
    Preconditions.checkArgument(isNotEmpty(loggingQueue), "SQS Queue url must be provided");

    var supplierId = authenticationService.getCurrentSupplierId();

    if (supplierId.isEmpty()) {
      log.warn("No supplier id, not sending audit");
      return;
    }

    try {
      SendMessageRequest request = new SendMessageRequest()
          .withMessageGroupId(supplierId.get())
          .withMessageDeduplicationId(UUID.randomUUID().toString())
          .addMessageAttributesEntry(SENDER, stringAttribute(serviceName))
          .addMessageAttributesEntry(SUPPLIER, stringAttribute(supplierId.get()))
          .withQueueUrl(loggingQueue)
          .withMessageBody(mapper.writeValueAsString(session));
      sqsClient.sendMessage(request);
    } catch (AmazonSQSException e) {
      if (e.getStatusCode() == 413) {
        log.warn("Audit request exceeded max size SQS can handle", e);
        //TODO: CDSCT-338 - Should we be auditing/paging audit search audits or excluding things
      }
      log.error("an error occurred sending audit session {} to SQS", format(session), e);
    } catch (Exception e) {
      log.error("an error occurred sending audit session {} to SQS", format(session), e);
    }
  }

  private String format(AuditSession session) {
    String sessionString = session.toString();
    if (sessionString.length() < 1 << 10) {
      return sessionString;
    }

    return String.format("%s... (truncated from %d characters)",
        sessionString.substring(0, 1 << 10), sessionString.length());
  }

  private MessageAttributeValue stringAttribute(String value) {
    return new MessageAttributeValue()
        .withDataType("String")
        .withStringValue(value);
  }
}
