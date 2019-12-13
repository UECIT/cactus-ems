package uk.nhs.ctp.service;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.hl7.fhir.dstu3.model.Attachment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import uk.nhs.ctp.service.attachment.AttachmentData;
import uk.nhs.ctp.service.attachment.AttachmentService;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AttachmentService.class, AttachmentServiceTest.Config.class})
@TestPropertySource(properties = {"blob.server=http://blob.server/blob"})
public class AttachmentServiceTest {

  @TestConfiguration
  public static class Config {

    @Bean
    public RestTemplate restTemplate() {
      return new RestTemplate();
    }
  }

  @Autowired
  private AttachmentService attachmentService;

  @Autowired
  private RestTemplate blobRestTemplate;

  private MockRestServiceServer mockServer;
  private ObjectMapper mapper = new ObjectMapper();

  @Before
  public void init() {
    mockServer = MockRestServiceServer.createServer(blobRestTemplate);
  }

  @Test
  public void storeAttachment() {
    byte[] data = "Test attachment".getBytes();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Digest", "SHA=" + Base64.encodeBase64URLSafeString(DigestUtils.sha1(data)));
    headers.add(HttpHeaders.LOCATION, "/blob/new_blob");

    mockServer.expect(ExpectedCount.once(),
        requestTo("http://blob.server/blob"))
        .andExpect(method(HttpMethod.PUT))
        .andRespond(withStatus(HttpStatus.CREATED)
            .contentType(MediaType.TEXT_PLAIN)
            .headers(headers)
            .body("")
        );

    Attachment attachment = attachmentService.storeAttachment(MediaType.TEXT_PLAIN, data);

    Assert.assertArrayEquals(DigestUtils.sha1(data), attachment.getHash());
    Assert.assertEquals("http://blob.server/blob/new_blob", attachment.getUrl());
  }

  @Test
  public void fetchAttachment() {
    byte[] data = "Test attachment".getBytes();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Digest", "SHA=" + Base64.encodeBase64URLSafeString(DigestUtils.sha1(data)));

    mockServer.expect(ExpectedCount.once(),
        requestTo("http://blob.server/blob/new_blob"))
        .andExpect(method(HttpMethod.GET))
        .andRespond(withStatus(HttpStatus.OK)
            .contentType(MediaType.TEXT_PLAIN)
            .headers(headers)
            .body(data)
        );

    Attachment attachment = new Attachment()
        .setContentType("text/plain")
        .setUrl("http://blob.server/blob/new_blob");

    AttachmentData fetchedData = attachmentService.fetchAttachment(attachment);

    Assert.assertArrayEquals(DigestUtils.sha1(data), fetchedData.getDigest());
    Assert.assertArrayEquals(data, fetchedData.getData());
  }
}
