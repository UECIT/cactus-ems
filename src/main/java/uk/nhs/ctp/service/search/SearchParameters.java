package uk.nhs.ctp.service.search;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SearchParameters {

  private static final String SP_QUERY = "_query";
  private static final String SP_EXPERIMENTAL = "experimental";
  private static final String SP_TYPE_CODE = "type-code";
  private static final String SP_CONTEXT_VALUE = "context-value";
  private static final String SP_CONTEXT_QUANTITY = "context-quantity";
  private static final String SP_CONTEXT_RANGE = "context-range";

  private String query;
  private List<String> typeCode = new ArrayList<>();

  public MultiValueMap<String, String> toMultiValueMap() {
    LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add(SP_QUERY, query);
    if (!typeCode.isEmpty()) {
      map.put(SP_TYPE_CODE, typeCode);
    }
    return map;
  }

  public String getQuery() {
    return query;
  }

  public SearchParameters withQuery(String query) {
    this.query = query;
    return this;
  }

  public List<String> getTypeCode() {
    return typeCode;
  }

  public SearchParameters withTypeCode(String typeCode) {
    this.typeCode.add(typeCode);
    return this;
  }
}
