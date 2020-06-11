package uk.nhs.ctp.audit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import uk.nhs.cactus.common.security.TokenAuthenticationService;
import uk.nhs.ctp.audit.model.AuditEntry;
import uk.nhs.ctp.audit.model.AuditSession;

@RunWith(MockitoJUnitRunner.class)
public class SQSServiceTest {

  @InjectMocks
  private SQSService sqsService;

  @Mock
  private ObjectMapper mockMapper;

  @Mock
  private AmazonSQSClient mockSqs;

  @Mock
  private TokenAuthenticationService mockAuthService;

  @Test
  public void shouldNotSendIfNoQueue() {
    ReflectionTestUtils.setField(sqsService, "loggingQueue", null);
    when(mockAuthService.requireSupplierId())
        .thenReturn("mocksupplierid");

    sqsService.sendAudit(AuditSession.builder().build());

    verifyZeroInteractions(mockSqs);
  }

  @Test
  public void shouldNotSendIfNoSession() {
    ReflectionTestUtils.setField(sqsService, "loggingQueue", null);
    when(mockAuthService.getCurrentSupplierId())
        .thenReturn(Optional.empty());

    sqsService.sendAudit(AuditSession.builder().build());

    verifyZeroInteractions(mockSqs);
  }

  @Test
  public void shouldSendAuditSessionToSqs() throws Exception {
    ReflectionTestUtils.setField(sqsService, "loggingQueue", "mock.queue");
    ReflectionTestUtils.setField(sqsService, "serviceName", "cdss");
    when(mockAuthService.getCurrentSupplierId())
        .thenReturn(Optional.of("mocksupplierid"));

    AuditSession session = testSession();
    when(mockMapper.writeValueAsString(session))
        .thenReturn("testMessageBody");

    sqsService.sendAudit(session);

    var captor = ArgumentCaptor.forClass(SendMessageRequest.class);
    verify(mockSqs).sendMessage(captor.capture());

    SendMessageRequest actual = captor.getValue();
    assertThat(actual.getMessageBody(), is("testMessageBody"));
    assertThat(actual.getMessageGroupId(), is("mocksupplierid"));
    assertThat(actual.getQueueUrl(), is("mock.queue"));
    assertThat(actual.getMessageAttributes(),
        hasEntry("sender", new MessageAttributeValue()
            .withDataType("String")
            .withStringValue("cdss")));
    assertThat(actual.getMessageDeduplicationId(), notNullValue());
  }

  private AuditSession testSession() {
    return AuditSession.builder()
        .createdDate(Instant.now())
        .requestMethod("GET")
        .requestOrigin("origin")
        .requestBody("a body")
        .responseBody("a response")
        .responseHeaders("these are response headers")
        .responseStatus("200")
        .entries(Collections.singletonList(
            AuditEntry.builder()
                .dateOfEntry(Instant.now())
                .requestHeaders("some headers")
                .requestUrl("a url")
                .build()
        ))
        .build();
  }

}