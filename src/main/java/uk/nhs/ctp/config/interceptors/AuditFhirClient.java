package uk.nhs.ctp.config.interceptors;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.api.IHttpRequest;
import ca.uhn.fhir.rest.client.api.IHttpResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.audit.HttpRequest;
import uk.nhs.ctp.audit.HttpResponse;

@Component
@RequiredArgsConstructor
public class AuditFhirClient implements IClientInterceptor {

  private final AuditService auditService;

  @Override
  public void interceptRequest(IHttpRequest theRequest) {
    auditService.startEntry(HttpRequest.from(theRequest));
  }

  @Override
  public void interceptResponse(IHttpResponse theResponse) throws IOException {
    theResponse.bufferEntity();
    auditService.endEntry(HttpResponse.from(theResponse));
  }
}
