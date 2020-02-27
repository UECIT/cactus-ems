package uk.nhs.ctp.transform;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.nhs.ctp.entities.Audit;
import uk.nhs.ctp.entities.AuditEntry;
import uk.nhs.ctp.audit.HttpAudit;

@Component
public class AuditTransformer implements Transformer<Audit, String> {

  @Override
  public String transform(Audit audit) {
    StringBuilder sb = new StringBuilder();

    writeHttpAudit(sb, audit);

    for (AuditEntry entry : audit.getAuditEntries()) {
      writeHttpAudit(sb, entry);
    }
    return sb.toString();
  }

  private void writeHttpAudit(StringBuilder sb, HttpAudit audit) {
    writeRequest(sb, audit.getRequestMethod(), audit.getRequestUrl());
    writeHeaders(sb, audit.getRequestHeaders());
    writeBody(sb, audit.getRequestBody());

    writeResponse(sb, audit.getResponseStatus(), audit.getResponseStatusText());
    writeHeaders(sb, audit.getResponseHeaders());
    writeBody(sb, audit.getResponseBody());
  }

  private void writeRequest(StringBuilder sb, String method, String url) {
    sb.append(method + " " + url + "\n\n");
  }

  private void writeResponse(StringBuilder sb, int status, String statusText) {
    sb.append(status + " " + statusText + "\n\n");
  }

  private void writeHeaders(StringBuilder sb, String headers) {
    sb.append(headers);
    sb.append("\n");
  }

  private void writeBody(StringBuilder sb, String body) {
    if (StringUtils.isNotEmpty(body)) {
      sb.append(body);
      sb.append("\n");
    }
    sb.append("----\n");
  }
}
