package uk.nhs.ctp.audit.model;

import java.util.Collection;
import java.util.Map;

public interface HttpExchange {

  Map<String, ? extends Collection<String>> getHeaders();

  byte[] getBody();

}
