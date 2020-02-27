package uk.nhs.ctp.config.interceptors;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import uk.nhs.ctp.service.AuditService;
import uk.nhs.ctp.audit.HttpRequest;
import uk.nhs.ctp.audit.HttpResponse;

@Component
@RequiredArgsConstructor
public class AuditFhirServer extends OncePerRequestFilter {

  private static final int CONTENT_CACHE_LIMIT = 1 << 20;

  private final AuditService auditService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    ContentCachingRequestWrapper requestWrapper;
    ContentCachingResponseWrapper responseWrapper;

    if (request instanceof ContentCachingRequestWrapper) {
      requestWrapper = (ContentCachingRequestWrapper) request;
    } else {
      requestWrapper = new ContentCachingRequestWrapper(request, CONTENT_CACHE_LIMIT);
    }

    if (response instanceof ContentCachingResponseWrapper) {
      responseWrapper = (ContentCachingResponseWrapper) response;
    } else {
      responseWrapper = new ContentCachingResponseWrapper(response);
    }

    auditService.startAudit(HttpRequest.from(requestWrapper));

    try {
      filterChain.doFilter(requestWrapper, responseWrapper);
      responseWrapper.copyBodyToResponse();
    } finally {
      auditService.endAudit(HttpRequest.from(requestWrapper), HttpResponse.from(responseWrapper));
    }
  }
}
