package uk.nhs.ctp.service.attachment;

import lombok.Value;
import org.springframework.http.MediaType;

@Value
public class AttachmentData {
  MediaType contentType;
  byte[] data;
  byte[] digest;
}
