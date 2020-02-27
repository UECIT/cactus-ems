package uk.nhs.ctp.audit;

public interface HttpAudit {

  java.util.Date getCreatedDate();

  String getRequestMethod();

  String getRequestUrl();

  String getRequestHeaders();

  String getRequestBody();

  int getResponseStatus();

  String getResponseStatusText();

  String getResponseHeaders();

  String getResponseBody();
}
