package uk.nhs.ctp.service;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import uk.nhs.ctp.audit.HttpRequest;
import uk.nhs.ctp.audit.HttpResponse;
import uk.nhs.ctp.entities.Audit;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.entities.AuditEntry.AuditEntryBuilder;
import uk.nhs.ctp.repos.AuditRepository;
import uk.nhs.ctp.repos.CaseRepository;
import uk.nhs.ctp.service.search.AuditSearchRequest;
import uk.nhs.ctp.service.search.AuditSearchResultDTO;
import uk.nhs.ctp.transform.AuditTransformer;

/**
 * Collects an audit of HTTP requests and responses during a single EMS API call
 * <p>
 * Expected sequence of calls:
 *
 * <ul>
 * <li>{@link #startAudit(HttpRequest)}()</li>
 * <li>{@link #setCaseId(Long)}()</li>
 *
 * <li>Repeated several times:
 * <ul>
 * <li>{@link #startEntry(HttpRequest)}</li>
 * <li>{@link #endEntry(HttpResponse)}</li>
 * <li>(maybe {@link #addAuditEntry(HttpRequest, HttpResponse)})</li>
 * </ul></li>
 * <li>{@link #endAudit(HttpRequest, HttpResponse)}</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {

  private final AuditRepository auditRepository;
  private final CaseRepository caseRepository;
  private final AuditTransformer auditTransformer;

  private ThreadLocal<Audit> currentAudit = new ThreadLocal<>();
  private ThreadLocal<AuditEntry> currentEntry = new ThreadLocal<>();

  public Audit getCurrentAudit() {
    Audit audit = currentAudit.get();
    Preconditions.checkState(audit != null, "No active request audit");
    return audit;
  }

  public AuditEntry getCurrentEntry() {
    AuditEntry entry = currentEntry.get();
    Preconditions.checkState(entry != null, "No active request audit entry");
    return entry;
  }

  public Audit startAudit(HttpRequest request) {
    Preconditions.checkState(currentAudit.get() == null, "Unclosed audit");

    Audit audit = Audit.builder()
        .auditEntries(new ArrayList<>())
        .requestUrl(request.getUri())
        .requestMethod(request.getMethod())
        .requestHeaders(request.getHeadersString())
        .build();

    currentAudit.set(audit);
    return audit;
  }

  public void endAudit(HttpRequest request, HttpResponse response) {
    try {
      Preconditions.checkState(currentEntry.get() == null, "Unclosed audit entry");

      Audit audit = getCurrentAudit();
      audit.setRequestBody(request.getBodyString());
      audit.setResponseStatus(response.getStatus());
      audit.setResponseStatusText(response.getStatusText());
      audit.setResponseHeaders(response.getHeadersString());
      audit.setResponseBody(response.getBodyString());

      if (!audit.isStorable() && !audit.getAuditEntries().isEmpty()) {
        log.warn("Request with logged entries but not marked storable: {} {} ",
            audit.getRequestMethod(), audit.getRequestUrl());
      }
      if (audit.isStorable()) {
        auditRepository.saveAndFlush(audit);
      }
    } finally {
      this.currentAudit.remove();
    }
  }

  public Audit setCaseId(Long caseId) {
    Audit audit = getCurrentAudit();
    audit.setCaseId(caseId);

    audit.setStorable(caseId != null);
    return audit;
  }

  public AuditEntry addAuditEntry(HttpRequest request, HttpResponse response) {
    Audit audit = getCurrentAudit();

    AuditEntryBuilder builder = AuditEntry.builder()
        .audit(audit)
        .createdDate(new Date());

    addRequest(builder, request);
    addResponse(builder, response);

    AuditEntry auditEntry = builder.build();
    audit.getAuditEntries().add(auditEntry);

    return auditEntry;
  }

  private AuditEntryBuilder addRequest(AuditEntryBuilder builder, HttpRequest request) {

    builder.requestMethod(request.getMethod())
        .requestUrl(request.getUri())
        .requestHeaders(request.getHeadersString())
        .requestBody(request.getBodyString());

    return builder;
  }

  private AuditEntryBuilder addResponse(AuditEntryBuilder builder, HttpResponse response) {
    builder
        .responseStatus(response.getStatus())
        .responseStatusText(response.getStatusText())
        .responseHeaders(response.getHeadersString())
        .responseBody(response.getBodyString());

    return builder;
  }

  public AuditEntry startEntry(HttpRequest request) {
    Preconditions.checkState(currentEntry.get() == null, "Unclosed audit entry");

    Audit audit = getCurrentAudit();

    AuditEntryBuilder builder = AuditEntry.builder()
        .audit(audit)
        .createdDate(new Date());

    addRequest(builder, request);
    AuditEntry auditEntry = builder.build();

    audit.getAuditEntries().add(auditEntry);

    currentEntry.set(auditEntry);

    return auditEntry;
  }

  public AuditEntry endEntry(HttpResponse response) {
    AuditEntry entry = getCurrentEntry();

    entry.setResponseStatus(response.getStatus());
    entry.setResponseStatusText(response.getStatusText());
    entry.setResponseHeaders(response.getHeadersString());
    entry.setResponseBody(response.getBodyString());

    currentEntry.remove();
    return entry;
  }

  public Page<AuditSearchResultDTO> search(AuditSearchRequest request) {
    return caseRepository.search(
        request.getFrom(), request.getTo(),
        request.isIncludeClosed(), request.isIncludeIncomplete(), request);
  }

}
