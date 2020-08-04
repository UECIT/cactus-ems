package uk.nhs.ctp.service.attachment;

import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.google.common.base.Preconditions;
import java.net.URI;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AttachmentService {

  private Logger log = LoggerFactory.getLogger(getClass());

  @Value("${blob.server}")
  private String blobServer;

  private final RestTemplate blobRestTemplate;

  public Attachment storeAttachment(MediaType contentType, byte[] data) {
    log.info("Storing attachment to server {}", blobServer);
    LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add(HttpHeaders.CONTENT_TYPE, contentType.toString());

    HttpEntity<byte[]> body = new HttpEntity<>(data, headers);
    ResponseEntity<String> response = blobRestTemplate
        .exchange(blobServer, HttpMethod.PUT, body, String.class);

    if (response.getStatusCode() != HttpStatus.CREATED) {
      throw new InternalErrorException(
          "Storing attachment: Unexpected response status: " + response.getStatusCode());
    }
    URI location = response.getHeaders().getLocation();
    String digestHeader = response.getHeaders().getFirst("Digest");
    byte[] digest = DigestUtils.sha1(data);
    Preconditions
        .checkState(formatDigestHeader(digest).equals(digestHeader), "Invalid digest: " + digestHeader);
    log.info("Successfully stored attachment with digest {}", digestHeader);

    return new Attachment()
        .setContentType(contentType.toString())
        .setUrl(URI.create(blobServer).resolve(location).toString())
        .setCreation(new Date())
        .setHash(digest);
  }

  public AttachmentData fetchAttachment(Attachment attachment) {
    if (StringUtils.isNotEmpty(attachment.getUrl())) {
      log.info("Fetching attachment from {}", attachment.getUrl());
      ResponseEntity<byte[]> response = blobRestTemplate
          .getForEntity(attachment.getUrl(), byte[].class);

      if (!response.getStatusCode().is2xxSuccessful()) {
        throw new InternalErrorException(
            "Storing attachment: Unexpected response status: " + response.getStatusCode());
      }

      MediaType contentType = response.getHeaders().getContentType();
      String digest = response.getHeaders().getFirst("Digest");
      if (ArrayUtils.isNotEmpty(attachment.getHash())) {
        Preconditions
            .checkState(formatDigestHeader(attachment.getHash()).equals(digest), "Invalid Digest: " + digest);
      }
      log.info("Loaded attachment data with digest {}", digest);

      byte[] data = response.getBody();
      return new AttachmentData(contentType, data,
          ObjectUtils.defaultIfNull(attachment.getHash(), DigestUtils.sha1(data)));
    } else {
      byte[] data = attachment.getData();
      if (ArrayUtils.isNotEmpty(data)) {
        return new AttachmentData(
            MediaType.valueOf(attachment.getContentType()), data,
            ObjectUtils.defaultIfNull(attachment.getHash(), DigestUtils.sha1(data)));
      } else {
        throw new IllegalStateException("No attachment data");
      }
    }
  }

  private String formatDigestHeader(byte[] hash) {
    return String.format("SHA=%s", Base64.encodeBase64URLSafeString(hash));
  }
}
